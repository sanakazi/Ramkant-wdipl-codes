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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.debalink.adapters.CategoryAdapter;
import com.debalink.adapters.DiscussionsAdapter;
import com.debalink.adapters.MergeAdapter;
import com.debalink.models.CategoryModel;
import com.debalink.models.DiscussionModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class PublicDiscussionFragment extends Fragment {
	private ListView lvDiscussions;
	private ArrayList<DiscussionModel> discussionsPopularList, discussionsFeatureList, discussionsLatestList;
	private MergeAdapter mergeAdapter;
	private ArrayList<CategoryModel> categoryList;
	private AutoCompleteTextView edtCategory;
	private String selectedCatId = "0";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.public_publication_and_discussion, null);
		lvDiscussions = (ListView) view.findViewById(R.id.list);
		edtCategory = (AutoCompleteTextView) view.findViewById(R.id.edtCategory);
		new Async(getActivity(), "Loading Discussion", "0").execute();
		
		edtCategory.setTypeface(Fonts.getRobotoMedium(getActivity()));

		edtCategory.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				if (categoryList != null) {
					edtCategory.setText(categoryList.get(position).getCategoryName());
					selectedCatId = categoryList.get(position).getCategoryId();
				}

				new Async(getActivity(), "Loading Discussion", selectedCatId).execute();
			}
		});

		edtCategory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				edtCategory.showDropDown();

			}
		});

		return view;
	}

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message, String categoryId) {
			super(ctx, message);
			this.categoryId = categoryId;
		}

		private SoapObject res;
		private int errorFlag = 0;
		private String categoryId = "0";

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
				cv.put("CategoryId", categoryId);

				res = hr.getSoapObjectFromServer(cv, "SelectPublicDiscussionBySectionWise");
				discussionsPopularList = hr.parseDiscussinResponse(res);

				cv = new ContentValues();
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("SectionId", "FEA");
				cv.put("CategoryId", categoryId);
				res = hr.getSoapObjectFromServer(cv, "SelectPublicDiscussionBySectionWise");
				discussionsFeatureList = hr.parseDiscussinResponse(res);

				cv = new ContentValues();
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("SectionId", "LAT");
				cv.put("CategoryId", categoryId);
				res = hr.getSoapObjectFromServer(cv, "SelectPublicDiscussionBySectionWise");
				discussionsLatestList = hr.parseDiscussinResponse(res);

				categoryList=new ArrayList<CategoryModel>();
				CategoryModel model=new CategoryModel();
				model.setCategoryId("0");
				model.setCategoryName("All");
				categoryList.add(model);
				res = hr.getSoapObjectFromServer(null, "SelectCategoryPublicDiscussion");
				ArrayList<CategoryModel> categoryList1 = hr.parseCategoryList(res);
				categoryList.addAll(categoryList1);

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
				ArrayList<ArrayList<DiscussionModel>> childItem = new ArrayList<ArrayList<DiscussionModel>>();
				childItem.add(discussionsPopularList);
				childItem.add(discussionsLatestList);
				childItem.add(discussionsFeatureList);
				ArrayList<String> groupTitle = new ArrayList<String>();

				groupTitle.add("Popular");
				groupTitle.add("Feature");
				groupTitle.add("Latest");

				View v = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
				TextView txt1 = (TextView) v.findViewById(R.id.textView1);

				View v2 = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
				TextView txt2 = (TextView) v2.findViewById(R.id.textView1);

				View v3 = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
				TextView txt3 = (TextView) v3.findViewById(R.id.textView1);

				txt1.setText("POPULAR DISCUSSIONS");
				txt2.setText("LATEST DISCUSSIONS");
				txt3.setText("FEATURE DISCUSSIONS");

				DiscussionsAdapter popularAdapter = new DiscussionsAdapter(getActivity(), discussionsPopularList);
				DiscussionsAdapter featureAdapter = new DiscussionsAdapter(getActivity(), discussionsFeatureList);
				DiscussionsAdapter latestrAdapter = new DiscussionsAdapter(getActivity(), discussionsLatestList);

				txt1.setTag("1");
				txt2.setTag("2");
				txt3.setTag("3");

				txt1.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));
				txt2.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));
				txt3.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));

				mergeAdapter = new MergeAdapter();
				if (discussionsPopularList != null && discussionsPopularList.size() > 0) {
					mergeAdapter.addView(v);
					mergeAdapter.addAdapter(popularAdapter);// mergeAdapter.addView(vPopular);
				}

				if (discussionsLatestList != null && discussionsLatestList.size() > 0) {
					mergeAdapter.addView(v2);
					mergeAdapter.addAdapter(latestrAdapter);// mergeAdapter.addView(vFeature);
				}

				if (discussionsFeatureList != null && discussionsFeatureList.size() > 0) {
					mergeAdapter.addView(v3);
					mergeAdapter.addAdapter(featureAdapter);// mergeAdapter.addView(vLatest);

				}

				lvDiscussions.setAdapter(mergeAdapter);

				edtCategory.setAdapter(new CategoryAdapter(getActivity(), categoryList));
			}
		}

	}

	private class AsyncCategory extends AsynDownloader {

		public AsyncCategory(Context ctx, String message) {
			super(ctx, message);
		}

		private SoapObject res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {
			HttpRequest hr = new HttpRequest();
			try {

				res = hr.getSoapObjectFromServer(null, "getCategory");
				categoryList = hr.parseCategoryList(res);

				Log.i("getCategory", res.toString());

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
				edtCategory.setAdapter(new CategoryAdapter(getActivity(), categoryList));
			} else {
				// CustomPopup.showPicDialog(getActivity(), errorFlag,
				// AddDiscussionFragment.this);
			}

		}

	}

}
