package com.visionfederation.venn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ortiz.touch.TouchImageView;
import com.visionfederation.venn.photo.Photo;
import com.visionfederation.venn.utils.BitmapUtils;

public class PhotoViewerAdapter extends PagerAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Photo> mPhotos = new ArrayList<Photo>();
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			LoadBitMapTask loadBitMapTask = (LoadBitMapTask) inputMessage.obj;
			if (loadBitMapTask != null
					&& !loadBitMapTask.getSyncObject().getIsCancelPending()) {
				if (loadBitMapTask.getIsFinal()) {
					loadBitMapTask.getSyncObject().setCancelPending();
				}
				Bitmap bitmap = loadBitMapTask.getBitmap();
				ImageView imageView = loadBitMapTask.getLowResView();
				if (loadBitMapTask.getIsFinal()) {
					imageView.setVisibility(View.GONE);

					TouchImageView touchImageView = loadBitMapTask
							.getHighResView();
					if (touchImageView != null) {
						touchImageView.setVisibility(View.VISIBLE);
						touchImageView.setImageBitmap(bitmap);
						loadBitMapTask.releaseViews();
					}

				} else {

					if (imageView != null) {
						imageView.setImageBitmap(bitmap);
					}
				}
			}
		}
	};

	public PhotoViewerAdapter(Context context, List<Photo> photos) {
		mContext = context;
		mPhotos = photos;

		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mPhotos.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		final View view = mInflater.inflate(R.layout.view_photo_viewer_item,
				null);

		final ImageView loResImageView = (ImageView) view
				.findViewById(R.id.loResImageView);
		final int itemPosition = position;
		loResImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		final TouchImageView hiResImageView = (TouchImageView) view
				.findViewById(R.id.hiResImageView);

		Display display = ((WindowManager) mContext
				.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();

		final int orientation = display.getRotation();

		ViewTreeObserver viewTreeObserver = loResImageView
				.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver
					.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {
							loResImageView.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);

							int width;
							int height;

							if (orientation == Surface.ROTATION_90
									|| orientation == Surface.ROTATION_270) {
								width = loResImageView.getMeasuredWidth();
								height = loResImageView.getMeasuredHeight();
							} else {
								width = loResImageView.getMeasuredWidth();
								height = loResImageView.getMeasuredHeight();
							}

							loResImageView.setScaleType(ScaleType.FIT_CENTER);

							Uri imageUri = mPhotos.get(itemPosition).getUri();
							if (imageUri != null) {
								SyncObject syncObject = new SyncObject();
								loadTempBitmap(imageUri, loResImageView,
										hiResImageView, width, height,
										syncObject);
								loadFullBitmap(imageUri, loResImageView,
										hiResImageView, width, height,
										syncObject);
							}
						}

					});
		}
		((ViewPager) container).addView(view, 0);
		return view;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	public void addImage(Photo photo) {
		mPhotos.add(photo);
	}

	private void loadTempBitmap(Uri imageUri, ImageView loResImageView,
			TouchImageView hiResImageView, int width, int height,
			SyncObject syncObject) {
		LoadBitmapRunnable loadBitmapRunnable = new LoadBitmapRunnable(
				imageUri, loResImageView, hiResImageView, width, height, 20,
				syncObject, false);
		new Thread(loadBitmapRunnable).start();
	}

	private void loadFullBitmap(Uri imageUri, ImageView loResImageView,
			TouchImageView hiResImageView, int width, int height,
			SyncObject syncObject) {
		LoadBitmapRunnable loadBitmapRunnable = new LoadBitmapRunnable(
				imageUri, loResImageView, hiResImageView, width, height, 2,
				syncObject, true);
		new Thread(loadBitmapRunnable).start();
	}

	class LoadBitMapTask {
		private TouchImageView hiResView;
		private ImageView loResView;
		private Bitmap bitmap;
		private SyncObject syncObject;
		private boolean isFinal;

		LoadBitMapTask(ImageView loResView, TouchImageView hiResView,
				Bitmap bitmap, SyncObject syncObject, boolean isFinal) {
			this.hiResView = hiResView;
			this.loResView = loResView;
			this.bitmap = bitmap;
			this.syncObject = syncObject;
			this.isFinal = isFinal;
		}

		protected ImageView getLowResView() {
			return this.loResView;
		}

		protected TouchImageView getHighResView() {
			return this.hiResView;
		}

		protected Bitmap getBitmap() {
			return this.bitmap;
		}

		protected SyncObject getSyncObject() {
			return this.syncObject;
		}

		protected boolean getIsFinal() {
			return this.isFinal;
		}

		protected void releaseViews() {
			this.hiResView = null;
			this.loResView = null;
		}
	}

	class SyncObject {
		private boolean cancelPending = false;

		synchronized void setCancelPending() {
			cancelPending = true;
		}

		synchronized boolean getIsCancelPending() {
			return cancelPending;
		}
	}

	class LoadBitmapRunnable implements Runnable {
		private final Uri imageUri;
		private final TouchImageView hiResView;
		private final ImageView loResView;
		private final int width;
		private final int height;
		private final int quality;
		private final SyncObject syncObject;
		private final boolean isFinal;

		LoadBitmapRunnable(Uri imageUri, ImageView loResView,
				TouchImageView hiResView, int width, int height, int quality,
				SyncObject syncObject, boolean isFinal) {
			this.imageUri = imageUri;
			this.hiResView = hiResView;
			this.loResView = loResView;
			this.width = width;
			this.height = height;
			this.quality = quality;
			this.syncObject = syncObject;
			this.isFinal = isFinal;
		}

		@Override
		public void run() {
			Bitmap imageBitmap;
			try {
				imageBitmap = BitmapUtils.rotateAndScaleBitmap(this.imageUri,
						mContext, this.width, this.height, this.quality,
						BitmapUtils.getBitmapOrientation(this.imageUri,
								mContext));

				Message message = mHandler.obtainMessage();
				message.obj = new LoadBitMapTask(this.loResView,
						this.hiResView, imageBitmap, this.syncObject,
						this.isFinal);
				mHandler.sendMessage(message);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}