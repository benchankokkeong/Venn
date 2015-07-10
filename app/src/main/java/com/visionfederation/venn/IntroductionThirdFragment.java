package com.visionfederation.venn;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IntroductionThirdFragment extends Fragment {
	public IntroductionThirdFragment() {

	}

	public static IntroductionThirdFragment newInstance() {
		IntroductionThirdFragment fragmentThird = new IntroductionThirdFragment();
		return fragmentThird;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = (View) inflater.inflate(
				R.layout.fragment_introduction_third, container, false);
		return view;
	}
}
