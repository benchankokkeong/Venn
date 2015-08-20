package com.visionfederation.venn;

import android.app.Activity;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntroductionActivity extends Activity {

	private TextView mIntroDoneTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduction);

		final LinearLayout firstIndicatorLayout = (LinearLayout) findViewById(R.id.firstIndicatorLayout);
		final LinearLayout secondIndicatorLayout = (LinearLayout) findViewById(R.id.secondIndicatorLayout);
		final LinearLayout thirdIndicatorLayout = (LinearLayout) findViewById(R.id.thirdIndicatorLayout);

		final Activity thisActivity = this;

		mIntroDoneTextView = (TextView) findViewById(R.id.introDoneTextView);
		mIntroDoneTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				thisActivity.finish();
			}
		});


		ViewPager viewPager = (ViewPager) findViewById(R.id.introViewPager);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					firstIndicatorLayout.setVisibility(View.VISIBLE);
					secondIndicatorLayout.setVisibility(View.GONE);
					thirdIndicatorLayout.setVisibility(View.GONE);
					mIntroDoneTextView.setVisibility(View.INVISIBLE);
					break;
				case 1:
					firstIndicatorLayout.setVisibility(View.GONE);
					secondIndicatorLayout.setVisibility(View.VISIBLE);
					thirdIndicatorLayout.setVisibility(View.GONE);
					if (mIntroDoneTextView.getVisibility() == View.VISIBLE) {
						animateDoneButtonExit();
					}
					mIntroDoneTextView.setVisibility(View.INVISIBLE);
					break;
				case 2:
					firstIndicatorLayout.setVisibility(View.GONE);
					secondIndicatorLayout.setVisibility(View.GONE);
					thirdIndicatorLayout.setVisibility(View.VISIBLE);
					animateDoneButtonEntry();
					mIntroDoneTextView.setVisibility(View.VISIBLE);
					break;
				default:
					firstIndicatorLayout.setVisibility(View.GONE);
					secondIndicatorLayout.setVisibility(View.GONE);
					thirdIndicatorLayout.setVisibility(View.GONE);
					mIntroDoneTextView.setVisibility(View.VISIBLE);
				}
			}

		});
		FragmentPagerAdapter pageAdapter = new IntroPagerAdapter(
				getFragmentManager());
		viewPager.setAdapter(pageAdapter);
	}

	private void animateDoneButtonEntry() {
		Animation bottomUp = AnimationUtils.loadAnimation(this,
				R.anim.menu_enter_bottom);
		mIntroDoneTextView.startAnimation(bottomUp);
	}

	private void animateDoneButtonExit() {
		Animation bottomDown = AnimationUtils.loadAnimation(this,
				R.anim.menu_exit_bottom);
		mIntroDoneTextView.startAnimation(bottomDown);
	}
}
