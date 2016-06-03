package com.app.test.foodrool;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;
import com.app.test.foodrool.menu.Header;
import com.app.test.foodrool.model.OrderModel;
import com.app.test.foodrool.model.OrderedItemModel;
import com.app.test.foodrool.webservices.HttpRequest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetailsActivity extends ActionBarActivity {
	private TextView txtOrderNo, txtRestaurantName, txtRestaurantMob, txtRestaurantMob1, txtRestaurantMob2,
			txtRestaurantAddr;
	private TextView txtCustName, txtCustMob, txtCustAddr, txtPaymentMode, txtInstruction, btnOrderStatus;
	private LinearLayout llOrderDetails, llMain;
	private OrderModel order;
	private ArrayList<OrderedItemModel> itemsList;
	private Button btnAccept, btnReject, btnSetCurrentOrder,btnGetDirection;
	private int status;
	private int RETRY_TYPE;
	private String strReason;
	private static final int TYPE_DETAILS = 0, TYPE_STATUS_UPDATE = 1, TYPE_CURRENT_ORDER_UPDATE = 21;
	private String type;
	private View view;
	private ViewGroup viewGroup;
	private boolean isCurrentOrderSet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_details_new);

		new Header().showMenu(OrderDetailsActivity.this, View.VISIBLE, "Order Detail");

		order = (OrderModel) getIntent().getSerializableExtra("order");
		type = getIntent().getStringExtra("type");
		isCurrentOrderSet = getIntent().getBooleanExtra("isCurrentOrderSet", false);

		viewGroup = (ViewGroup) llMain;
		view = getLayoutInflater().inflate(R.layout.internet_connection_error, null);

		txtOrderNo = (TextView) findViewById(R.id.txtOrderNo);
		txtRestaurantName = (TextView) findViewById(R.id.txtRestaurantName);
		txtRestaurantMob = (TextView) findViewById(R.id.txtCustMob);
		txtRestaurantMob1 = (TextView) findViewById(R.id.txtRestaurantMob1);
		txtRestaurantMob2 = (TextView) findViewById(R.id.txtRestaurantMob2);
		txtRestaurantAddr = (TextView) findViewById(R.id.txtRestaurantAddr);

		txtCustName = (TextView) findViewById(R.id.txtCustName);
		txtCustMob = (TextView) findViewById(R.id.txtRestaurantMob);
		txtCustAddr = (TextView) findViewById(R.id.txtCustAddr);
		txtPaymentMode = (TextView) findViewById(R.id.txtPaymentMode);
		txtInstruction = (TextView) findViewById(R.id.txtInstruction);

		btnAccept = (Button) findViewById(R.id.btnAccept);
		btnReject = (Button) findViewById(R.id.btnReject);
		btnOrderStatus = (Button) findViewById(R.id.btnOrderStatus);
		btnSetCurrentOrder = (Button) findViewById(R.id.btnSetCurrentOrder);
		btnGetDirection= (Button) findViewById(R.id.btnGetDirection);
		
		llOrderDetails = (LinearLayout) findViewById(R.id.ll10);
		llMain = (LinearLayout) findViewById(R.id.main_layout);
		txtOrderNo.setText("" + order.getOrderNo());

		txtRestaurantName.setText("" + order.getRestaurantName());
		txtRestaurantMob.setText("" + order.getCustomerMobileNo());
		txtRestaurantMob1.setText("" + order.getRestaurantContactNo1());
		txtRestaurantMob2.setText("" + order.getRestaurantContactNo2());
		txtRestaurantAddr.setText("" + order.getRestaurantAddress());

		txtCustName.setText("" + order.getFirstName() + " " + order.getLastName());
		txtCustMob.setText("" + order.getRestaurantContactNo());
		txtCustAddr.setText("" + order.getCustomerAddress());
		txtPaymentMode.setText("" + order.getPaymentMode());
		txtInstruction.setText("" + order.getInstruction());

		btnOrderStatus.setText("" + order.getStatus());

		btnAccept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				status = 4;
				new AsyncUpdateOrderStatus().execute();
			}
		});

		btnReject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				status = 5;
				// new AsyncUpdateOrderStatus().execute();
				sendReason();
			}
		});

		txtCustMob.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + order.getRestaurantContactNo()));
				startActivity(intent);
			}
		});

		txtRestaurantMob.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_CALL);

				intent.setData(Uri.parse("tel:" +order.getCustomerMobileNo() ));
				startActivity(intent);
			}
		});
		
		btnGetDirection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(OrderDetailsActivity.this,RouteMapActivity.class);
				intent.putExtra("latitude", order.getCustomerLat());
				intent.putExtra("longitude", order.getCustomerLng());
				intent.setData(Uri.parse("tel:" + order.getRestaurantContactNo()));
				startActivity(intent);
			}
		});

		btnSetCurrentOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new AsyncSetOrderAsCurrent().execute();
			}
		});

		if (type != null && !type.equalsIgnoreCase("allorders")) {
			btnAccept.setVisibility(View.GONE);
			btnReject.setVisibility(View.GONE);

			if (isCurrentOrderSet) {
				btnSetCurrentOrder.setVisibility(View.GONE);
			} else {
				btnSetCurrentOrder.setVisibility(View.VISIBLE);
			}
			btnGetDirection.setVisibility(View.VISIBLE);
		} else if (type != null && !type.equalsIgnoreCase("myorders")) {
			btnOrderStatus.setVisibility(View.GONE);
			btnSetCurrentOrder.setVisibility(View.GONE);
			btnGetDirection.setVisibility(View.VISIBLE);
		}else{
			btnSetCurrentOrder.setVisibility(View.GONE);
			btnOrderStatus.setVisibility(View.VISIBLE);
			btnAccept.setVisibility(View.GONE);
			btnReject.setVisibility(View.GONE);
			btnGetDirection.setVisibility(View.GONE);
		}

		new AsyncGetOrderDetail().execute();

	}

	private class AsyncGetOrderDetail extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			dialog.dismiss();

			if (isSuccess) {
				try {
					View row;
					llOrderDetails.setWeightSum(itemsList.size());
					for (int i = 0; i < itemsList.size(); i++) {
						row = getLayoutInflater().inflate(R.layout.order_item_row, null);
						LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);

						lparams.weight = 1;
						((TextView) row.findViewById(R.id.txtItemName)).setText("" + itemsList.get(i).getItemName());
						((TextView) row.findViewById(R.id.txtItemQuantity))
								.setText("" + itemsList.get(i).getQuantity());
						row.setLayoutParams(lparams);
						// if (i != itemsList.size() - 1) {
						LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
						TextView txt = new TextView(OrderDetailsActivity.this);
						txt.setLayoutParams(txtParam);
						txt.setBackgroundColor(Color.GRAY);

						llOrderDetails.addView(txt);
						llOrderDetails.addView(row);
						// } else {
						// llOrderDetails.addView(row);
						// }

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

			dialog = new ProgressDialog(OrderDetailsActivity.this);
			dialog.setIndeterminate(true);
			// dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();
			RETRY_TYPE = TYPE_DETAILS;
		}

		@Override
		protected String doInBackground(String... params) {
			HttpRequest hr = new HttpRequest();
			try {
				ContentValues cv = new ContentValues();
				String res;

				cv.put("OrderId", order.getOrderId());
				cv.put("DeliveryBoyId", getUserId() + "");
				res = hr.getDataFromServer(cv, "OrderDetails");

				JSONArray jArr = new JSONArray(res);
				itemsList = new ArrayList<OrderedItemModel>();
				for (int i = 0; i < jArr.length(); i++) {
					itemsList.add(OrderedItemModel.parseResult(jArr.getJSONObject(i)));
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

	private class AsyncUpdateOrderStatus extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			dialog.dismiss();

			if (isSuccess && result != null) {
				if (result.equalsIgnoreCase("0")) {
					Toast.makeText(getApplicationContext(), "Order updated successfully", Toast.LENGTH_LONG).show();
					setResult(RESULT_OK);
					finish();
				}
				removeRetry();

			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(OrderDetailsActivity.this);
			dialog.setIndeterminate(true);
			// dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();
			RETRY_TYPE = TYPE_STATUS_UPDATE;

		}

		@Override
		protected String doInBackground(String... params) {
			HttpRequest hr = new HttpRequest();
			try {
				ContentValues cv = new ContentValues();
				String res;
				cv.put("AllocatedTo", getUserId() + "");
				cv.put("OrderId", order.getOrderId());
				cv.put("OrderStatusId", status + "");
				res = hr.getDataFromServer(cv, "OrderAcceptByDeliveryBoy");

				if (status == 5) {
					cv.put("RejectOrderReason", strReason);
					res = hr.getDataFromServer(cv, "RejectedOrderReasonInsert");
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
			} catch (Exception e) {
				isSuccess = false;
				e.printStackTrace();
			}
			return null;
		}
	}

	private class AsyncSetOrderAsCurrent extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;
		private boolean isSuccess = false;

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			dialog.dismiss();

			if (isSuccess && result != null) {
				if (result.equalsIgnoreCase("0")) {
					Toast.makeText(getApplicationContext(), "Order updated successfully", Toast.LENGTH_LONG).show();
					setResult(RESULT_OK);
					finish();
				}
				removeRetry();

			} else {
				showRetryDialog();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(OrderDetailsActivity.this);
			dialog.setIndeterminate(true);
			// dialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();
			RETRY_TYPE = TYPE_CURRENT_ORDER_UPDATE;

		}

		@Override
		protected String doInBackground(String... params) {
			HttpRequest hr = new HttpRequest();
			try {
				ContentValues cv = new ContentValues();
				String res;
				cv.put("OrderId", order.getOrderId());
				cv.put("IsCurrentOrder", "1");
				res = hr.getDataFromServer(cv, "CurrentOrderInsert");

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
		 * if (RETRY_TYPE == TYPE_STATUS_UPDATE) { new
		 * AsyncUpdateOrderStatus().execute(); } else { new
		 * AsyncGetOrderDetail().execute(); }
		 * 
		 * } }); builder.setNegativeButton("Close", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int id) { dialog.cancel(); finish();
		 * } });
		 * 
		 * AlertDialog alert11 = builder.create(); alert11.show();
		 */
		if(viewGroup==null){
			viewGroup = (ViewGroup) llMain;
		}else{
			viewGroup.removeView(view);
		}
		

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		viewGroup.addView(view, params);
		ImageButton btnRetry = (ImageButton) view.findViewById(R.id.btnRetry);

		btnRetry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// viewGroup.removeView(view);
				if (RETRY_TYPE == TYPE_STATUS_UPDATE) {
					new AsyncUpdateOrderStatus().execute();
				} else if (RETRY_TYPE == TYPE_DETAILS) {
					new AsyncGetOrderDetail().execute();
				} else {
					new AsyncSetOrderAsCurrent().execute();
				}
			}
		});
	}

	private void removeRetry() {

		try {
			viewGroup.removeView(view);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void sendReason() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LayoutInflater inflater = getLayoutInflater();
		// this is what I did to added the layout to the alert dialog
		View layout = inflater.inflate(R.layout.reject_reason, null);

		final EditText edtReason = (EditText) layout.findViewById(R.id.edtReason);
		final Button btnSubmit = (Button) layout.findViewById(R.id.btnSubmit);
		final ImageButton btnCancel = (ImageButton) layout.findViewById(R.id.btnCancel);
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				strReason = edtReason.getText().toString();
				if (strReason.equals("")) {
					Toast.makeText(getApplicationContext(), "Please Enter Reason", Toast.LENGTH_LONG).show();
					return;
				}
				dialog.dismiss();

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtReason.getWindowToken(), 0);

				new AsyncUpdateOrderStatus().execute();

			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.setContentView(layout);
		dialog.show();
	}

}
