package com.debalink.adapters;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.debalink.R;
import com.debalink.models.SearchModel;
import com.debalink.utils.Fonts;
import com.debalink.webservices.HttpRequest;

public class GlobalSearchAdapter extends ArrayAdapter<SearchModel> {
	private Filter mFilter;
	private Context context;
	private List<SearchModel> mSubData = new ArrayList<SearchModel>();
	static int counter = 0;

	public GlobalSearchAdapter(Context context) {
		super(context, R.layout.text_row, R.id.textView1);
		this.context = context;
		setNotifyOnChange(false);

		mFilter = new Filter() {
			private int c = ++counter;
			private List<SearchModel> mData = new ArrayList<SearchModel>();

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// This method is called in a worker thread
				mData.clear();

				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					try {

						mData = new Async().execute(new String[] { constraint + "" }).get();

					} catch (Exception e) {
					}

					filterResults.values = mData;
					filterResults.count = mData.size();
				}
				return filterResults;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence contraint, FilterResults results) {
				if (c == counter) {
					mSubData.clear();
					if (results != null && results.count > 0) {
						ArrayList<SearchModel> objects = (ArrayList<SearchModel>) results.values;
						for (SearchModel v : objects)
							mSubData.add(v);
						
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			}
		};
	}

	@Override
	public int getCount() {
		return mSubData.size();
	}

	private class ViewHolder {
		TextView txt;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.global_search_list_row, null);
			viewHolder.txt = (TextView) convertView.findViewById(R.id.textView1);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txt.setTypeface(Fonts.getRobotoMedium(context));
		viewHolder.txt.setText(getItem(position).getName());
		return convertView;
	}

	@Override
	public SearchModel getItem(int index) {
		return mSubData.get(index);
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}
	@Override
	public void clear() {
		super.clear();
		mSubData.clear();
		notifyDataSetChanged();
	}

	private class Async extends AsyncTask<String, Void, ArrayList<SearchModel>> {

		private SoapObject res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected ArrayList<SearchModel> doInBackground(String... param) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("prefixText", param[0]);

				res = hr.getSoapObjectFromServer(cv, "SearchGlobally");

				Log.i("SearchGlobally", res.toString());
				return hr.parseGlobalSearchResponse(res);

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
			return new ArrayList<SearchModel>();
		}

		@Override
		protected void onPostExecute(ArrayList<SearchModel> result) {
			super.onPostExecute(result);
		}

	}

}