package com.visionfederation.venn.photo;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
	private Uri mUri = null;
	private Bitmap mBitmapCache = null;
	private long mId = -1;
	private String mPhotoBucketName = "";
	private boolean mIsSelected = false;

	public Photo() {

	}

	private Photo(Parcel in) {
		mUri = Uri.parse(in.readString());
		mId = in.readLong();
		mPhotoBucketName = in.readString();
		mIsSelected = in.readByte() != 0;
	}

	public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {

		@Override
		public Photo createFromParcel(Parcel source) {
			return new Photo(source);
		}

		@Override
		public Photo[] newArray(int size) {
			return new Photo[size];
		}
	};

	public void setUri(Uri uri) {
		mUri = uri;
	}

	public Uri getUri() {
		return mUri;
	}

	public void cacheBitmap(Bitmap bitmap) {
		mBitmapCache = bitmap;
	}

	public Bitmap getBitmapCache() {
		return mBitmapCache;
	}

	public void setId(long id) {
		mId = id;
	}

	public long getId() {
		return mId;
	}

	public String getPhotoBucketName() {
		return mPhotoBucketName;
	}

	public void setPhotoBucketName(String name) {
		mPhotoBucketName = name;
	}

	public void setIsSelected(boolean isSelected) {
		mIsSelected = isSelected;
	}

	public boolean getIsSelected() {
		return mIsSelected;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mUri.toString());
		out.writeLong(mId);
		out.writeString(mPhotoBucketName);
		out.writeByte((byte) (mIsSelected ? 1 : 0));
	}
}
