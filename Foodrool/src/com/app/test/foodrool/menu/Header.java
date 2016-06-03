package com.app.test.foodrool.menu;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.test.foodrool.R;
import com.app.test.foodrool.utils.Fonts;
import com.korovyansk.android.slideout.SlideoutActivity;

public class Header implements View.OnClickListener {
	private ActionBar actionBar;
	private ActionBarActivity act;
	private ImageButton splitter;

	public Header() {
	}

	public void showMenu(ActionBarActivity act, int visibility, String title) {
		this.act = act;
		actionBar = act.getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.header);
		actionBar.setBackgroundDrawable(act.getResources().getDrawable(R.drawable.bg_title_menu));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		splitter = (ImageButton) actionBar.getCustomView().findViewById(R.id.strip);
		splitter.setVisibility(visibility);
		ImageButton btnHeader = (ImageButton) actionBar.getCustomView().findViewById(R.id.btnHeader);
		btnHeader.setOnClickListener(this);
		TextView txtTitle = (TextView) actionBar.getCustomView().findViewById(R.id.txtTitle);
		txtTitle.setText(title);
		txtTitle.setTypeface(Fonts.getSansBold(act));
	}

	@Override
	public void onClick(View v) {
		showMenu();
	}

	@SuppressWarnings("deprecation")
	private void showMenu() {
		View v = act.getLayoutInflater().inflate(R.layout.siderbar_new, null);
		Display display = act.getWindowManager().getDefaultDisplay();
		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		LinearLayout ll = (LinearLayout) v.findViewById(R.id.rl);
		int w = ll.getMeasuredWidth();
		w = display.getWidth() - w;
		v.invalidate();

		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, w, act.getResources().getDisplayMetrics());
		if (act != null) {
			SlideoutActivity.prepare(act, R.id.main_layout, width);
			act.startActivity(new Intent(act, MenuActivity.class));
			act.overridePendingTransition(0, 0);
		}
	}
}
