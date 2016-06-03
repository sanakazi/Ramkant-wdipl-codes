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
import android.widget.ArrayAdapter;
import android.widget.Filter;
import com.debalink.webservices.HttpRequest;

public class AutoCompleteAdapters extends ArrayAdapter<String> {
	private Filter mFilter;
	//private Context context;
	private List<String> mSubData = new ArrayList<String>();
	static int counter = 0;

	public AutoCompleteAdapters(Context context) {
		super(context, android.R.layout.simple_list_item_1);
		//this.context = context;
		setNotifyOnChange(false);

		mFilter = new Filter() {
			private int c = ++counter;
			private List<String> mData = new ArrayList<String>();

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// This method is called in a worker thread
				mData.clear();

				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					try {

						mData=	new Async().execute(new String[]{constraint+""}).get();

						
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
						ArrayList<String> objects = (ArrayList<String>) results.values;
						for (String v : objects)
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

	@Override
	public String getItem(int index) {
		return mSubData.get(index);
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}

	private class Async extends AsyncTask<String, Void, ArrayList<String>> {

		private SoapObject res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(String... param) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("prefixText", param[0]);

				res = hr.getSoapObjectFromServer(cv, "GetCompletionList");
				// messageList = hr.parseMessageResponse(res);
				Log.i("SelectUserInbox", res.toString());
				return getUserList(res);

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
			return new ArrayList<String>();
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			super.onPostExecute(result);
		}

	}

	private ArrayList<String> getUserList(SoapObject res) {
		ArrayList<String> userList = new ArrayList<String>();

		for (int i = 0; i < res.getPropertyCount(); i++) {
			
			
			userList.add(res.getProperty(i).toString());
		}

		return userList;
	}
}