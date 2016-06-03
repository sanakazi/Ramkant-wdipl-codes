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
import com.debalink.adapters.MergeAdapter;
import com.debalink.adapters.PublicationsAdapter;
import com.debalink.models.CategoryModel;
import com.debalink.models.PublicationModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class PublicPublicationFragment extends Fragment implements View.OnClickListener {
	private ListView lvPublications;
	private ArrayList<PublicationModel> publicationsPopularList, publicationsFeatureList, publicationsLatestList;
	private MergeAdapter mergeAdapter;
	private ArrayList<CategoryModel> categoryList;
	private AutoCompleteTextView edtCategory;
	private String selectedCatId = "0";

	// private GridView gvPublications;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.public_publication_and_discussion, null);
		lvPublications = (ListView) view.findViewById(R.id.list);
		edtCategory = (AutoCompleteTextView) view.findViewById(R.id.edtCategory);
		// gvPublications = (GridView) view.findViewById(R.id.grid);
		new Async(getActivity(), "Loading Publications", "0").execute();

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

		private String categoryId = "0";
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
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));
				cv.put("SectionId", "POP");
				cv.put("CategoryId", categoryId);

				res = hr.getSoapObjectFromServer(cv, "SelectPublicPublicationBySectionWise");
				publicationsPopularList = hr.parsePublicationResponse(res);

				cv.put("SectionId", "FEA");
				res = hr.getSoapObjectFromServer(cv, "SelectPublicPublicationBySectionWise");
				publicationsFeatureList = hr.parsePublicationResponse(res);

				cv.put("SectionId", "LAT");
				res = hr.getSoapObjectFromServer(cv, "SelectPublicPublicationBySectionWise");
				publicationsLatestList = hr.parsePublicationResponse(res);
				
				categoryList=new ArrayList<CategoryModel>();
				CategoryModel model=new CategoryModel();
				model.setCategoryId("0");
				model.setCategoryName("All");
				categoryList.add(model);
				res = hr.getSoapObjectFromServer(null, "SelectCategoryPublicPublication");
				ArrayList<CategoryModel> categoryList1 = hr.parseCategoryList(res);
				categoryList.addAll(categoryList1);

				Log.i("SelectAllPublicationForDashBoard ", publicationsPopularList.size() + " " + publicationsFeatureList.size() + " " + publicationsLatestList.size());

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

				View v = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
				TextView txt1 = (TextView) v.findViewById(R.id.textView1);

				View v2 = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
				TextView txt2 = (TextView) v2.findViewById(R.id.textView1);

				View v3 = getActivity().getLayoutInflater().inflate(R.layout.text_row, null);
				TextView txt3 = (TextView) v3.findViewById(R.id.textView1);

				txt1.setText("POPULAR PUBLICACTIONS");
				txt2.setText("LATEST PUBLICACTIONS");
				txt3.setText("FEATURE PUBLICACTIONS");

				PublicationsAdapter popularAdapter = new PublicationsAdapter(getActivity(), publicationsPopularList);
				PublicationsAdapter featureAdapter = new PublicationsAdapter(getActivity(), publicationsFeatureList);
				PublicationsAdapter latestrAdapter = new PublicationsAdapter(getActivity(), publicationsLatestList);

				txt1.setTag("1");
				txt2.setTag("2");
				txt3.setTag("3");

				txt1.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));
				txt2.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));
				txt3.setTypeface(Fonts.getRobotoMedium(getActivity().getBaseContext()));

				txt1.setOnClickListener(PublicPublicationFragment.this);
				mergeAdapter = new MergeAdapter();

				if (publicationsPopularList != null && publicationsPopularList.size() > 0) {
					mergeAdapter.addView(v);
					mergeAdapter.addAdapter(popularAdapter);// mergeAdapter.addView(vPopular);
				}
				if (publicationsLatestList != null && publicationsLatestList.size() > 0) {
					mergeAdapter.addView(v2);
					mergeAdapter.addAdapter(latestrAdapter);// mergeAdapter.addView(vFeature);
				}
				if (publicationsFeatureList != null && publicationsFeatureList.size() > 0) {
					mergeAdapter.addView(v3);
					mergeAdapter.addAdapter(featureAdapter);// mergeAdapter.addView(vLatest);
				}
				lvPublications.setAdapter(mergeAdapter);

				edtCategory.setAdapter(new CategoryAdapter(getActivity(), categoryList));
			}

			// gvPublications.setAdapter(new
			// PublicPublicationAdapter(getActivity(),groupTitle,childItem));
		}

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
