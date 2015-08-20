package com.visionfederation.venn;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IntroductionFirstFragment extends Fragment {

	public static IntroductionFirstFragment newInstance() {
		IntroductionFirstFragment fragmentFirst = new IntroductionFirstFragment();
		return fragmentFirst;
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
				R.layout.fragment_introduction_first, container, false);
		return view;
	}
}
