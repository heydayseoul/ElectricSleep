package com.androsz.electricsleepbeta.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class DecimalSeekBar extends SeekBar {

	private static java.text.NumberFormat nf = java.text.NumberFormat.getInstance();

	public final static float PRECISION = 100f;

	static {
		nf.setGroupingUsed(false);
		nf.setMinimumFractionDigits(0);
		nf.setMaximumFractionDigits(("" + (int) Math.pow(PRECISION, 0.5)).length());
		nf.setMinimumFractionDigits(("" + (int) Math.pow(PRECISION, 0.5)).length());
	}

	public DecimalSeekBar(final Context context) {
		this(context, null);
	}

	public DecimalSeekBar(final Context context, final AttributeSet as) {
		this(context, as, 0);
	}

	public DecimalSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setMax(Math.round((10 * PRECISION)));
	}

	public float getFloatProgress() {
		return super.getProgress() / PRECISION;
	}

	public void setMax(final float max) {
		super.setMax(Math.round((max * PRECISION)));
	}

	@Override
	public void setMax(final int max) {
		super.setMax(Math.round((max * PRECISION)));
	}

	public float getFloatMax() {
		return getMax() / PRECISION;
	}

	public void setProgress(final float progress) {
		super.setProgress(Math.round(progress * PRECISION));
	}

	@Override
	public void setProgress(final int progress) {
		super.setProgress(Math.round((float) progress / PRECISION));
	}
}
