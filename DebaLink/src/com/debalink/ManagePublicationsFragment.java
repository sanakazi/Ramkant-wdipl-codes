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

import com.debalink.adapters.ManagePublicationAdapter;
import com.debalink.adapters.MergeAdapter;
import com.debalink.adapters.PublicationsAdapter;
import com.debalink.models.PublicationModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class ManagePublicationsFragment extends Fragment implements View.OnClickListener {
	private ListView lvPublications;
	private ArrayList<PublicationModel> publicationsPopularList, publicationsFeatureList, publicationsLatestList;
	private MergeAdapter mergeAdapter;
	private int nextPopular = 1, nextFeature = 1, nextLatest = 1;
	private View viewMorePopular, viewMoreFeature, viewMoreLatest;
	private ImageButton btnMorePopular, btnMoreFeature, btnMoreLatest;
	private ManagePublicationAdapter popularAdapter, featureAdapter, latestrAdapter;
	private boolean isMoreFeature, isMoreLatest, isMorePopular;

	// private GridView gvPublications;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_layout, null);
		lvPublications = (ListView) view.findViewById(R.id.list);
		// gvPublications = (GridView) view.findViewById(R.id.grid);
		new Async(getActivity(), "Loading Publications", 0).execute();

		viewMorePopular = inflater.inflate(R.layout.list_more_footer_view, null);
		viewMoreFeature = inflater.inflate(R.layout.list_more_footer_view, null);
		viewMoreLatest = inflater.inflate(R.layout.list_more_footer_view, null);

		publicationsPopularList = new ArrayList<PublicationModel>();
		publicationsLatestList = new ArrayList<PublicationModel>();
		publicationsFeatureList = new ArrayList<PublicationModel>();

		btnMorePopular = (ImageButton) viewMorePopular.findViewById(R.id.button1);
		btnMoreFeature = (ImageButton) viewMoreFeature.findViewById(R.id.button1);
		btnMoreLatest = (ImageButton) viewMoreLatest.findViewById(R.id.button1);

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

		private int from;
		private SoapObject res;
		private int errorFlag = 0;
		private ArrayList<PublicationModel> publicationsPopular, publicationsFeature, publicationsLatest;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {

			// publicationsPopular=new ArrayList<PublicationModel>();
			// publicationsFeature=new ArrayList<PublicationModel>();
			// publicationsLatest=new ArrayList<PublicationModel>();

			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("UserID", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("SectionType", "POP"); 
				cv.put("Next", nextPopular);

				if (from == 0 || from == 1) {
					res = hr.getSoapObjectFromServer(cv, "ManagePublicationBySectionWise");
					publicationsPopular = hr.parsePublicationResponse(res);
					publicationsPopularList.addAll(publicationsPopular);

				}

				if (from == 0 || from == 2) {

					cv.put("SectionType", "FEA");
					cv.put("Next", nextFeature);
					res = hr.getSoapObjectFromServer(cv, "ManagePublicationBySectionWise");
					publicationsFeature = hr.parsePublicationResponse(res);
					publicationsFeatureList.addAll(publicationsFeature);
				}

				if (from == 0 || from == 3) {
					cv.put("SectionType", "LAT");
					cv.put("Next", nextLatest);
					res = hr.getSoapObjectFromServer(cv, "ManagePublicationBySectionWise");
					publicationsLatest = hr.parsePublicationResponse(res);

					publicationsLatestList.addAll(publicationsLatest);
				}

				Log.i("getPublicationBySectionWise ", publicationsPopularList.size() + " " + publicationsFeatureList.size() + " " + publicationsLatestList.size());

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
				 * txt1.setText("POPULAR PUBLICACTIONS");
				 * txt2.setText("LATEST PUBLICACTIONS");
				 * txt3.setText("FEATURE PUBLICACTIONS");
				 * 
				 * PublicationsAdapter popularAdapter = new
				 * PublicationsAdapter(getActivity(), publicationsPopularList);
				 * PublicationsAdapter featureAdapter = new
				 * PublicationsAdapter(getActivity(), publicationsFeatureList);
				 * PublicationsAdapter latestrAdapter = new
				 * PublicationsAdapter(getActivity(), publicationsLatestList);
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
				 * txt1.setOnClickListener(ManagePublicationsFragment.this);
				 * mergeAdapter = new MergeAdapter();
				 * 
				 * if (publicationsPopularList != null &&
				 * publicationsPopularList.size() > 0) {
				 * mergeAdapter.addView(v);
				 * mergeAdapter.addAdapter(popularAdapter);//
				 * 
				 * if (publicationsPopular.size() == 6 && (from == 0 || from ==
				 * 1)) mergeAdapter.addView(viewMorePopular); }
				 * 
				 * if (publicationsFeatureList != null &&
				 * publicationsFeatureList.size() > 0) {
				 * mergeAdapter.addView(v3);
				 * mergeAdapter.addAdapter(featureAdapter);//
				 * 
				 * if (publicationsFeature.size() == 6 && (from == 0 || from ==
				 * 2)) mergeAdapter.addView(viewMoreLatest); }
				 * 
				 * if (publicationsLatestList != null &&
				 * publicationsLatestList.size() > 0) {
				 * mergeAdapter.addView(v2);
				 * mergeAdapter.addAdapter(latestrAdapter);//
				 * 
				 * if (publicationsLatest.size() == 6 && (from == 0 || from ==
				 * 3)) mergeAdapter.addView(viewMoreFeature); }
				 * 
				 * lvPublications.setAdapter(mergeAdapter);
				 */

				setAdapter(publicationsPopular, publicationsFeature, publicationsLatest, from);
			}

			// gvPublications.setAdapter(new
			// PublicPublicationAdapter(getActivity(),groupTitle,childItem));
		}

	}

	private void setAdapter(ArrayList<PublicationModel> publicationsPopular, ArrayList<PublicationModel> publicationsFeature, ArrayList<PublicationModel> publicationsLatest,
			int from) {

		View v = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
		TextView txt1 = (TextView) v.findViewById(R.id.textView1);

		View v2 = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
		TextView txt2 = (TextView) v2.findViewById(R.id.textView1);

		View v3 = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
		TextView txt3 = (TextView) v3.findViewById(R.id.textView1);

		txt1.setText("POPULAR PUBLICACTIONS");
		txt2.setText("LATEST PUBLICACTIONS");
		txt3.setText("FEATURE PUBLICACTIONS");

		popularAdapter = new ManagePublicationAdapter(getActivity(), publicationsPopularList);
		featureAdapter = new ManagePublicationAdapter(getActivity(), publicationsFeatureList);
		latestrAdapter = new ManagePublicationAdapter(getActivity(), publicationsLatestList);

		txt1.setTag("1");
		txt2.setTag("2");
		txt3.setTag("3");

		txt1.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));
		txt2.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));
		txt3.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));

		txt1.setOnClickListener(ManagePublicationsFragment.this);
		mergeAdapter = new MergeAdapter();

		if (publicationsPopularList != null && publicationsPopularList.size() > 0) {
			mergeAdapter.addView(v);
			mergeAdapter.addAdapter(popularAdapter);//

			if ((publicationsPopular == null || publicationsPopular.size() == 6) && (from == 0 || from == 1 || isMorePopular)) {
				mergeAdapter.addView(viewMorePopular);
				isMorePopular = true;
			} else {
				isMorePopular = false;
			}
		}

		if (publicationsFeatureList != null && publicationsFeatureList.size() > 0) {
			mergeAdapter.addView(v3);
			mergeAdapter.addAdapter(featureAdapter);//

			if ((publicationsFeature == null || publicationsFeature.size() == 6) && (from == 0 || from == 2) || isMoreFeature) {
				mergeAdapter.addView(viewMoreFeature);
				isMoreFeature = true;
			} else {
				isMoreFeature = false;
			}
		}

		if (publicationsLatestList != null && publicationsLatestList.size() > 0) {
			mergeAdapter.addView(v2);
			mergeAdapter.addAdapter(latestrAdapter);//

			if ((publicationsLatest == null || publicationsLatest.size() == 6) && (from == 0 || from == 3 || isMoreLatest)) {
				mergeAdapter.addView(viewMoreLatest);
				isMoreLatest = true;
			} else {
				isMoreLatest = false;
			}
		}

		lvPublications.setAdapter(mergeAdapter);

	}

	public View buildlabel(ArrayList<PublicationModel> list) {
		View v = (View) LayoutInflater.from(getActivity()).inflate(R.layout.list_layout, null);
		ListView gridView = (ListView) v.findViewById(R.id.list);
		// int margin = getResources().getDimensionPixelSize(R.dimen.margin);
		// gridView.setItemMargin(margin); // set the GridView margin
		// gridView.setPadding(margin, 0, margin, 0); // have the margin on the
		// sides as well

		//
		PublicationsAdapter adapter = new PublicationsAdapter(getActivity(), list);
		gridView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		Log.e("view", v.toString());
		return v;
	}

	@Override
	public void onClick(View v) {
		int id = Integer.parseInt(v.getTag().toString());

		switch (id) {
		case 1:
			// popuplar
			// vPopular.setVisibility(View.GONE);
			// mergeAdapter.notifyDataSetChanged();

			break;

		case 2:
			// latest
			break;
		case 3:
			// feature
			break;
		}

	}

}
