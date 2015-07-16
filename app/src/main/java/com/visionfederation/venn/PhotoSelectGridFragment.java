package com.visionfederation.venn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.visionfederation.venn.photo.Photo;
import com.visionfederation.venn.photo.PhotoManager;
import com.visionfederation.venn.utils.StorageUtils;
import com.visionfederation.venn.utils.UriUtils;

public class PhotoSelectGridFragment extends Fragment implements
        AdapterView.OnItemClickListener, PhotoManager.PhotoManagerEventListener {

    private PhotoGridAdapter mPhotoGridAdapter;
    private List<Photo> mPhotos = new ArrayList<Photo>();
    private List<Photo> mSelectedPhotos = new ArrayList<Photo>();
    private List<Integer> mSelectedPositions = new ArrayList<Integer>();
    private GridView mGridView;
    private ViewGroup mViewGroup;
    private Toolbar mToolbar;
    private ImageButton mCloseButton;
    private ImageButton mDeselectAllBackButton;
    private ImageButton mAcceptSelectedPhotosButton;
    private TextView mSelectedPhotosCounterTextView;
    private boolean mIsSelectionMode = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            mPhotoGridAdapter.notifyDataSetChanged();
        }
    };

    public PhotoSelectGridFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mPhotos = getArguments().getParcelableArrayList(Const.FIELD_PHOTO_LIST);
        mPhotoGridAdapter = new PhotoGridAdapter(getActivity(), mPhotos, 100,
                100);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photo_select_grid,
                container, false);

        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.federation_black));

        mGridView = (GridView) view.findViewById(R.id.photoSelectGridview);
        mGridView.setAdapter(mPhotoGridAdapter);
        mGridView.setOnItemClickListener(this);

        mViewGroup = container;

        mToolbar = (Toolbar) view.findViewById(R.id.toolbarPhotoSelector);
        mCloseButton = (ImageButton) view.findViewById(R.id.imageButtonClosePhotoSelector);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment();
            }
        });

        mDeselectAllBackButton = (ImageButton) view.findViewById(R.id.imageButtonDeselectPhotoSelector);
        mDeselectAllBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableSelectionMode();
                removeAllSelectedPhotos();
            }
        });

        mAcceptSelectedPhotosButton = (ImageButton) view.findViewById(R.id.imageButtonAcceptSelectedPhotos);
        mAcceptSelectedPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableSelectionMode();
                acceptSelectedPhotos();
            }
        });

        mSelectedPhotosCounterTextView = (TextView) view.findViewById(R.id.textViewSelectedCounter);
        mSelectedPhotosCounterTextView.setText(String.valueOf(mSelectedPhotos.size()));

        ImageButton selectYesButton = (ImageButton) view
                .findViewById(R.id.selectYesButton);
        selectYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptSelectedPhotos();
            }
        });

        ImageButton deselectAllButton = (ImageButton) view
                .findViewById(R.id.deselectAllButton);
        deselectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllSelectedPhotos();
            }
        });

        initialize();

        return view;
    }

    public boolean isHandlingBackButtonPressed() {
        if (mIsSelectionMode) {
            disableSelectionMode();
            removeAllSelectedPhotos();
            return true;
        }
        return false;
    }

    private void enableSelectionMode() {
        if (!mIsSelectionMode) {
            mIsSelectionMode = true;
            enableSelectionToolbar();
        }
    }

    private void disableSelectionMode() {
        mIsSelectionMode = false;
        disableSelectionToolbar();
    }

    private void enableSelectionToolbar() {
        mDeselectAllBackButton.setVisibility(View.VISIBLE);
        mAcceptSelectedPhotosButton.setVisibility(View.VISIBLE);
        mSelectedPhotosCounterTextView.setVisibility(View.VISIBLE);
        mCloseButton.setVisibility(View.INVISIBLE);
    }

    private void disableSelectionToolbar() {
        mDeselectAllBackButton.setVisibility(View.INVISIBLE);
        mAcceptSelectedPhotosButton.setVisibility(View.INVISIBLE);
        mSelectedPhotosCounterTextView.setVisibility(View.INVISIBLE);
        mCloseButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Photo selectedPhoto = mPhotos.get(position);
        if (mSelectedPhotos.contains(selectedPhoto)) {
            removeSelectedPhoto(selectedPhoto, position, view);
        } else {
            enableSelectionMode();
            addSelectedPhoto(selectedPhoto, position, view);
        }
    }

    private void addSelectedPhoto(Photo selectedPhoto, int position, View view) {
        mSelectedPhotos.add(selectedPhoto);
        mSelectedPositions.add(position);
        setPhotoSelected(position, true, view);
        mSelectedPhotosCounterTextView.setText(String.valueOf(mSelectedPhotos.size()));
    }

    private void removeSelectedPhoto(Photo selectedPhoto, int position, View view) {
        mSelectedPhotos.remove(selectedPhoto);
        for (Integer i : mSelectedPositions) {
            if (i == position) {
                mSelectedPhotos.remove(i);
            }
        }
        setPhotoSelected(position, false, view);
        mSelectedPhotosCounterTextView.setText(String.valueOf(mSelectedPhotos.size()));
    }

    private void removeAllSelectedPhotos() {
        if (!mSelectedPhotos.isEmpty()) {
            mSelectedPhotos = new ArrayList<Photo>();
            for (Integer i : mSelectedPositions) {
                View itemView = mPhotoGridAdapter.getView(i, null,
                        mViewGroup);
                setPhotoSelected(i, false, itemView);
            }
            mSelectedPositions = new ArrayList<Integer>();
            mPhotoGridAdapter.notifyDataSetChanged();
        }
    }

    private void acceptSelectedPhotos() {
        if (!mSelectedPhotos.isEmpty()) {
            List<String> photoSetUriStrings = new ArrayList<String>();
            for (Photo photo : mSelectedPhotos) {
                photoSetUriStrings.add(photo.getUri().toString());
            }

            Intent intent = new Intent();
            intent.putStringArrayListExtra(Const.FIELD_URI_STRING_LIST,
                    (ArrayList<String>) photoSetUriStrings);
            getActivity().setResult(Const.RESULT_SELECTED_PHOTOS,
                    intent);
            getActivity().finish();
        }
    }

    private void setPhotoSelected(int position, boolean isSelected, View view) {
        Photo photo = mPhotos.get(position);
        photo.setIsSelected(isSelected);

        if (mPhotoGridAdapter != null) {
            mPhotoGridAdapter.changePhotoSelectedState(position, isSelected,
                    view);
        }
    }

    private void initialize() {

        mSelectedPhotos = new ArrayList<Photo>();
        mSelectedPositions = new ArrayList<Integer>();
        mPhotoGridAdapter.notifyDataSetChanged();

        // PhotoManager photoManager = new PhotoManager(getActivity(), this);
        // photoManager.getAllDevicePhotoPropertiesAsync();

        // show progress spinner!!!
        //
        //

    }

    private void closeFragment() {
        getActivity().getFragmentManager().popBackStack();
    }

    @Override
    public void onDevicePhotoUrisFetched(List<String> uriStringList) {
        if (uriStringList != null && !uriStringList.isEmpty()) {
            for (String uriString : uriStringList) {
                Photo photo = new Photo();
                photo.setUri(UriUtils.getUriFromString(uriString));
                mPhotos.add(photo);
                mPhotoGridAdapter.addImage(photo);
            }
            mHandler.sendEmptyMessage(1);
        }
    }

    @Override
    public void onDevicePhotoPropertiesFetched(
            List<HashMap<String, String>> photoPropertiesList) {
        if (photoPropertiesList != null && !photoPropertiesList.isEmpty()) {
            for (HashMap<String, String> photoPropertiesMap : photoPropertiesList) {
                Photo photo = new Photo();
                String photoIdString = (String) photoPropertiesMap
                        .get(StorageUtils.Const.PHOTO_ID);
                photo.setId(Long.valueOf(photoIdString));
                photo.setUri(StorageUtils.getUriFromStorageId(photoIdString));
                String photoBucketName = (String) photoPropertiesMap
                        .get(StorageUtils.Const.PHOTO_BUCKET_NAME);
                photo.setPhotoBucketName(photoBucketName);

                mPhotos.add(photo);
                mPhotoGridAdapter.addImage(photo);
            }
            mHandler.sendEmptyMessage(1);
        }
    }
}
