package com.app.test.foodrool;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListView;

import com.app.test.foodrool.adapters.HistoryListAdapter;
import com.app.test.foodrool.adapters.OrderListAdapter;
import com.app.test.foodrool.gcm.RegistrationIntentService;
import com.app.test.foodrool.location.LocationCheck;
import com.app.test.foodrool.location.LocationService;
import com.app.test.foodrool.menu.Header;
import com.app.test.foodrool.model.OrderModel;
import com.app.test.foodrool.model.OrderStatusModel;
import com.app.test.foodrool.webservices.HttpRequest;

public class MainActivity extends ActionBarActivity {
	public static int TYPE = 0;
	public static final int ALL_ORDERS = 0, MY_ORDERS = 1, HISTORY_ORDERS = 2;
	private ListView lvOrderList;
	private ArrayList<OrderModel> arrOrders;
	private ArrayList<OrderStatusModel> arrOrderStatus;
	private View view;
	private ViewGroup viewGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent service = new Intent(this, LocationService.class);
		startService(service);
		sendTokenToServer();

		view = getLayoutInflater().inflate(R.layout.internet_connection_error, null);

		if (getIntent().hasExtra("type") && getIntent().getStringExtra("type").equals("my_orders")) {
			TYPE = MY_ORDERS;
			new Header().showMenu(MainActivity.this, View.VISIBLE, "My Orders");
		} else if (TYPE == ALL_ORDERS) {
			TYPE = ALL_ORDERS;
			new Header().showMenu(MainActivity.this, View.VISIBLE, "Order List");
		} else if (TYPE == HISTORY_ORDERS) {
			new Header().showMenu(MainActivity.this, View.VISIBLE, "History Order");
			TYPE = HISTORY_ORDERS;
		}

		lvOrderList = (ListView) findViewById(R.id.lvList);
		LocationCheck l = new LocationCheck(this);
		boolean check = l.isLocationServiceAvailable();
		if (!check) {
			l.createLocationServiceError(this);
		} else {
			new AsyncGetOrders().execute();
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (TYPE == MY_ORDERS) {

			new Header().showMenu(MainActivity.this, View.VISIBLE, "My Orders");
		} else if (TYPE == ALL_ORDERS) {

			new Header().showMenu(MainActivity.this, View.VISIBLE, "Order List");
		} else if (TYPE == HISTORY_ORDERS) {
			new Header().showMenu(MainActivity.this, View.VISIBLE, "History Order");
		}

		new AsyncGetOrders().execute();
	}

	private class AsyncGetOrders extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private boolean isSuccess = false;
		private boolean isCurrentOrderSet = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			dialog.dismiss();

			if (isSuccess) {
				try {

					if (arrOrders == null || arrOrders.size() == 0) {
						View view = getLayoutInflater().inflate(R.layout.empty_list_view, null);
						ViewGroup viewGroup = (ViewGroup) lvOrderList.getParent();
						LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						viewGroup.addView(view, params);
						lvOrderList.setEmptyView(view);
					} else {
						View view = getLayoutInflater().inflate(R.layout.empty_list_view, null);
						ViewGroup viewGroup = (ViewGroup) lvOrderList.getParent();
						viewGroup.removeView(view);
					}
					if (TYPE == HISTORY_ORDERS) {
						HistoryListAdapter adapter = new HistoryListAdapter(MainActivity.this, arrOrders);
						lvOrderList.setAdapter(adapter);
					} else {
						OrderListAdapter adapter = new OrderListAdapter(MainActivity.this, view, lvOrderList,
								arrOrders, TYPE);
						adapter.setOrderStatusList(arrOrderStatus);
						adapter.setCurrentOrderSet(isCurrentOrderSet);
						lvOrderList.setAdapter(adapter);
					}

					removeRetry();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(MainActivity.this);
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
				String res = null;
				cv.put("DeliveryBoyId", getUserId() + "");
				if (TYPE == ALL_ORDERS) {
					res = hr.getDataFromServer(cv, "DeliveryBoyAllOrdersList");
				} else if (TYPE == MY_ORDERS) {
					res = hr.getDataFromServer(cv, "OrderListByDeliveryBoyId");
				} else if (TYPE == HISTORY_ORDERS) {
					res = hr.getDataFromServer(cv, "OrderHistoryByDeliveryBoyId");
				}
				isCurrentOrderSet = false;
				JSONArray jArr = new JSONArray(res);
				arrOrders = new ArrayList<OrderModel>();
				for (int i = 0; i < jArr.length(); i++) {
					if (TYPE == HISTORY_ORDERS) {
						OrderModel model = OrderModel.parseHistoryResult(jArr.getJSONObject(i));
						arrOrders.add(model);
					} else {
						OrderModel model = OrderModel.parseResult(jArr.getJSONObject(i));
						arrOrders.add(model);
						if (!isCurrentOrderSet && model.isCurrentOrder()) {
							isCurrentOrderSet = true;
						}
					}

				}

				if (TYPE == MY_ORDERS) {
					res = hr.getDataFromServer(null, "OrderStatusSelectAll");
					arrOrderStatus = new ArrayList<OrderStatusModel>();
					jArr = new JSONArray(res);

					for (int i = 0; i < jArr.length(); i++) {
						arrOrderStatus.add(OrderStatusModel.parseResult(jArr.getJSONObject(i)));
					}
				}

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
			} catch (JSONException e) {
				isSuccess = false;
				e.printStackTrace();
			} catch (Exception e) {
				isSuccess = false;
				e.printStackTrace();
			}
			return null;
		}
	}

	private int getUserId() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);

		return Integer.parseInt(sp.getString("user_id", "-1"));
	}

	private void showRetryDialog() {
		/*
		 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 * builder.setMessage("Problem getting data");
		 * builder.setCancelable(true); builder.setPositiveButton("Retry", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int id) { dialog.cancel();
		 * 
		 * } }); builder.setNegativeButton("Close", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int id) { dialog.cancel(); finish();
		 * } });
		 * 
		 * AlertDialog alert11 = builder.create(); alert11.show();
		 */

		try {
			if (viewGroup == null) {
				viewGroup = (ViewGroup) lvOrderList.getParent();
			} else {
				viewGroup.removeView(view);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		viewGroup = (ViewGroup) lvOrderList.getParent();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		viewGroup.addView(view, params);
		ImageButton btnRetry = (ImageButton) view.findViewById(R.id.btnRetry);

		btnRetry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// viewGroup.removeView(view);

				new AsyncGetOrders().execute();
			}
		});

	}

	private void removeRetry() {

		// ViewGroup viewGroup = (ViewGroup) lvOrderList.getParent();
		try {
			viewGroup.removeView(view);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void sendTokenToServer() {
		SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
		if (sp.getBoolean("device_id_sent_to_server", false)) {
			RegistrationIntentService.sendRegistrationToServer(this, sp.getString("device_id", "-1"), getUserId());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == 100) {
			new AsyncGetOrders().execute();
		}
	}

	public void updateList() {
		new AsyncGetOrders().execute();
	}
}
