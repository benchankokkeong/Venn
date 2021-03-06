package com.visionfederation.venn;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.visionfederation.venn.utils.OSUtils;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		getWindow().setStatusBarColor(getResources().getColor(R.color.federation_darkgray_status));

		TextView versionTextView = (TextView) findViewById(R.id.versionTextView);
		if (versionTextView != null) {
			String defaultVersionString = (String) versionTextView.getText();
			versionTextView.setText(defaultVersionString
					+ OSUtils.getVersionName(this));

		}
	}
}
