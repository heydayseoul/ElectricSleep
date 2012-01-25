package com.androsz.electricsleepbeta.app;

import java.lang.reflect.Field;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.SupportActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.androsz.electricsleepbeta.R;
import com.androsz.electricsleepbeta.app.wizard.CalibrationWizardActivity;
import com.androsz.electricsleepbeta.app.wizard.WelcomeTutorialWizardActivity;

public abstract class HostActivity extends AnalyticActivity {

	static ColorMatrixColorFilter COLOR_FILTER;

	static {
		// COLOR_FILTER = new PorterDuffColorFilter(Color.WHITE,
		// PorterDuff.Mode.MULTIPLY);

		final ColorMatrix cm = new ColorMatrix();
		cm.setScale(4, 4, 4, 1); // tint it closer to white (too much can cause
									// transparent pixels to look too opaque)
		COLOR_FILTER = new ColorMatrixColorFilter(cm);
	}

	protected abstract int getContentAreaLayoutId();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final View root = getLayoutInflater().inflate(getContentAreaLayoutId(), null, false);
		root.setBackgroundColor(getResources().getColor(R.color.background));
		setContentView(root);

		prepareActionBar(this);
	}

	public static void prepareActionBar(SupportActivity supportActivity) {
		final ActionBar bar = supportActivity.getSupportActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(supportActivity.getResources().getColor(R.color.actionbar_background)));
		bar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_host, menu);
		for (int i = 0; i < menu.size(); i++) {
			final MenuItem mi = menu.getItem(i);
			try {
				// Do nasty reflection to access ShowAsAction...
				// TODO just use my own icons...
				Field showAsActionField = MenuItemImpl.class.getDeclaredField("mShowAsAction");
				showAsActionField.setAccessible(true);
				int showAsAction = showAsActionField.getInt(mi);

				// Only change the color if it is actually on the ActionBar.
				// Changing ones that aren't in the AB sometimes make them hard
				// to see.
				if ((showAsAction != MenuItem.SHOW_AS_ACTION_NEVER)) {
					final Drawable icon = mi.getIcon();
					if (icon != null) {
						final Drawable mutated = icon.getCurrent().mutate();
						mutated.setColorFilter(COLOR_FILTER);
						mi.setIcon(mutated);
					}
				}
			} catch (NoSuchFieldException nsfe) {
				trackEvent("mShowAsAction reflection error", 1);
			} catch (IllegalArgumentException e) {
				//do not track this. it happens a lot.
				//trackEvent("mShowAsAction reflection error", 2);
			} catch (IllegalAccessException e) {
				trackEvent("mShowAsAction reflection error", 3);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return (true);
		case R.id.menu_item_tutorial:
			startActivity(new Intent(this, WelcomeTutorialWizardActivity.class));
			break;
		case R.id.menu_item_calibrate:
			startActivity(new Intent(this, CalibrationWizardActivity.class));
			break;
		case R.id.menu_item_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		case R.id.menu_item_report:
			String versionName = "???";
			try {
				versionName = getPackageManager().getPackageInfo(this.getPackageName(),
						PackageManager.GET_META_DATA).versionName;
			} catch (NameNotFoundException e) {
				this.trackEvent("Retrieving VersionName failed for HostActivity.", 1);
				break;
			}

			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { getString(R.string.developer_email_address) });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					getString(R.string.email_developer_subject, versionName));
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.developer_email_body));
			startActivity(Intent.createChooser(emailIntent, getString(R.string.title_report)));
			// startActivity(new Intent("android.intent.action.VIEW",
			// Uri.parse("http://code.google.com/p/electricsleep/issues/entry")));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
