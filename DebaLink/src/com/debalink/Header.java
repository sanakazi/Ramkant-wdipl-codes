package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.adapters.DiscussionsAdapter;
import com.debalink.adapters.HeadlinesAdapter;
import com.debalink.adapters.OwnHeadlineAdapter;
import com.debalink.adapters.PublicationsAdapter;
import com.debalink.adapters.RecommendedAdapter;
import com.debalink.models.DiscussionModel;
import com.debalink.models.HeadlinesModel;
import com.debalink.models.OwnHeadlineModel;
import com.debalink.models.PublicationModel;
import com.debalink.models.RecommendedModel;
import com.debalink.slidemenu.MenuActivity;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import com.korovyansk.android.slideout.SlideoutActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class Header implements View.OnClickListener {
	private ActionBar actionBar;
	private ActionBarActivity act;
	private ImageButton btnHeadline, btnRecommended, btnShowcase, btnPinned;
	private View subView, subviewRecommaded, subviewPinnedSafe;
	private ImageButton splitter;
	private EditText edtPost;
	private ListView lvRecommended, lvRecommendedDetailed, lvPinsafe, lvPinsafeDetailed;
	private ArrayList<OwnHeadlineModel> listRecommended;
	private ArrayList<RecommendedModel> listRecommendedDetailed;
	private ArrayList<OwnHeadlineModel> listPinnedSafe;
	private ArrayList<DiscussionModel> pinnedDiscussins;
	private ArrayList<PublicationModel> pinnedPublications;
	private ArrayList<HeadlinesModel> listPinnedHealines;
	private RelativeLayout rl, rlPinned, rlListview, rlRecommendListview;
	private ImageView hdrPinsafe, hdrRecommended;

	public Header() {

	}

	public void showMenu(ActionBarActivity act, int visibility) {
		this.act = act;
		actionBar = act.getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.header);
		actionBar.setBackgroundDrawable(act.getResources().getDrawable(R.drawable.bg_title_menu));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		subView = (View) act.findViewById(R.id.headerLayout);
		subviewRecommaded = (View) act.findViewById(R.id.recommandedLayout);
		subviewPinnedSafe = (View) act.findViewById(R.id.pinnedSafeLayout);

		splitter = (ImageButton) actionBar.getCustomView().findViewById(R.id.strip);
		splitter.setVisibility(visibility);
		subView.setVisibility(View.GONE);

		initHeadlineView();
		initRecommendedView();
		initPinsafeView();

		btnHeadline = (ImageButton) actionBar.getCustomView().findViewById(R.id.btnHeadline);
		btnRecommended = (ImageButton) actionBar.getCustomView().findViewById(R.id.btnRecommended);
		btnShowcase = (ImageButton) actionBar.getCustomView().findViewById(R.id.btnShowcase);
		btnPinned = (ImageButton) actionBar.getCustomView().findViewById(R.id.btnPinned);

		btnHeadline.setOnClickListener(this);
		btnRecommended.setOnClickListener(this);
		btnShowcase.setOnClickListener(this);
		btnPinned.setOnClickListener(this);

		ImageButton btnHeader = (ImageButton) actionBar.getCustomView().findViewById(R.id.btnHeader);
		btnHeader.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showMenu();
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (btnHeadline.isSelected()) {
			subView.setVisibility(View.GONE);
			btnHeadline.setSelected(false);
			((HomeActivity) act).showOwnHeadlineFragment(View.GONE);
			return;
		}

		btnHeadline.setSelected(false);
		btnRecommended.setSelected(false);
		btnShowcase.setSelected(false);
		btnPinned.setSelected(false);
		v.setSelected(true);
		switch (id) {
		case R.id.btnHeadline:
			subviewRecommaded.setVisibility(View.GONE);
			subviewPinnedSafe.setVisibility(View.GONE);
			if (subView.getVisibility() == View.GONE) {
				subView.setVisibility(View.VISIBLE);

				splitter.setVisibility(View.GONE);
			} else {
				subView.setVisibility(View.GONE);
				splitter.setVisibility(View.VISIBLE);
			}

			((HomeActivity) act).showOwnHeadlineFragment(View.VISIBLE);
			// showMenu(act, View.GONE);
			break;
		case R.id.btnRecommended:
			subView.setVisibility(View.GONE);
			subviewPinnedSafe.setVisibility(View.GONE);
			if (subviewRecommaded.getVisibility() == View.GONE) {
				subviewRecommaded.setVisibility(View.VISIBLE);
				splitter.setVisibility(View.GONE);

				new AsyncRecommended(act, "Loading ").execute();
			} else {
				subviewRecommaded.setVisibility(View.GONE);
				splitter.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btnShowcase:
			subView.setVisibility(View.GONE);
			subviewRecommaded.setVisibility(View.GONE);
			subviewPinnedSafe.setVisibility(View.GONE);
			break;
		case R.id.btnPinned:
			subView.setVisibility(View.GONE);
			subviewRecommaded.setVisibility(View.GONE);

			if (subviewPinnedSafe.getVisibility() == View.GONE) {
				subviewPinnedSafe.setVisibility(View.VISIBLE);
				splitter.setVisibility(View.GONE);

				new AsyncPinsafe(act, "Loading ").execute();
			} else {
				subviewPinnedSafe.setVisibility(View.GONE);
				splitter.setVisibility(View.VISIBLE);
			}

			break;
		}

	}

	@SuppressWarnings("deprecation")
	private void showMenu() {

		View v = act.getLayoutInflater().inflate(R.layout.sidebar_new, null);
		Display display = act.getWindowManager().getDefaultDisplay();
		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		LinearLayout ll = (LinearLayout) v.findViewById(R.id.ll);
		int w = ll.getMeasuredWidth();

		w = display.getWidth() - w;
		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, w, act.getResources().getDisplayMetrics());
		SlideoutActivity.prepare(act, R.id.side_menu, width);
		act.startActivity(new Intent(act, MenuActivity.class));
		act.overridePendingTransition(0, 0);
	}

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message, String headline) {
			super(ctx, message);
			this.headline = headline;
		}

		private String res;
		private int errorFlag = 0;
		private String headline;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... v) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("UserId", UserPreferences.getUserId(act));
				cv.put("Description", headline);

				res = hr.getDataFromServer(cv, "InsertHeadlines");

				Log.i("InsertHeadlines", res.toString());

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return res;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (errorFlag == 0 && result.equalsIgnoreCase("0")) {
				edtPost.setText("");
				((HomeActivity) act).refreshHeadlines();
			}
		}

	}

	private void initHeadlineView() {
		ImageButton btnPost;

		btnPost = (ImageButton) subView.findViewById(R.id.btnPost);
		edtPost = (EditText) subView.findViewById(R.id.edtPost);

		btnPost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (edtPost.getText().toString().isEmpty()) {
					return;
				}

				new Async(act, "Posting headline", edtPost.getText().toString()).execute();
			}
		});
	}

	private void initRecommendedView() {
		lvRecommended = (ListView) subviewRecommaded.findViewById(R.id.listView1);
		lvRecommendedDetailed = (ListView) subviewRecommaded.findViewById(R.id.listView2);
		rl = (RelativeLayout) subviewRecommaded.findViewById(R.id.rl);

		hdrRecommended = (ImageView) subviewRecommaded.findViewById(R.id.hdrRecommended);
		rlRecommendListview = (RelativeLayout) subviewRecommaded.findViewById(R.id.rlRecommendListview);

		hdrRecommended.setVisibility(View.GONE);
		lvRecommendedDetailed.setVisibility(View.GONE);
		rlRecommendListview.setVisibility(View.GONE);

		lvRecommended.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				new AsyncRecommendedDetailed(act, "Loading", listRecommended.get(position).getType(), listRecommended.get(position).getiD()).execute();
			}
		});

	}

	private void initPinsafeView() {
		lvPinsafe = (ListView) subviewPinnedSafe.findViewById(R.id.listView1);
		lvPinsafeDetailed = (ListView) subviewPinnedSafe.findViewById(R.id.listView2);
		rlPinned = (RelativeLayout) subviewPinnedSafe.findViewById(R.id.rl);
		rlListview = (RelativeLayout) subviewPinnedSafe.findViewById(R.id.rlListview);
		hdrPinsafe = (ImageView) subviewPinnedSafe.findViewById(R.id.hdrPinsafe);
		lvPinsafeDetailed.setVisibility(View.GONE);
		hdrPinsafe.setVisibility(View.GONE);
		rlListview.setVisibility(View.GONE);

		lvPinsafe.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				new AsyncPinsafeDetailed(act, "Loading", listPinnedSafe.get(position).getType(), listPinnedSafe.get(position).getiD()).execute();
			}
		});

	}

	private class AsyncRecommended extends AsynDownloader {
		public AsyncRecommended(Context ctx, String message) {
			super(ctx, message);
		}

		private SoapObject res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {

			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("ReceiverId", UserPreferences.getUserId(act));

				res = hr.getSoapObjectFromServer(cv, "SelectRecommendationByUserWise");
				listRecommended = hr.parseOwnHeadlinesResponse(res);
				Log.i("SelectRecommendationByUserWise", res.toString());

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (errorFlag == 0) {

				if (listRecommended != null && listRecommended.size() < 5) {
					// rl.setLayoutParams(new LinearLayout.LayoutParams(new
					// LayoutParams(LayoutParams.MATCH_PARENT,
					// listRecommended.size() * 60)));
				}
				lvRecommended.setAdapter(new OwnHeadlineAdapter(act, listRecommended));

			}

		}

	}

	private class AsyncPinsafe extends AsynDownloader {
		public AsyncPinsafe(Context ctx, String message) {
			super(ctx, message);
		}

		private SoapObject res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {

			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("ReceiverId", UserPreferences.getUserId(act));

				res = hr.getSoapObjectFromServer(cv, "SelectPinsafeByUserWise");
				listPinnedSafe = hr.parseOwnHeadlinesResponse(res);
				Log.i("SelectPinsafeByUserWise", res.toString());

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (errorFlag == 0) {

				if (listPinnedSafe != null && listPinnedSafe.size() > 7) {
					// rlPinned.setLayoutParams(new
					// LinearLayout.LayoutParams(new
					// LayoutParams(LayoutParams.MATCH_PARENT,
					// listPinnedSafe.size() * 40)));
				}
				lvPinsafe.setAdapter(new OwnHeadlineAdapter(act, listPinnedSafe));

			}

		}

	}

	private class AsyncRecommendedDetailed extends AsynDownloader {
		public AsyncRecommendedDetailed(Context ctx, String message, String type, String id) {
			super(ctx, message);
			this.type = type;
			this.id = id;
		}

		private SoapObject res;
		private int errorFlag = 0;

		private String type, id;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("RecommendationType", type);
				cv.put("RecommendationID", id);

				res = hr.getSoapObjectFromServer(cv, "selectRecommendation");
				listRecommendedDetailed = hr.parseRecommendDetailedResponse(res, type);
				Log.i("selectRecommendation", res.toString());

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (errorFlag == 0) {
				hdrRecommended.setVisibility(View.VISIBLE);
				lvRecommendedDetailed.setVisibility(View.VISIBLE);
				rlRecommendListview.setVisibility(View.VISIBLE);
				if (listRecommended != null && listRecommended.size() > 5) {
					// rl.setLayoutParams(new LinearLayout.LayoutParams(new
					// LayoutParams(LayoutParams.MATCH_PARENT, 5 * 60)));
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.8f);

					rl.setLayoutParams(params);

					params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.1f);
					hdrRecommended.setLayoutParams(params);

					params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);
					rlRecommendListview.setLayoutParams(params);

				}

				lvRecommendedDetailed.setAdapter(new RecommendedAdapter(act, listRecommendedDetailed));
			}

		}

	}

	private class AsyncPinsafeDetailed extends AsynDownloader {
		public AsyncPinsafeDetailed(Context ctx, String message, String type, String id) {
			super(ctx, message);
			this.type = type;
			this.id = id;
		}

		private SoapObject res;
		private int errorFlag = 0;

		private String type, id;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("PinsafeType", type);
				cv.put("PinsafeID", id);
				res = hr.getSoapObjectFromServer(cv, "ViewPinsafe");
				if (type.equalsIgnoreCase("discussion")) {
					pinnedDiscussins = hr.parsePinsafeDiscussinResponse(res);
				} else if (type.equalsIgnoreCase("publication")) {
					pinnedPublications = hr.parsePinsafepublicationResponse(res);
				} else if (type.equalsIgnoreCase("headline")) {
					listPinnedHealines = hr.parsePinsafeHeadlinesResponse(res);
				}
				Log.i("selectRecommendation", res.toString());

			} catch (SocketTimeoutException e) {
				errorFlag = 1;
				e.printStackTrace();
			} catch (SocketException e) {
				errorFlag = 2;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				errorFlag = 3;
				e.printStackTrace();
			} catch (IOException e) {
				errorFlag = 4;
				e.printStackTrace();
			} catch (Exception e) {
				errorFlag = 5;
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (errorFlag == 0) {

				lvPinsafeDetailed.setVisibility(View.VISIBLE);
				hdrPinsafe.setVisibility(View.VISIBLE);
				rlListview.setVisibility(View.VISIBLE);

				if (listPinnedSafe != null && listPinnedSafe.size() > 7) {

					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.8f);

					rlPinned.setLayoutParams(params);

					params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.1f);
					hdrPinsafe.setLayoutParams(params);

					params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);
					rlListview.setLayoutParams(params);
				}

				if (type.equalsIgnoreCase("discussion")) {
					lvPinsafeDetailed.setAdapter(new DiscussionsAdapter(act, pinnedDiscussins, subviewPinnedSafe));
				} else if (type.equalsIgnoreCase("headline")) {
					lvPinsafeDetailed.setAdapter(new HeadlinesAdapter(act, listPinnedHealines));
				} else if (type.equalsIgnoreCase("publication")) {
					lvPinsafeDetailed.setAdapter(new PublicationsAdapter(act, pinnedPublications, subviewPinnedSafe));
				}
			}

		}

	}
}
