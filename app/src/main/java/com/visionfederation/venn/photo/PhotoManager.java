package com.visionfederation.venn.photo;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.visionfederation.venn.utils.StorageUtils;

public class PhotoManager {
	private Context mContext;
	private PhotoManagerEventListener mPhotoManagerEventListener;

	public PhotoManager(Context context,
			PhotoManagerEventListener photoManagerEventListener) {
		mContext = context.getApplicationContext();
		mPhotoManagerEventListener = photoManagerEventListener;
	}

	public void getAllDevicePhotoUrisAsync() {
		GetDevicePhotoUrisRunnable getDevicePhotoUrisRunnable = new GetDevicePhotoUrisRunnable();
		new Thread(getDevicePhotoUrisRunnable).start();
	}

	public void getAllDevicePhotoPropertiesAsync() {
		GetDevicePhotoPropertiesRunnable getDevicePhotoIdsRunnable = new GetDevicePhotoPropertiesRunnable();
		new Thread(getDevicePhotoIdsRunnable).start();
	}

	public interface PhotoManagerEventListener {
		public void onDevicePhotoUrisFetched(List<String> uriStringList);

		public void onDevicePhotoPropertiesFetched(
				List<HashMap<String, String>> photoPropertiesList);
	}

	class GetDevicePhotoUrisRunnable implements Runnable {

		@Override
		public void run() {
			List<String> devicePhotoUris = StorageUtils
					.getDeviceCameraPhotoUris(mContext);
			mPhotoManagerEventListener
					.onDevicePhotoUrisFetched(devicePhotoUris);
		}

	}

	class GetDevicePhotoPropertiesRunnable implements Runnable {

		@Override
		public void run() {
			List<HashMap<String, String>> devicePhotoProperties = StorageUtils
					.getDeviceCameraPhotoProperties(mContext);
			mPhotoManagerEventListener
					.onDevicePhotoPropertiesFetched(devicePhotoProperties);
		}

	}
}
