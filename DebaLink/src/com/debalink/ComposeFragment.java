package com.debalink;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.xmlpull.v1.XmlPullParserException;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.debalink.adapters.AutoCompleteAdapters;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.UserPreferences;
import com.debalink.webservices.HttpRequest;

public class ComposeFragment extends Fragment {
	private ImageButton btnSend, btnCancel;
	private EditText edtSubject, edtCommnets;
	private AutoCompleteTextView edtTo;
	private TextView txtTo,txtSubject;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.compose_message, null);
		btnSend = (ImageButton) view.findViewById(R.id.btnSend);
		btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
		edtTo = (AutoCompleteTextView) view.findViewById(R.id.edtTo);
		edtSubject = (EditText) view.findViewById(R.id.edtSubject);
		edtCommnets = (EditText) view.findViewById(R.id.edtComments);
		
		txtTo=(TextView)view.findViewById(R.id.txtTo);
		txtSubject=(TextView)view.findViewById(R.id.txtSubject);

		// edtTo.addTextChangedListener(new GetUsersList());
		edtTo.setThreshold(2);
		edtTo.setAdapter(new AutoCompleteAdapters(getActivity()));

		edtTo.getText().toString();
		
		edtTo.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtSubject.setTypeface(Fonts.getRobotoMedium(getActivity()));
		edtCommnets.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtTo.setTypeface(Fonts.getRobotoMedium(getActivity()));
		txtSubject.setTypeface(Fonts.getRobotoMedium(getActivity()));
		
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (edtTo.getText().toString().isEmpty()) {
					Toast.makeText(getActivity(), "Enter email id", Toast.LENGTH_LONG).show();
					return;
				}
				if (edtCommnets.getText().toString().isEmpty()) {
					Toast.makeText(getActivity(), "Enter Commnets id", Toast.LENGTH_LONG).show();
					return;
				}

				if (edtSubject.getText().toString().isEmpty()) {
					Toast.makeText(getActivity(), "Enter Subject id", Toast.LENGTH_LONG).show();
					return;
				}
				if (!isValidEmail(edtTo.getText().toString())) {
					
					Toast.makeText(getActivity(), "Email not valid", Toast.LENGTH_LONG).show();
					return;
				}
				new Async(getActivity(), "Sending message").execute();
			}
		});

		return view;
	}

	private class Async extends AsynDownloader {

		public Async(Context ctx, String message) {
			super(ctx, message);

		}

		private String res;
		private int errorFlag = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("SenderId", UserPreferences.getUserId(getActivity()));
				cv.put("ReceiverEmailId", edtTo.getText().toString());
				cv.put("Subject", edtSubject.getText().toString());
				cv.put("Description", edtCommnets.getText().toString());

				res = hr.getDataFromServer(cv, "InsertMessage");
				

				Log.i("SelectUserInbox", res.toString());

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

			int response = Integer.parseInt(res);

			if (errorFlag == 0 && response == 2) {
				Toast.makeText(getActivity(), "Email Id not available", Toast.LENGTH_LONG).show();
			} else if (errorFlag == 0 && response == 0) {
				Toast.makeText(getActivity(), "Message sent successfully", Toast.LENGTH_LONG).show();
			} else if (errorFlag == 0 && response == 1) {
				Toast.makeText(getActivity(), "Unknown Error", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(), "Connection Problem", Toast.LENGTH_LONG).show();
			}

		}

	}
	public final static boolean isValidEmail(String target) {
	    if(target == null) {
	        return false;
	    }else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}
}
