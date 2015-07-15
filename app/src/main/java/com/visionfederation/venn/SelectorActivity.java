package com.visionfederation.venn;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.visionfederation.venn.photo.Photo;
import com.visionfederation.venn.photo.PhotoAlbum;

public class SelectorActivity extends Activity implements
        PhotoAlbumSelectGridFragment.OnPhotoAlbumGridItemClickedListener {

    private List<PhotoAlbum> mPhotoAlbums = new ArrayList<PhotoAlbum>();
    private List<Photo> mPhotos = new ArrayList<Photo>();
    private static final String FRAGMENT_PHOTO_SELECTOR = "FRAGMENT_PHOTO_SELECTOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        initialize();
    }

    private void initialize() {
        mPhotoAlbums = new ArrayList<PhotoAlbum>();
        mPhotos = new ArrayList<Photo>();

        String action = getIntent().getAction();
        if (action == Const.ACTION_LOAD_PHOTO_SELECTOR) {
            PhotoSelectGridFragment photoSelectGridFragment = new PhotoSelectGridFragment();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.photoSelectorFragment,
                    photoSelectGridFragment);
            fragmentTransaction.commit();
        } else if (action == Const.ACTION_LOAD_PHOTO_ALBUM_SELECTOR) {
            PhotoAlbumSelectGridFragment photoAlbumSelectGridFragment = new PhotoAlbumSelectGridFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.photoSelectorFragment,
                    photoAlbumSelectGridFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        PhotoSelectGridFragment photoSelectGridFragment = (PhotoSelectGridFragment) fragmentManager.findFragmentByTag(FRAGMENT_PHOTO_SELECTOR);
        if (photoSelectGridFragment != null) {
            if (!photoSelectGridFragment.isHandlingBackButtonPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    protected void setPhotosAndAlbums(List<Photo> photos,
                                      List<PhotoAlbum> photoAlbums) {
        mPhotos = photos;
        mPhotoAlbums = photoAlbums;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPhotoAlbumClicked(int photoAlbumPosition) {
        PhotoAlbum album = mPhotoAlbums.get(photoAlbumPosition);
        if (album != null) {
            List<Photo> albumPhotos = new ArrayList<Photo>();
            String albumName = album.getName();
            for (Photo photo : mPhotos) {
                String photoAlbumBucket = photo.getPhotoBucketName();
                if (photoAlbumBucket != null
                        && photoAlbumBucket.equals(albumName)) {
                    albumPhotos.add(photo);
                }
            }

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Const.FIELD_PHOTO_LIST,
                    (ArrayList<? extends Parcelable>) albumPhotos);

            PhotoSelectGridFragment photoSelectGridFragment = new PhotoSelectGridFragment();
            photoSelectGridFragment.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();

            fragmentTransaction.replace(R.id.photoSelectorFragment,
                    photoSelectGridFragment, FRAGMENT_PHOTO_SELECTOR);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
