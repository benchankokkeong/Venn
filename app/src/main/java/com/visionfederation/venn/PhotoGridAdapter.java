package com.visionfederation.venn;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.visionfederation.venn.photo.Photo;
import com.visionfederation.venn.utils.BitmapUtils;
import com.visionfederation.venn.utils.Converter;

public class PhotoGridAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	private final Context mContext;
	private List<Photo> mPhotos = new ArrayList<Photo>();
	private Bitmap mPlaceHolderBitmap;
	private int mBitmapWidth;
	private int mBitmapHeight;
	private LruCache<String, Bitmap> mMemoryCache;

	public PhotoGridAdapter(Context context, List<Photo> photos,
			int bitmapWidth, int bitmapHeight) {
		super();

		mContext = context;
		mPhotos = photos;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};

		mBitmapWidth = Converter.getPixels(bitmapWidth, mContext);
		mBitmapHeight = Converter.getPixels(bitmapHeight, mContext);
		mPlaceHolderBitmap = BitmapUtils.createPlaceholderBitmap(mBitmapWidth,
				mBitmapHeight);
	}

	@Override
	public int getCount() {
		return mPhotos.size();
	}

	@Override
	public Object getItem(int position) {
		return mPhotos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.view_photo_grid_item, null);
		}

		loadBitmap(position, convertView);

		convertView.setClickable(false);
		convertView.setFocusable(false);
		convertView.setFocusableInTouchMode(false);
		return convertView;
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public void addImage(Photo photo) {
		mPhotos.add(photo);
	}

	public void changePhotoSelectedState(int position, boolean isSelected,
			View view) {
		Photo photo = mPhotos.get(position);
		if (photo != null) {
			photo.setIsSelected(isSelected);
			setPhotoGridItemSelectedState(isSelected, view);
		}
	}

	private void setPhotoGridItemSelectedState(boolean isSelected, View view) {
		LinearLayout selectorLayout = (LinearLayout) view
				.findViewById(R.id.photoViewSelector);
		if (selectorLayout != null) {
			if (isSelected) {
				selectorLayout.setVisibility(View.VISIBLE);
			} else {
				selectorLayout.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void loadBitmap(int position, View view) {
		SquareImageView squareImageView = (SquareImageView) view
				.findViewById(R.id.photoView);
		if (cancelPotentialWork(position, squareImageView)) {
			Photo photo = mPhotos.get(position);
			LinearLayout selectorLayout = (LinearLayout) view
					.findViewById(R.id.photoViewSelector);
			if (selectorLayout != null) {
				if (photo.getIsSelected()) {
					selectorLayout.setVisibility(View.VISIBLE);
				} else {
					selectorLayout.setVisibility(View.INVISIBLE);
				}
			}

			final String imageKey = String.valueOf(position);
			Bitmap bitmapCache = getBitmapFromMemCache(imageKey);
			// Bitmap bitmapCache = photo.getBitmapCache();
			if (bitmapCache == null) {
				final BitmapWorkerTask task = new BitmapWorkerTask(
						squareImageView);
				final AsyncDrawable asyncDrawable = new AsyncDrawable(
						mContext.getResources(), mPlaceHolderBitmap, task);
				squareImageView.setImageDrawable(asyncDrawable);
				task.execute(position);
			} else {
				squareImageView.setImageBitmap(bitmapCache);
			}
		}
	}

	public static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources resources, Bitmap bitmap,
				BitmapWorkerTask bitmapWorkerTask) {
			super(resources, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	private class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int data = 0;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			data = params[0];

			Bitmap bitmap = null;
			Photo photo = mPhotos.get(data);
			if (photo.getId() > -1) {
				bitmap = BitmapUtils.getThumbnailFromImageId(photo.getId(),
						mContext);
				if (bitmap != null) {
					bitmap = BitmapUtils.rotateBitmap(bitmap, BitmapUtils
							.getOrientation(photo.getUri(), mContext));
				}
			} else {
				bitmap = BitmapUtils.getThumbnailFromImageUri(mPhotos.get(data)
						.getUri(), mContext);
				if (bitmap != null) {
					bitmap = BitmapUtils.rotateBitmap(bitmap, BitmapUtils
							.getOrientation(photo.getUri(), mContext));
				}
				/*
				 * Uri imageUri = mPhotos.get(data).getUri(); if (imageUri !=
				 * null) { bitmap = BitmapUtils.rotateAndScaleBitmap(imageUri,
				 * mContext, mBitmapWidth, mBitmapHeight, 14,
				 * BitmapUtils.getOrientation(imageUri, mContext)); }
				 */
			}

			if (bitmap != null) {
				addBitmapToMemoryCache(String.valueOf(data), bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
					/*
					 * Photo photo = mPhotos.get(data); if (photo != null) {
					 * photo.cacheBitmap(bitmap); }
					 */
				}
			}
		}
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	public static boolean cancelPotentialWork(int data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final int bitmapData = bitmapWorkerTask.data;
			if (bitmapData != data) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was
		// cancelled
		return true;
	}
}
