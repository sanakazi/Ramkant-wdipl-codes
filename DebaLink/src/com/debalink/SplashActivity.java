package com.debalink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.debalink.utils.CustomTypefaceSpan;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;

public class SplashActivity extends Activity implements OnClickListener {
	private TextView txtTitle;
	private ImageButton btnLogin, btnSignUp;
	private String firstWord = "Find and share stories, ideas and opportunities with";
	private String secondWord = " Debalink";
	private ViewPager viewPager;
	private SeekBar sb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.startup_screen);
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		btnLogin = (ImageButton) findViewById(R.id.btnLogin);
		btnSignUp = (ImageButton) findViewById(R.id.btnSignUp);
		viewPager = (ViewPager) findViewById(R.id.imgLogo);
		sb = (SeekBar) findViewById(R.id.seekBar1);

		Spannable spannable = new SpannableString(firstWord + secondWord);
		spannable.setSpan(new CustomTypefaceSpan("sans-serif", Fonts.getNormal(getBaseContext())), 0, firstWord.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new CustomTypefaceSpan("sans-serif", Fonts.getBold(getBaseContext())), firstWord.length(), firstWord.length()
				+ secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		txtTitle.setText(spannable);

		btnLogin.setOnClickListener(this);
		btnSignUp.setOnClickListener(this);

		MyPagerAdapter adapter = new MyPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);

		if (!UserPreferences.getUserId(getApplicationContext()).equals("-1")) {
			Intent signupIntent = new Intent(this, HomeActivity.class);
			startActivity(signupIntent);
			finish();
		}

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int pos) {
				sb.setProgress(pos);
				
				if(pos!=0){
					txtTitle.setVisibility(View.GONE);
				}else{
					txtTitle.setVisibility(View.VISIBLE);
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
				if(fromUser)
				viewPager.setCurrentItem(progress);
				
				if(progress!=0){
					txtTitle.setVisibility(View.GONE);
				}else{
					txtTitle.setVisibility(View.VISIBLE);
				}

			}
		});

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnLogin:
			Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
			startActivity(loginIntent);
			break;

		case R.id.btnSignUp:
			Intent registerIntent = new Intent(getBaseContext(), RegisterActivity.class);
			startActivity(registerIntent);
			break;

		}
		finish();

	}

	private class MyPagerAdapter extends PagerAdapter {

		private int[] ids = { R.drawable.ic_logo,R.drawable.sl_publications, R.drawable.sl_headline, R.drawable.sl_recommend, R.drawable.sl_pin,
				R.drawable.sl_discussions,R.drawable.sl_showcase};

		public int getCount() {
			return ids.length;
		}

		public Object instantiateItem(View collection, int position) {

			LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View view = inflater.inflate(R.layout.sl_layout, null);
			ImageView img = (ImageView) view.findViewById(R.id.imageView1);
			img.setImageResource(ids[position]);

			((ViewPager) collection).addView(view, 0);
			return view;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);

		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}
}
