package com.debalink.slidemenu;

import java.lang.ref.WeakReference;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.debalink.GlobalSearchActivity;
import com.debalink.HomeActivity;
import com.debalink.R;
import com.debalink.customviews.CustomButton;
import com.debalink.customviews.CustomEditText;
import com.debalink.customviews.DrawableClickListener;
import com.debalink.interfaces.MenuCallback;
import com.debalink.services.OnAlarmReceiver;
import com.debalink.utils.Contant;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import com.debalink.utils.UserPreferences;

public class MenuFragment extends Fragment {

	private TextView txtName;
	private Button btnFeeds, btnNotification, btnMessage, btnSubscribers, btnYourSubscriptions, btnHeadlines, btnLogout,btnSetting;
	private static MenuCallback callback;
	private ImageView imgProfilePic;
	private ImageThreadDownloader imageDownloader;
	private static int num = -1;
	private CustomEditText edtSearch;
	private CustomButton btnPublicDiscussion, btnPublicPublications;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.sidebar_new, null);

		btnFeeds = (Button) view.findViewById(R.id.button1);
		btnNotification = (Button) view.findViewById(R.id.button2);
		btnMessage = (Button) view.findViewById(R.id.button3);
		btnSubscribers = (Button) view.findViewById(R.id.button4);
		btnYourSubscriptions = (Button) view.findViewById(R.id.button6);
		btnSetting= (Button) view.findViewById(R.id.btnSetting);
		btnHeadlines = (Button) view.findViewById(R.id.button7);
		btnPublicPublications = (CustomButton) view.findViewById(R.id.button8);
		btnPublicDiscussion = (CustomButton) view.findViewById(R.id.button9);
		btnLogout = (Button) view.findViewById(R.id.button5);
		txtName = (TextView) view.findViewById(R.id.txtName);
		imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);
		edtSearch = (CustomEditText) view.findViewById(R.id.edtSearch);
		txtName.setTypeface(Fonts.getFuturaMedium(getActivity()));
		edtSearch.setTypeface(Fonts.getFuturaMedium(getActivity()));

		if (num == 0) {
			btnHeadlines.setSelected(true);
		} else if (num == 1) {
			btnFeeds.setSelected(true);
		} else if (num == 2) {
			btnNotification.setSelected(true);
		} else if (num == 3) {
			btnMessage.setSelected(true);
		} else if (num == 4) {
			btnSubscribers.setSelected(true);
		} else if (num == 5) {
			btnYourSubscriptions.setSelected(true);
		} else if (num == 6) {
			btnPublicPublications.setSelected(true);
		} else if (num == 7) {
			btnPublicDiscussion.setSelected(true);
		}else if(num==8){
			btnSetting.setSelected(true);
		}

		if (OnAlarmReceiver.notificationCount > 0) {
			Drawable drawable = ProcessingBitmap(R.drawable.trans, "(" + OnAlarmReceiver.notificationCount + ")");
			btnNotification.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		}

		if (OnAlarmReceiver.messageCount > 0) {
			Drawable drawable = ProcessingBitmap(R.drawable.trans, "(" + OnAlarmReceiver.messageCount + ")");
			btnMessage.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		}

		if (OnAlarmReceiver.subscribersCount > 0) {
			Drawable drawable = ProcessingBitmap(R.drawable.trans, "(" + OnAlarmReceiver.subscribersCount + ")");
			btnYourSubscriptions.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		}

		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		Drawable d = getResources().getDrawable(R.drawable.ic_blank_profile);
		width = d.getIntrinsicWidth();

		width = (int) (width * (0.50));
		imageDownloader = new ImageThreadDownloader(getActivity(), getActivity(), width, width);

		imgProfilePic.setTag(Contant.PIC_URL + UserPreferences.getProfilePic(getActivity()));

		WeakReference<ImageView> softImgView = new WeakReference<ImageView>(imgProfilePic);

		txtName.setText(UserPreferences.getUserName(getActivity()));

		imgProfilePic.setLayoutParams(new LinearLayout.LayoutParams(width, width));

		imageDownloader.displayImage(Contant.PIC_URL + UserPreferences.getProfilePic(getActivity()), softImgView);

		edtSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), GlobalSearchActivity.class);
				startActivity(intent);

			}
		});

		btnFeeds.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				num = 1;
				btnFeeds.setSelected(true);
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("feeds", "feeds");
				startActivity(intent);
				getActivity().finish();
			}
		});

		btnPublicDiscussion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				num = 7;
				btnPublicDiscussion.setSelected(true);
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("discussion", "discussion");
				startActivity(intent);
				getActivity().finish();
			}
		});

		btnPublicPublications.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				num = 6;
				btnPublicPublications.setSelected(true);
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("publication", "publication");
				startActivity(intent);
				getActivity().finish();
			}
		});

		btnHeadlines.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				num = 0;
				btnHeadlines.setSelected(true);
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("dashboard", "dashboard");
				startActivity(intent);
				getActivity().finish();
			}
		});

		btnYourSubscriptions.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				num = 5;
				btnYourSubscriptions.setSelected(true);
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("subscription_list", "subscription_list");
				startActivity(intent);
				getActivity().finish();
			}
		});

		btnSubscribers.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				num = 4;
				btnSubscribers.setSelected(true);
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("subscribers_list", "subscribers_list");
				startActivity(intent);
				getActivity().finish();
			}
		});

		btnNotification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				num = 2;
				btnNotification.setSelected(true);
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("notifications", "notifications");
				startActivity(intent);
				getActivity().finish();
			}
		});
		btnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				UserPreferences.clearPreferences(getActivity());

				// getActivity().finish();

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(intent);
				// getActivity().finish();
				Intent intent1 = new Intent(getActivity(), HomeActivity.class);
				intent1.putExtra("logout", "logout");
				// intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent1);
				getActivity().finish();
			}
		});
		btnMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				num = 3;
				btnMessage.setSelected(true);
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("message", "message");
				startActivity(intent);
				getActivity().finish();
			}
		});

		edtSearch.setDrawableClickListener(new DrawableClickListener() {

			public void onClick(DrawablePosition target) {
				switch (target) {
				case RIGHT:
					if (edtSearch.getText().toString().length() > 0) {
						Intent intent = new Intent(getActivity(), GlobalSearchActivity.class);

						startActivity(intent);
					}
					break;

				}
			}

		});

		btnPublicPublications.setDrawableClickListener(new DrawableClickListener() {

			public void onClick(DrawablePosition target) {
				switch (target) {
				case RIGHT:

					if (callback != null) {
						getActivity().finish();
						// callback.showPublicDiscussions();
					}
					num = -1;
					
					Intent intent = new Intent(getActivity(), HomeActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.putExtra("add_publication", "add_publication");
					startActivity(intent);
					getActivity().finish();

					break;
				default:
				}
			}

		});
		
		btnPublicDiscussion.setDrawableClickListener(new DrawableClickListener() {

			public void onClick(DrawablePosition target) {
				switch (target) {
				case RIGHT:

					if (callback != null) {
						getActivity().finish();
						// callback.showPublicDiscussions();
					}
					num = -1;
					
					Intent intent = new Intent(getActivity(), HomeActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.putExtra("add_discussion", "add_discussion");
					startActivity(intent);
					getActivity().finish();

					break;
				default:
				}
			}

		});
		
		btnSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				num = 8;
				btnSetting.setSelected(true);
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("setting", "setting");
				startActivity(intent);
				getActivity().finish();
			}
		});
		
		
		imgProfilePic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("profile", "profile");
				startActivity(intent);
				getActivity().finish();
			}
		});
		
		txtName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (callback != null) {
					getActivity().finish();
					// callback.showPublicDiscussions();
				}
				
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("profile", "profile");
				startActivity(intent);
				getActivity().finish();
			}
		});

		

		return view;
	}

	public static void setMenuCallback(MenuCallback callback) {
		MenuFragment.callback = callback;
	}

	private BitmapDrawable ProcessingBitmap(int drawableId, String text) {

		Bitmap bm1 = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

		// Bitmap bm1 = null;
		Bitmap newBitmap = null;

		Config config = bm1.getConfig();
		if (config == null) {
			config = Bitmap.Config.ARGB_8888;
		}
		Typeface tf = Fonts.getNormal(getActivity());
		newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
		Canvas newCanvas = new Canvas(newBitmap);

		newCanvas.drawBitmap(bm1, 0, 0, null);

		if (text != null) {

			Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
			paintText.setColor(Color.BLUE);
			paintText.setTextSize(convertToPixels(getActivity(), 14));
			paintText.setStyle(Style.FILL);
			paintText.setTypeface(tf);
			// paintText.setShadowLayer(10f, 10f, 10f, Color.WHITE);
			paintText.setTextAlign(Align.CENTER);
			Rect rectText = new Rect();
			paintText.getTextBounds(text, 0, text.length(), rectText);

			newCanvas.drawText(text, 8, rectText.height(), paintText);

		}

		return new BitmapDrawable(getResources(), newBitmap);
	}

	private BitmapDrawable writeTextOnDrawable(int drawableId, String text) {

		Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

		Typeface tf = Fonts.getBold(getActivity());

		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.RED);
		paint.setTypeface(tf);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(convertToPixels(getActivity(), 15));

		Rect textRect = new Rect();
		paint.getTextBounds(text, 0, text.length(), textRect);

		Canvas canvas = new Canvas(bm);

		// If the text is bigger than the canvas , reduce the font size
		if (textRect.width() >= (canvas.getWidth() - 4)) // the padding on
															// either sides is
															// considered as 4,
															// so as to
															// appropriately fit
															// in the text
			paint.setTextSize(convertToPixels(getActivity(), 7)); // Scaling
																	// needs to
																	// be used
																	// for
																	// different
																	// dpi's

		// Calculate the positions
		int xPos = (canvas.getWidth() / 2) - 2; // -2 is for regulating the x
												// position offset

		// "- ((paint.descent() + paint.ascent()) / 2)" is the distance from the
		// baseline to the center.
		int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));

		canvas.drawText(text, xPos, yPos, paint);

		return new BitmapDrawable(getResources(), bm);
	}

	public static int convertToPixels(Context context, int nDP) {
		final float conversionScale = context.getResources().getDisplayMetrics().density;

		return (int) ((nDP * conversionScale) + 0.5f);

	}
}
