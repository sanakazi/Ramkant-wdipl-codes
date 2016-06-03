package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.adapters.PublicationsAdapter;
import com.debalink.models.PublicationModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class PublicationsFragment extends Fragment {
	private ListView lvPublications;
	private ArrayList<PublicationModel> publicationsList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_layout, null);

		lvPublications = (ListView) view.findViewById(R.id.list);

		new Async(getActivity(), "Loading Publications").execute();

		view.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {

				return true;
			}

		});

		return view;
	}

	private class Async extends AsynDownloader {
		public Async(Context ctx, String message) {
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
				cv.put("UserId", UserPreferences.getUserId(getActivity().getApplicationContext()));

				res = hr.getSoapObjectFromServer(cv, "SelectAllPublicationForDashBoard");
				publicationsList = hr.parsePublicationResponse(res);

				Log.i("SelectAllPublicationForDashBoard", res.toString());

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
			if (errorFlag == 0)
				lvPublications.setAdapter(new PublicationsAdapter(getActivity(), publicationsList));

		}

	}
}
