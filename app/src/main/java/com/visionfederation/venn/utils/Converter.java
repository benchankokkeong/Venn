package com.visionfederation.venn.utils;

import android.content.Context;

public class Converter {
	public static int getPixels(int dp, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}
