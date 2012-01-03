package com.androsz.electricsleepbeta.app.wizard;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;

import com.androsz.electricsleepbeta.R;
import com.androsz.electricsleepbeta.app.HostActivity;
import com.viewpagerindicator.TitlePageIndicator;

public abstract class WizardActivity extends HostActivity {

	private DisablablePager wizardPager;
	private TitlePageIndicator indicator;

	@Override
	protected int getContentAreaLayoutId() {
		return R.layout.activity_wizard;
	}

	@Override
	public void onBackPressed() {
		onLeftButtonClick(null);
	}

	private final class IndicatorPageChangeListener implements OnPageChangeListener {

		private int lastSettledPosition = 0;
		private int lastPosition = 0;

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			lastPosition = position;
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// if we are settled on a new page...
			if (state == ViewPager.SCROLL_STATE_IDLE && lastSettledPosition != lastPosition) {

				lastSettledPosition = lastPosition;

				onPerformWizardAction(lastPosition);

				setupNavigationButtons();
			}
		}

		@Override
		public void onPageSelected(int position) {
		}
	}

	protected abstract PagerAdapter getPagerAdapter();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		wizardPager = (DisablablePager) findViewById(R.id.wizardPager);
		wizardPager.setAdapter(getPagerAdapter());


		if (savedInstanceState != null) {
			wizardPager.setCurrentItem(savedInstanceState.getInt("child"), false);
		}
		
		indicator = (DisablablePageIndicator) findViewById(R.id.indicator);
		indicator.setFooterColor(getResources().getColor(R.color.primary1));
		indicator.setViewPager(wizardPager, 0);
		indicator.setOnPageChangeListener(new IndicatorPageChangeListener());
	}

	@Override
	protected void onResume() {
		super.onResume();

		// onPerformWizardAction();
		setupNavigationButtons();
	}

	protected abstract void onFinishWizardActivity() throws IllegalStateException;

	public void onLeftButtonClick(final View v) {

		int currentItem = getCurrentWizardIndex();
		if (currentItem != 0) {
			wizardPager.setCurrentItem(currentItem - 1);
		} else {
			super.onBackPressed();
		}
	}

	protected int getCurrentWizardIndex() {
		return wizardPager.getCurrentItem();
	}

	protected void setPagingEnabled(boolean enabled) {
	}

	protected abstract void onPrepareLastSlide();

	public void onRightButtonClick(final View v) {

		final int lastPageIndex = getPagerAdapter().getCount() - 1;
		final int displayedChildIndex = getCurrentWizardIndex();

		if (displayedChildIndex == lastPageIndex) {
			onFinishWizardActivity();
		} else {
			wizardPager.setCurrentItem(displayedChildIndex + 1);
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("child", getCurrentWizardIndex());
	}

	protected abstract void onPerformWizardAction(int focusedIndex);

	protected void setupNavigationButtons() {
		final Button leftButton = (Button) findViewById(R.id.leftButton);
		final Button rightButton = (Button) findViewById(R.id.rightButton);
		final int lastChildIndex = getPagerAdapter().getCount() - 1;
		final int displayedChildIndex = getCurrentWizardIndex();
		if (displayedChildIndex > -1 && displayedChildIndex < lastChildIndex) {
			leftButton.setText(R.string.back);
			rightButton.setText(R.string.next);
		} else if (displayedChildIndex == lastChildIndex) {
			leftButton.setText(R.string.back);
			rightButton.setText(R.string.finish);

			onPrepareLastSlide();
		}
	}
}
