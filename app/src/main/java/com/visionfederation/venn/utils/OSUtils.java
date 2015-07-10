package com.visionfederation.venn.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class OSUtils {
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionName;
	}

	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionCode;
	}

	public static String getFullVersion(Context context) {
		String versionName = getVersionName(context);
		String versionCode = String.valueOf(getVersionCode(context));
		return versionName + "." + versionCode;
	}
}
