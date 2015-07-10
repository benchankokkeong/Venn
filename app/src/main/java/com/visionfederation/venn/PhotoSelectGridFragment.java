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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

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
		final ViewGroup viewGroup = container;

		mGridView = (GridView) view.findViewById(R.id.photoSelectGridview);
		mGridView.setAdapter(mPhotoGridAdapter);
		mGridView.setOnItemClickListener(this);

		ImageButton selectYesButton = (ImageButton) view
				.findViewById(R.id.selectYesButton);
		selectYesButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
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

					/*
					 * Intent intent = new Intent(context,
					 * ViewerActivity.class);
					 * intent.setAction(Const.ACTION_REFRESH_PHOTO_SET);
					 * intent.putStringArrayListExtra
					 * (Const.FIELD_URI_STRING_LIST, (ArrayList<String>)
					 * photoSetUriStrings); startActivity(intent);
					 * currentFragment.getFragmentManager().popBackStack();
					 */
				}
			}
		});

		ImageButton deselectAllButton = (ImageButton) view
				.findViewById(R.id.deselectAllButton);
		deselectAllButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mSelectedPhotos.isEmpty()) {
					mSelectedPhotos = new ArrayList<Photo>();
					for (Integer i : mSelectedPositions) {
						View itemView = mPhotoGridAdapter.getView(i, null,
								viewGroup);
						setPhotoSelected(i, false, itemView);
					}
					mSelectedPositions = new ArrayList<Integer>();
					mPhotoGridAdapter.notifyDataSetChanged();
				}
			}
		});

		initialize();

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Photo selectedPhoto = mPhotos.get(position);
		if (mSelectedPhotos.contains(selectedPhoto)) {
			mSelectedPhotos.remove(selectedPhoto);
			for (Integer i : mSelectedPositions) {
				if (i == position) {
					mSelectedPhotos.remove(i);
				}
			}
			setPhotoSelected(position, false, view);
		} else {
			mSelectedPhotos.add(selectedPhoto);
			mSelectedPositions.add(position);
			setPhotoSelected(position, true, view);
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
				Log.d("PhotoSelectGridFragment.onDevicePhotoPropertiesFetched",
						"Bucket: " + photoBucketName);

				mPhotos.add(photo);
				mPhotoGridAdapter.addImage(photo);
			}
			mHandler.sendEmptyMessage(1);
		}
	}
}
