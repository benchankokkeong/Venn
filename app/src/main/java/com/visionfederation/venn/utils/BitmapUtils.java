package com.visionfederation.venn.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class BitmapUtils {
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int requiredWidth, int requiredHeight, int requiredInSampleSize) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > requiredHeight || width > requiredWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > requiredHeight
					&& (halfWidth / inSampleSize) > requiredWidth) {
				inSampleSize *= requiredInSampleSize;
			}
		}

		return inSampleSize;
	}

	public static Bitmap getBitmapFromUri(Uri uri, Context context) {
		Bitmap bitmap = null;
		ContentResolver contentResolver = context.getContentResolver();
		try {
			bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public static Bitmap createPlaceholderBitmap(int width, int height) {
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap placeholderBitmap = Bitmap.createBitmap(width, height, config);
		placeholderBitmap.eraseColor(android.graphics.Color.DKGRAY);
		return placeholderBitmap;
	}

	public static Bitmap rotateAndScaleBitmap(Uri uri, Context context,
			int requiredWidth, int requiredHeight, int inSampleSize,
			int orientation) throws IOException {
		Bitmap bitmap = performBitmapScaling(uri, context, requiredWidth,
				requiredHeight, inSampleSize);
		return performBitmapRotation(bitmap, orientation);
	}

	public static Bitmap scaleBitmap(Uri uri, Context context,
			int requiredWidth, int requiredHeight, int inSampleSize)
			throws IOException {
		return performBitmapScaling(uri, context, requiredWidth,
				requiredHeight, inSampleSize);
	}

	private static Bitmap performBitmapScaling(Uri uri, Context context,
			int requiredWidth, int requiredHeight, int inSampleSize)
			throws IOException {
		ContentResolver contentResolver = context.getContentResolver();

		InputStream inputStream = contentResolver.openInputStream(uri);
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
		inputStream.close();

		inputStream = contentResolver.openInputStream(uri);
		bitmapOptions.inSampleSize = calculateInSampleSize(bitmapOptions,
				requiredWidth, requiredHeight, inSampleSize);
		bitmapOptions.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,
				bitmapOptions);
		inputStream.close();
		return bitmap;
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
		return performBitmapRotation(bitmap, orientation);
	}

	private static Bitmap performBitmapRotation(Bitmap bitmap, int orientation) {
		Matrix matrix = new Matrix();

		switch (orientation) {
		case ExifInterface.ORIENTATION_NORMAL:
			return bitmap;
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
			matrix.setScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			matrix.setRotate(180);
			break;
		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
			matrix.setRotate(180);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_TRANSPOSE:
			matrix.setRotate(90);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			matrix.setRotate(90);
			break;
		case ExifInterface.ORIENTATION_TRANSVERSE:
			matrix.setRotate(-90);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			matrix.setRotate(-90);
			break;
		default:
			return bitmap;
		}

		try {
			Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bitmap.recycle();
			return rotatedBitmap;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getBitmapOrientation(Uri uri, Context context) {
		ExifInterface exif;
		try {
			String filePath = getRealPathFromURI(uri, context);
			exif = new ExifInterface(filePath);
			return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ExifInterface.ORIENTATION_NORMAL;
	}

	public static int getOrientation(Uri uri, Context context) {
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
				null, null, null);

		int orientation = ExifInterface.ORIENTATION_NORMAL;

		if (cursor != null) {
			if (cursor.getCount() != 1) {
				Log.e("BitmapUtils.getOrientation", ">>>>>>no orientation!!!");
				return ExifInterface.ORIENTATION_UNDEFINED;
			}

			cursor.moveToFirst();
			orientation = cursor.getInt(0);
			cursor.close();

			if (orientation == 90) {
				orientation = ExifInterface.ORIENTATION_ROTATE_90;
			} else if (orientation == 270) {
				orientation = ExifInterface.ORIENTATION_ROTATE_270;
			} else {
				orientation = ExifInterface.ORIENTATION_NORMAL;
			}
		}
		return orientation;
	}

	private static String getRealPathFromURI(Uri contentUri, Context context) {
		String path = null;
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri,
				projection, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			path = cursor.getString(columnIndex);
			cursor.close();
		}
		return path;
	}

	public static Bitmap getThumbnailFromImageUri(Uri uri, Context context) {
		Bitmap bitmap = null;
		String[] projection = { MediaStore.Images.Media._ID };
		Cursor cursor = context.getContentResolver().query(uri, projection,
				null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.MediaColumns._ID));
			cursor.close();
			bitmap = MediaStore.Images.Thumbnails.getThumbnail(
					context.getContentResolver(), id,
					MediaStore.Images.Thumbnails.MINI_KIND, null);
		}

		cursor.close();
		return bitmap;
	}

	public static Bitmap getThumbnailFromImageId(long id, Context context) {
		return MediaStore.Images.Thumbnails.getThumbnail(
				context.getContentResolver(), id,
				MediaStore.Images.Thumbnails.MINI_KIND,
				(BitmapFactory.Options) null);
	}
}
