package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.debalink.adapters.ManageDiscussionAdapter;
import com.debalink.adapters.MergeAdapter;
import com.debalink.models.DiscussionModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class ManageDiscussionsFragment extends Fragment {
	private ListView lvDiscussions;
	private ArrayList<DiscussionModel> discussionsPopularList, discussionsFeatureList, discussionsLatestList;
	private MergeAdapter mergeAdapter;
	private int nextPopular = 1, nextFeature = 1, nextLatest = 1;
	private View viewMorePopular, viewMoreFeature, viewMoreLatest;
	private ImageButton btnMorePopular, btnMoreFeature, btnMoreLatest;
	private ManageDiscussionAdapter popularAdapter, featureAdapter, latestrAdapter;
	private boolean isMoreFeature, isMoreLatest, isMorePopular;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_layout, null);
		lvDiscussions = (ListView) view.findViewById(R.id.list);

		viewMorePopular = inflater.inflate(R.layout.list_more_footer_view, null);
		viewMoreFeature = inflater.inflate(R.layout.list_more_footer_view, null);
		viewMoreLatest = inflater.inflate(R.layout.list_more_footer_view, null);

		btnMorePopular = (ImageButton) viewMorePopular.findViewById(R.id.button1);
		btnMoreFeature = (ImageButton) viewMoreFeature.findViewById(R.id.button1);
		btnMoreLatest = (ImageButton) viewMoreLatest.findViewById(R.id.button1);

		discussionsPopularList = new ArrayList<DiscussionModel>();
		discussionsFeatureList = new ArrayList<DiscussionModel>();
		discussionsLatestList = new ArrayList<DiscussionModel>();

		new Async(getActivity(), "Loading Discussion", 0).execute();
		btnMorePopular.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				nextPopular++;
				new Async(getActivity(), "Loading Publications", 1).execute();
			}
		});

		btnMoreFeature.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				nextFeature++;
				new Async(getActivity(), "Loading Publications", 2).execute();
			}
		});

		btnMoreLatest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				nextLatest++;
				new Async(getActivity(), "Loading Publications", 3).execute();
			}
		});

		return view;
	}

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message, int from) {
			super(ctx, message);
			this.from = from;
		}

		private SoapObject res;
		private int errorFlag = 0;
		private int from;
		private ArrayList<DiscussionModel> discussionsPopular, discussionsFeature, discussionsLatest;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("SectionId", "POP");
				cv.put("Next", nextPopular);

				if (from == 0 || from == 1) {
					res = hr.getSoapObjectFromServer(cv, "ManageDiscussionBySectionWise");
					discussionsPopular = hr.parseDiscussinResponse(res);
					discussionsPopularList.addAll(discussionsPopular);

				}

				if (from == 0 || from == 2) {

					cv.put("SectionId", "FEA");
					cv.put("Next", nextFeature);
					res = hr.getSoapObjectFromServer(cv, "ManageDiscussionBySectionWise");
					discussionsFeature = hr.parseDiscussinResponse(res);
					discussionsFeatureList.addAll(discussionsFeature);
				}

				if (from == 0 || from == 3) {
					cv.put("SectionId", "LAT");
					cv.put("Next", nextLatest);
					res = hr.getSoapObjectFromServer(cv, "ManageDiscussionBySectionWise");
					discussionsLatest = hr.parseDiscussinResponse(res);

					discussionsLatestList.addAll(discussionsLatest);
				}

				Log.i("SelectAllDiscussionForDashBoard", res.toString());

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
				/*
				 * ArrayList<ArrayList<DiscussionModel>> childItem = new
				 * ArrayList<ArrayList<DiscussionModel>>();
				 * childItem.add(discussionsPopularList);
				 * childItem.add(discussionsLatestList);
				 * childItem.add(discussionsFeatureList); ArrayList<String>
				 * groupTitle = new ArrayList<String>();
				 * 
				 * groupTitle.add("Popular"); groupTitle.add("Feature");
				 * groupTitle.add("Latest");
				 * 
				 * View v =
				 * getActivity().getLayoutInflater().inflate(R.layout.text_row,
				 * null); TextView txt1 = (TextView)
				 * v.findViewById(R.id.textView1);
				 * 
				 * View v2 =
				 * getActivity().getLayoutInflater().inflate(R.layout.text_row,
				 * null); TextView txt2 = (TextView)
				 * v2.findViewById(R.id.textView1);
				 * 
				 * View v3 =
				 * getActivity().getLayoutInflater().inflate(R.layout.text_row,
				 * null); TextView txt3 = (TextView)
				 * v3.findViewById(R.id.textView1);
				 * 
				 * txt1.setText("POPULAR DISCUSSIONS");
				 * txt2.setText("LATEST DISCUSSIONS");
				 * txt3.setText("FEATURE DISCUSSIONS");
				 * 
				 * DiscussionsAdapter popularAdapter = new
				 * DiscussionsAdapter(getActivity(), discussionsPopularList);
				 * DiscussionsAdapter featureAdapter = new
				 * DiscussionsAdapter(getActivity(), discussionsFeatureList);
				 * DiscussionsAdapter latestrAdapter = new
				 * DiscussionsAdapter(getActivity(), discussionsLatestList);
				 * 
				 * txt1.setTag("1"); txt2.setTag("2"); txt3.setTag("3");
				 * 
				 * txt1.setTypeface(Fonts.getRobotoMedium(getActivity().
				 * getBaseContext()));
				 * txt2.setTypeface(Fonts.getRobotoMedium(getActivity
				 * ().getBaseContext()));
				 * txt3.setTypeface(Fonts.getRobotoMedium(
				 * getActivity().getBaseContext()));
				 * 
				 * mergeAdapter = new MergeAdapter(); if (discussionsPopularList
				 * != null && discussionsPopularList.size() > 0) {
				 * mergeAdapter.addView(v);
				 * mergeAdapter.addAdapter(popularAdapter);//
				 * mergeAdapter.addView(vPopular); }
				 * 
				 * if (discussionsLatestList != null &&
				 * discussionsLatestList.size() > 0) { mergeAdapter.addView(v2);
				 * mergeAdapter.addAdapter(latestrAdapter);//
				 * mergeAdapter.addView(vFeature); }
				 * 
				 * if (discussionsFeatureList != null &&
				 * discussionsFeatureList.size() > 0) {
				 * mergeAdapter.addView(v3);
				 * mergeAdapter.addAdapter(featureAdapter);//
				 * mergeAdapter.addView(vLatest);
				 * 
				 * } lvDiscussions.setAdapter(mergeAdapter);
				 */

				setAdapter(discussionsPopular, discussionsFeature, discussionsLatest, from);
			}
		}

	}

	private void setAdapter(ArrayList<DiscussionModel> discussionsPopular, ArrayList<DiscussionModel> discussionsFeature, ArrayList<DiscussionModel> discussionsLatest, int from) {

		View v = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
		TextView txt1 = (TextView) v.findViewById(R.id.textView1);

		View v2 = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
		TextView txt2 = (TextView) v2.findViewById(R.id.textView1);

		View v3 = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
		TextView txt3 = (TextView) v3.findViewById(R.id.textView1);

		txt1.setText("POPULAR DISCUSSIONS");
		txt2.setText("LATEST DISCUSSIONS");
		txt3.setText("FEATURE DISCUSSIONS");

		popularAdapter = new ManageDiscussionAdapter(getActivity(), discussionsPopularList);
		featureAdapter = new ManageDiscussionAdapter(getActivity(), discussionsFeatureList);
		latestrAdapter = new ManageDiscussionAdapter(getActivity(), discussionsLatestList);

		txt1.setTag("1");
		txt2.setTag("2");
		txt3.setTag("3");

		txt1.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));
		txt2.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));
		txt3.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));

		// txt1.setOnClickListener(ManageDiscussionsFragment.this);
		mergeAdapter = new MergeAdapter();

		if (discussionsPopularList != null && discussionsPopularList.size() > 0) {
			mergeAdapter.addView(v);
			mergeAdapter.addAdapter(popularAdapter);//

			if ((discussionsPopular == null || discussionsPopular.size() == 6) && (from == 0 || from == 1 || isMorePopular)) {
				mergeAdapter.addView(viewMorePopular);
				isMorePopular = true;
			} else {
				isMorePopular = false;
			}
		}

		if (discussionsFeatureList != null && discussionsFeatureList.size() > 0) {
			mergeAdapter.addView(v3);
			mergeAdapter.addAdapter(featureAdapter);//

			if ((discussionsFeature == null || discussionsFeature.size() == 6) && (from == 0 || from == 2) || isMoreFeature) {
				mergeAdapter.addView(viewMoreFeature);
				isMoreFeature = true;
			} else {
				isMoreFeature = false;
			}
		}

		if (discussionsLatestList != null && discussionsLatestList.size() > 0) {
			mergeAdapter.addView(v2);
			mergeAdapter.addAdapter(latestrAdapter);//

			if ((discussionsLatest == null || discussionsLatest.size() == 6) && (from == 0 || from == 3 || isMoreLatest)) {
				mergeAdapter.addView(viewMoreLatest);
				isMoreLatest = true;
			} else {
				isMoreLatest = false;
			}
		}

		lvDiscussions.setAdapter(mergeAdapter);
	}

}
