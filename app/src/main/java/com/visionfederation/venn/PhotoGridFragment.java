package com.visionfederation.venn;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import com.visionfederation.venn.utils.UriUtils;

public class PhotoGridFragment extends Fragment implements
		AdapterView.OnItemClickListener {

	private PhotoGridAdapter mPhotoGridAdapter;
	private List<Photo> mPhotos = new ArrayList<Photo>();
	private OnPhotoGridItemClickedListener mOnPhotoGridItemClickedListener;
	private GridView mGridView;
	private ProgressDialog mProgressDialog;
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			for (Photo photo : mPhotos) {
				mPhotoGridAdapter.addImage(photo);
			}
			mPhotoGridAdapter.notifyDataSetChanged();
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
		}
	};

	public PhotoGridFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		mPhotoGridAdapter = new PhotoGridAdapter(getActivity(), mPhotos, 100,
				100);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_photo_grid,
				container, false);

		mGridView = (GridView) view.findViewById(R.id.photoGridview);
		mGridView.setAdapter(mPhotoGridAdapter);
		mGridView.setOnItemClickListener(this);

		// initialize();
		initializeAsync();

		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.show();

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnPhotoGridItemClickedListener) {
			mOnPhotoGridItemClickedListener = (OnPhotoGridItemClickedListener) activity;
		} else {
			throw new ClassCastException(
					activity.toString()
							+ " must implemenet PhotoGridFragment.OnPhotoGridItemClickedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mOnPhotoGridItemClickedListener = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d("PhotoGridFragment.onItemClick", ">>>>>>>Position clicked: "
				+ String.valueOf(position));
		mOnPhotoGridItemClickedListener.onPhotoClicked(position);
	}

	private void initializeAsync() {
		InitializePhotoGridRunnable initializePhotoGridRunnable = new InitializePhotoGridRunnable();
		new Thread(initializePhotoGridRunnable).start();
	}

	class InitializePhotoGridRunnable implements Runnable {

		@Override
		public void run() {
			mPhotos = new ArrayList<Photo>();
			List<String> photoUriStringList = getArguments()
					.getStringArrayList(Const.FIELD_PHOTO_GRID_LIST);
			if (photoUriStringList != null && !photoUriStringList.isEmpty()) {
				for (String uriString : photoUriStringList) {
					Photo photo = new Photo();
					photo.setUri(UriUtils.getUriFromString(uriString));
					mPhotos.add(photo);
				}
			}
			mHandler.sendEmptyMessage(0);
		}

	}

	/*
	 * private void initialize() { mPhotos = new ArrayList<Photo>();
	 * List<String> photoUriStringList = getArguments().getStringArrayList(
	 * Const.FIELD_PHOTO_GRID_LIST); if (photoUriStringList != null &&
	 * !photoUriStringList.isEmpty()) { for (String uriString :
	 * photoUriStringList) { Photo photo = new Photo();
	 * photo.setUri(UriUtils.getUriFromString(uriString)); mPhotos.add(photo);
	 * mPhotoGridAdapter.addImage(photo); }
	 * mPhotoGridAdapter.notifyDataSetChanged(); } }
	 */

	public interface OnPhotoGridItemClickedListener {
		public void onPhotoClicked(int photoPosition);
	}
}
