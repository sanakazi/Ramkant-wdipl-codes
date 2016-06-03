package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;
import com.debalink.adapters.RecommendUserListAdapter;
import com.debalink.customviews.CustomEditText;
import com.debalink.models.UserModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecommendedUserListFragment extends Fragment {
	private ListView lvUserList;
	private ImageButton btnRecommendPost;
	private ArrayList<UserModel> userList, searchedUserList;
	private CustomEditText edtSearch;
	private ArrayList<String> ids;
	private String id, from;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.recommend_user_list, null);
		lvUserList = (ListView) view.findViewById(R.id.lvUserList);
		btnRecommendPost = (ImageButton) view.findViewById(R.id.btnRecommended);
		edtSearch = (CustomEditText) view.findViewById(R.id.edtSearch);

		id = getArguments().getString("id");
		from = getArguments().getString("from");

		btnRecommendPost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ids = new ArrayList<String>();

				for (UserModel user : searchedUserList) {
					if (user.isChecked()) {
						ids.add(user.getUserId());
					}
				}

				if (ids.size() == 0) {
					showMessage("Please select atleast one user");
				} else {
					new AsyncRecommend(getActivity(), "Recommending " + from, id).execute();

				}
			}
		});

		edtSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				searchText(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		new AsyncUserList(getActivity(), "Getting user list", "").execute();

		return view;
	}

	private void searchText(CharSequence cs) {

		searchedUserList = new ArrayList<UserModel>();
		for (UserModel c : userList) {
			if (c.getUserName().toLowerCase().contains(cs.toString().toLowerCase())) {
				searchedUserList.add(c);
			}
		}

		lvUserList.setAdapter(new RecommendUserListAdapter(getActivity(), searchedUserList));

	}

	private class AsyncUserList extends AsyncTask<Void, Void, ArrayList<UserModel>> {
		private SoapObject res;
		private int errorFlag = 0;
		private ProgressDialog pd;

		public AsyncUserList(Context ctx, String message, String publicationId) {

			pd = new ProgressDialog(ctx);
			pd.setMessage(message);
			pd.setTitle("Please Wait");
			pd.setCanceledOnTouchOutside(false);
			pd.setCancelable(false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		}

		@Override
		protected ArrayList<UserModel> doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();

			try {

				ContentValues cv = new ContentValues();
				cv.put("SubscriberSenderId", UserPreferences.getUserId(getActivity().getApplicationContext()));

				res = hr.getSoapObjectFromServer(cv, "RecommedationPopUpByUserID");

				userList = hr.parseUserList(res);
				searchedUserList = userList;
				Log.i("RecommedationPopUpByUserID", res.toString());

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
			return userList;
		}

		@Override
		protected void onPostExecute(ArrayList<UserModel> result) {
			super.onPostExecute(result);
			try {
				if (errorFlag == 0) {
					if (pd != null && pd.isShowing())
						pd.dismiss();
					lvUserList.setAdapter(new RecommendUserListAdapter(getActivity(), userList));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private class AsyncRecommend extends AsynDownloader {
		public AsyncRecommend(Context ctx, String message, String id) {
			super(ctx, message);
			this.id = id;
		}

		private String res = "";
		private int errorFlag = 0;
		private String id;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			HttpRequest hr = new HttpRequest();
			try {
				String strIds = ids.toString();
				strIds = strIds.replace("[", "");
				strIds = strIds.replace("]", "");

				if (from.equals("publication")) {

					ContentValues cv = new ContentValues();
					cv.put("SenderId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					cv.put("PublicationId", id);
					cv.put("ReceiverId", strIds);
					res = hr.getDataFromServer(cv, "InsertPublicationRecommend");
				} else if (from.equals("discussion")) {

					ContentValues cv = new ContentValues();
					cv.put("SenderId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					cv.put("DiscussionId", id);
					cv.put("ReceiverId", strIds);
					res = hr.getDataFromServer(cv, "InsertDiscussionRecommend");
				} else if (from.equals("headline")) {

					ContentValues cv = new ContentValues();
					cv.put("SenderId", UserPreferences.getUserId(getActivity().getApplicationContext()));
					cv.put("HeadlineId", id);
					cv.put("ReceiverId", strIds);
					res = hr.getDataFromServer(cv, "InsertHeadlineIdRecommend");
				}

				Log.i("Insert", res.toString());

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
			try {
				if (errorFlag == 0 && res.equals("0")) {
					// showMessage("Discussion submitted successfully");
					getFragmentManager().beginTransaction().remove(RecommendedUserListFragment.this).commit();
					// (HomeActivity) getActivity()).showLastFramgent();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void showMessage(String message) {
		// Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		// //toast.setGravity(Gravity.CENTER, 0, 0);
		// toast.show();

		View view = getActivity().getLayoutInflater().inflate(R.layout.custom_toast, null);
		TextView txtMessage = (TextView) view.findViewById(R.id.textView1);
		txtMessage.setTypeface(Fonts.getBold(getActivity()));

		txtMessage.setText(message);
		Toast toast = new Toast(getActivity());
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
