package com.visionfederation.venn.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class StorageUtils {
	public class Const {
		public static final String PHOTO_ID = "PHOTO_ID";
		public static final String PHOTO_BUCKET_NAME = "PHOTO_BUCKET_NAME";
	}

	public static List<String> getDeviceCameraPhotoUris(Context context) {

		List<String> deviceCameraPhotoUriList = new ArrayList<String>();
		String[] projection = { MediaStore.Images.Media._ID };

		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
				null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					/*
					 * String id = String.valueOf(cursor.getString(cursor
					 * .getColumnIndex(MediaStore.Images.Media._ID)));
					 * 
					 * String path = (Uri.withAppendedPath(
					 * MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id))
					 * .toString();
					 */

					if (path != null && !path.isEmpty()) {
						deviceCameraPhotoUriList.add(path);
					}

				} while (cursor.moveToNext());

			}
			cursor.close();
		}

		Log.d("StorageUtils.getDeviceCameraPhotoUris",
				">>>>>total photos in device: "
						+ String.valueOf(deviceCameraPhotoUriList.size()));
		return deviceCameraPhotoUriList;
	}

	public static List<HashMap<String, String>> getDeviceCameraPhotoProperties(
			Context context) {

		List<HashMap<String, String>> deviceCameraPhotoPropertiesList = new ArrayList<HashMap<String, String>>();
		String[] projection = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
				null, MediaStore.Images.Media.DATE_ADDED + " DESC");

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					HashMap<String, String> photoPropertiesMap = new HashMap<String, String>();
					String id = String.valueOf(cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media._ID)));
					if (id != null && !id.isEmpty()) {
						photoPropertiesMap.put(Const.PHOTO_ID, id);
					}

					String bucketName = cursor
							.getString(cursor
									.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
					if (bucketName != null && !bucketName.isEmpty()) {
						photoPropertiesMap.put(Const.PHOTO_BUCKET_NAME,
								bucketName);
					}
					deviceCameraPhotoPropertiesList.add(photoPropertiesMap);

				} while (cursor.moveToNext());

			}
			cursor.close();
		}

		Log.d("StorageUtils.getDeviceCameraPhotoUris",
				">>>>>total photos in device: "
						+ String.valueOf(deviceCameraPhotoPropertiesList.size()));
		return deviceCameraPhotoPropertiesList;
	}

	public static Uri getUriFromStorageId(String id) {
		return Uri.withAppendedPath(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
	}
}
