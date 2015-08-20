package com.visionfederation.venn;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IntroductionSecondFragment extends Fragment {

	public static IntroductionSecondFragment newInstance() {
		IntroductionSecondFragment fragmentSecond = new IntroductionSecondFragment();
		return fragmentSecond;
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
				R.layout.fragment_introduction_second, container, false);
		return view;
	}
}
