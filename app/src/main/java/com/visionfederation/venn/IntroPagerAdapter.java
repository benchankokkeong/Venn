package com.visionfederation.venn;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

public class IntroPagerAdapter extends FragmentPagerAdapter {

	private static final int introPagesCount = 3;

	public IntroPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public int getCount() {
		return introPagesCount;
	}

	@Override
	public Fragment getItem(int position) {
		Log.d("IntroPagerAdapter.getItem",
				">>>>>>>>position: " + String.valueOf(position));
		switch (position) {
		case 0:
			return IntroductionFirstFragment.newInstance();
		case 1:
			return IntroductionSecondFragment.newInstance();
		default:
			return IntroductionThirdFragment.newInstance();
		}
	}
}
