package com.visionfederation.venn;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ViewerActivity extends Activity implements
		PhotoGridFragment.OnPhotoGridItemClickedListener {

	private static final String PHOTO_GRID_FRAGMENT = "PHOTO_GRID_FRAGMENT";

	PhotoGridFragment mPhotoGridFragment;
	PhotoViewerFragment mPhotoViewerFragment;
	private List<String> mPhotoUriStringList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewer);
		initialize();
	}

	private void initialize() {
		String action = getIntent().getAction();
		if (action == Const.ACTION_REFRESH_PHOTO_SET) {
			showPhotoGrid(getIntent().getStringArrayListExtra(
					Const.FIELD_URI_STRING_LIST));
		}
	}

	private void showPhotoGrid(List<String> photoSetUriStrings) {
		mPhotoUriStringList = new ArrayList<String>();
		if (photoSetUriStrings != null & !photoSetUriStrings.isEmpty()) {
			for (String string : photoSetUriStrings) {
				mPhotoUriStringList.add(string);
			}
		}

		if (mPhotoGridFragment == null) {
			mPhotoGridFragment = new PhotoGridFragment();
		}

		Bundle bundle = new Bundle();
		bundle.putStringArrayList(Const.FIELD_PHOTO_GRID_LIST,
				(ArrayList<String>) photoSetUriStrings);
		mPhotoGridFragment.setArguments(bundle);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		fragmentTransaction.replace(R.id.photoGridFragment, mPhotoGridFragment,
				PHOTO_GRID_FRAGMENT);

		fragmentTransaction.commit();
	}

	@Override
	public void onPhotoClicked(int position) {
		showPhotoViewerAtPosition(position);
	}

	private void showPhotoViewerAtPosition(int position) {
		Bundle bundle = new Bundle();
		bundle.putInt(Const.FIELD_PHOTO_POSITION, position);
		bundle.putStringArrayList(Const.FIELD_PHOTO_GRID_LIST,
				(ArrayList<String>) mPhotoUriStringList);

		mPhotoViewerFragment = new PhotoViewerFragment();
		mPhotoViewerFragment.setArguments(bundle);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.photoViewerFragment,
				mPhotoViewerFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

	}
}
