package com.visionfederation.venn;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visionfederation.venn.photo.Photo;
import com.visionfederation.venn.utils.UriUtils;

public class PhotoViewerFragment extends Fragment {
	private ViewPager mViewPager;
	private PhotoViewerAdapter mPhotoViewerAdapter;
	private List<Photo> mPhotosList = new ArrayList<Photo>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mViewPager = (ViewPager) inflater.inflate(
				R.layout.fragment_photo_viewer, container, false);
		mPhotoViewerAdapter = new PhotoViewerAdapter(getActivity()
				.getApplicationContext(), mPhotosList);
		mViewPager.setAdapter(mPhotoViewerAdapter);

		initialize();

		return mViewPager;
	}

	private void initialize() {
		mPhotosList = new ArrayList<Photo>();
		List<String> photoUriStringList = getArguments().getStringArrayList(
				Const.FIELD_PHOTO_GRID_LIST);
		if (photoUriStringList != null && !photoUriStringList.isEmpty()) {
			for (String uriString : photoUriStringList) {
				Photo photo = new Photo();
				photo.setUri(UriUtils.getUriFromString(uriString));
				mPhotosList.add(photo);
			}
			int position = getArguments().getInt(Const.FIELD_PHOTO_POSITION, 0);
			showPhotoAtPosition(position);
		}
	}

	private void showPhotoAtPosition(int position) {
		for (Photo photo : mPhotosList) {
			mPhotoViewerAdapter.addImage(photo);
		}
		mPhotoViewerAdapter.notifyDataSetChanged();
		mViewPager.setCurrentItem(position);
	}
}
