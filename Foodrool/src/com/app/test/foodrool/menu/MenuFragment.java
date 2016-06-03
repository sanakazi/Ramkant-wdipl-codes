package com.app.test.foodrool.menu;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.xmlpull.v1.XmlPullParserException;
import com.app.test.foodrool.ContactAdminActvity;
import com.app.test.foodrool.LoginActivity;
import com.app.test.foodrool.MainActivity;
import com.app.test.foodrool.R;
import com.app.test.foodrool.webservices.HttpRequest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class MenuFragment extends Fragment {
	private ImageButton btnLogout, btnRequestedOrders, btnMyOrders, btnHome, btnContactAdmin, btnOrderHistory;
	public static int from = 0;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.siderbar_new, null);
		btnLogout = (ImageButton) view.findViewById(R.id.btnLogout);
		btnRequestedOrders = (ImageButton) view.findViewById(R.id.btnRequestedOrders);
		btnMyOrders = (ImageButton) view.findViewById(R.id.btnMyOrders);
		btnHome = (ImageButton) view.findViewById(R.id.btnHome);
		btnContactAdmin = (ImageButton) view.findViewById(R.id.btnContactAdmin);
		btnOrderHistory = (ImageButton) view.findViewById(R.id.btnOrderHistory);
		// btnLogout.setTypeface(Fonts.getSansLight(getActivity()));
		// btnRequestedOrders.setTypeface(Fonts.getSansLight(getActivity()));
		// btnMyOrders.setTypeface(Fonts.getSansLight(getActivity()));

		btnLogout.setSelected(false);
		btnRequestedOrders.setSelected(false);
		btnMyOrders.setSelected(false);
		if (from == 0) {
			btnRequestedOrders.setSelected(true);
		} else if (from == 1) {
			btnMyOrders.setSelected(true);
		}

		btnLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				from = 3;
				btnLogout.setSelected(true);
				new AsyncLogoutStatus().execute();
			}
		});
		btnRequestedOrders.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
				MainActivity.TYPE = MainActivity.ALL_ORDERS;
				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.putExtra("type", "all_orders");
				startActivity(intent);
				from = 0;

				btnRequestedOrders.setSelected(true);
			}
		});
		btnHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
				MainActivity.TYPE = MainActivity.ALL_ORDERS;
				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.putExtra("type", "all_orders");
				startActivity(intent);
				from = 0;

				btnRequestedOrders.setSelected(true);
			}
		});

		btnMyOrders.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.TYPE = MainActivity.MY_ORDERS;
				getActivity().finish();
				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.putExtra("type", "my_orders");
				startActivity(intent);
				from = 1;
				btnMyOrders.setSelected(true);
			}
		});

		btnOrderHistory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.TYPE = MainActivity.HISTORY_ORDERS;
				getActivity().finish();
				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.putExtra("type", "order_history");
				startActivity(intent);
				from = 1;
				btnMyOrders.setSelected(true);
			}
		});

		btnContactAdmin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
				Intent intent = new Intent(getActivity(), ContactAdminActvity.class);
				startActivity(intent);
				from = 1;
				btnMyOrders.setSelected(true);
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private class AsyncLogoutStatus extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			dialog.dismiss();

			if (isSuccess && result != null) {

				if (result.contains("0")) {
					getActivity().finish();
					SharedPreferences sp = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
					sp.edit().clear().commit();
					Toast.makeText(getContext(), "You logged out successfully", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);

				}

			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(getActivity());
			dialog.setIndeterminate(true);
			// dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			HttpRequest hr = new HttpRequest();
			try {
				ContentValues cv = new ContentValues();
				String res;
				cv.put("UserId", getUserId() + "");
				res = hr.getDataFromServer(cv, "LogOutUser");

				isSuccess = true;
				return res;
			} catch (SocketTimeoutException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (SocketException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (IOException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (Exception e) {
				isSuccess = false;
				e.printStackTrace();
			}
			return null;
		}
	}

	private void showRetryDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Problem getting data");
		builder.setCancelable(true);
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				new AsyncLogoutStatus().execute();
			}
		});
		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();

			}
		});

		AlertDialog alert11 = builder.create();
		alert11.show();
	}

	private int getUserId() {
		SharedPreferences sp = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);

		return Integer.parseInt(sp.getString("user_id", "-1"));
	}
}
