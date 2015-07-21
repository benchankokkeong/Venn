package com.visionfederation.venn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
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

import com.visionfederation.venn.photo.Photo;
import com.visionfederation.venn.photo.PhotoAlbum;
import com.visionfederation.venn.photo.PhotoManager;
import com.visionfederation.venn.utils.StorageUtils;

public class PhotoAlbumSelectGridFragment extends Fragment implements
		AdapterView.OnItemClickListener, PhotoManager.PhotoManagerEventListener {

	private HashMap<String, String> mAlbumHashes = new HashMap<String, String>();
	private List<PhotoAlbum> mPhotoAlbums = new ArrayList<PhotoAlbum>();
	private List<Photo> mPhotos = new ArrayList<Photo>();
	private GridView mGridView;
	private PhotoAlbumGridAdapter mPhotoAlbumGridAdapter;
	private OnPhotoAlbumGridItemClickedListener mOnPhotoAlbumGridItemClickedListener;

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			SelectorActivity selectorActivity = (SelectorActivity) getActivity();
			selectorActivity.setPhotosAndAlbums(mPhotos, mPhotoAlbums);
			mPhotoAlbumGridAdapter.notifyDataSetChanged();
		}
	};

	public PhotoAlbumSelectGridFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		mPhotoAlbumGridAdapter = new PhotoAlbumGridAdapter(getActivity(),
				mPhotoAlbums);

		initialize();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(
				R.layout.fragment_photo_album_select_grid, container, false);

		getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.federation_dark_orange));

		mGridView = (GridView) view.findViewById(R.id.photoAlbumSelectGridview);
		mGridView.setAdapter(mPhotoAlbumGridAdapter);
		mGridView.setSelector(getResources().getDrawable(R.drawable.photo_album_grid_selector));
		mGridView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnPhotoAlbumGridItemClickedListener) {
			mOnPhotoAlbumGridItemClickedListener = (OnPhotoAlbumGridItemClickedListener) activity;
		} else {
			throw new ClassCastException(
					activity.toString()
							+ " must implemenet PhotoAlbumSelectGridFragment.OnPhotoAlbumGridItemClickedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mOnPhotoAlbumGridItemClickedListener = null;
	}

	@Override
	public void onDevicePhotoUrisFetched(List<String> uriStringList) {
		// TODO Auto-generated method stub

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
				if (!mAlbumHashes.containsKey(photoBucketName)) {
					mAlbumHashes.put(photoBucketName, photoBucketName);
					PhotoAlbum album = new PhotoAlbum();
					album.setName(photoBucketName);
					mPhotoAlbumGridAdapter.addAlbum(album);
					mPhotoAlbums.add(album);
				}
				mPhotos.add(photo);
			}
			mHandler.sendEmptyMessage(1);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mOnPhotoAlbumGridItemClickedListener.onPhotoAlbumClicked(position);
	}

	private void initialize() {
		mPhotoAlbums = new ArrayList<PhotoAlbum>();
		mAlbumHashes = new HashMap<String, String>();

		PhotoManager photoManager = new PhotoManager(getActivity(), this);
		photoManager.getAllDevicePhotoPropertiesAsync();

		// show progress spinner!!!
		//
		//

	}

	public interface OnPhotoAlbumGridItemClickedListener {
		public void onPhotoAlbumClicked(int photoAlbumPosition);
	}
}
