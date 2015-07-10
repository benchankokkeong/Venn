package com.visionfederation.venn;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;

public class SquareImageView extends ImageView {

	public SquareImageView(final Context context) {
		super(context);
		initialize();
	}

	public SquareImageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public SquareImageView(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private void initialize() {
		setScaleType(ImageView.ScaleType.CENTER_CROP);
		setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec,
			final int heightMeasureSpec) {
		final int width = getDefaultSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		setMeasuredDimension(width, width);
	}

	@Override
	protected void onSizeChanged(final int width, final int heights,
			final int oldWidth, final int oldHeight) {
		super.onSizeChanged(width, width, oldWidth, oldHeight);
	}
}
