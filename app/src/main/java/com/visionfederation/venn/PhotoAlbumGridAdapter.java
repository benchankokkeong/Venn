package com.visionfederation.venn;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.visionfederation.venn.photo.PhotoAlbum;

public class PhotoAlbumGridAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	private final Context mContext;
	private List<PhotoAlbum> mPhotoAlbums = new ArrayList<PhotoAlbum>();

	public PhotoAlbumGridAdapter(Context context, List<PhotoAlbum> photoAlbums) {
		super();

		mContext = context;
		mPhotoAlbums = photoAlbums;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPhotoAlbums.size();
	}

	@Override
	public Object getItem(int position) {
		return mPhotoAlbums.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.view_photo_album_grid_item, null);
		}

		TextView nameTextView = (TextView) convertView
				.findViewById(R.id.photoAlbumNameTextView);
		if (nameTextView != null) {
			PhotoAlbum album = mPhotoAlbums.get(position);
			nameTextView.setText(album.getName());
			nameTextView.setClickable(false);
			nameTextView.setFocusable(false);
			nameTextView.setFocusableInTouchMode(false);
		}

		convertView.setClickable(false);
		convertView.setFocusable(false);
		convertView.setFocusableInTouchMode(false);
		return convertView;
	}

	public void addAlbum(PhotoAlbum photoAlbum) {
		mPhotoAlbums.add(photoAlbum);
	}
}
