package com.app.test.foodrool.adapters;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.app.test.foodrool.MainActivity;
import com.app.test.foodrool.OrderDetailsActivity;
import com.app.test.foodrool.R;
import com.app.test.foodrool.model.OrderModel;
import com.app.test.foodrool.model.OrderStatusModel;
import com.app.test.foodrool.webservices.HttpRequest;

@SuppressLint("InflateParams")
public class OrderListAdapter extends ArrayAdapter<OrderModel> {
	private LayoutInflater inflater;
	private Activity act;
	private ArrayList<OrderModel> countryList;
	private ArrayList<OrderStatusModel> arrOrderStatus;
	private CharSequence[] items;
	private int TYPE;
	private View view;
	private ListView lvListView;
	private boolean isCurrentOrderSet = false;

	public OrderListAdapter(Activity act, View view, ListView lvListView, ArrayList<OrderModel> countryList, int type) {
		super(act, android.R.layout.simple_list_item_1);
		this.act = act;
		this.countryList = countryList;
		inflater = LayoutInflater.from(act);
		TYPE = type;
		this.view = view;
		this.lvListView = lvListView;
	}

	@Override
	public int getCount() {
		return countryList.size();
	}

	@Override
	public OrderModel getItem(int index) {
		return countryList.get(index);
	}

	public void setOrderStatusList(ArrayList<OrderStatusModel> arrOrderStatus) {
		this.arrOrderStatus = arrOrderStatus;

	}

	public void setCurrentOrderSet(boolean isCurrentOrderSet) {
		this.isCurrentOrderSet = isCurrentOrderSet;
	}

	@Override
	public int getItemViewType(int position) {
		return TYPE;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		OrderModel order = getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_order_row_new, null);
			viewHolder.txtToName = (TextView) convertView.findViewById(R.id.txtToName);
			viewHolder.txtToAddr = (TextView) convertView.findViewById(R.id.txtToAddr);
			viewHolder.txtToMobNo = (TextView) convertView.findViewById(R.id.txtToMob);

			viewHolder.txtFromName = (TextView) convertView.findViewById(R.id.txtFromName);
			viewHolder.txtFromAddr = (TextView) convertView.findViewById(R.id.txtFromAddr);
			viewHolder.txtFromMobNo = (TextView) convertView.findViewById(R.id.txtFromMob);
			viewHolder.txtPaymentMode = (TextView) convertView.findViewById(R.id.txtPaymentMode);
			viewHolder.txtInstruction = (TextView) convertView.findViewById(R.id.txtInstructino);
			viewHolder.btnView = (Button) convertView.findViewById(R.id.btnView);
			viewHolder.btnViewAll = (Button) convertView.findViewById(R.id.btnViewAll);
			viewHolder.btnSpace = (Button) convertView.findViewById(R.id.btnSpace);
			viewHolder.btnUpdateStatus = (Button) convertView.findViewById(R.id.btnUpdateStatus);

			viewHolder.txtOrderNo = (TextView) convertView.findViewById(R.id.txtOrderNo);
			viewHolder.chk = (CheckBox) convertView.findViewById(R.id.chkIsCurrent);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.txtFromName.setText(order.getFirstName() + " " + order.getLastName());
		viewHolder.txtFromAddr.setText(order.getRestaurantAddress());
		viewHolder.txtFromMobNo.setText(order.getRestaurantContactNo());

		viewHolder.txtToName.setText(order.getRestaurantName());
		viewHolder.txtToAddr.setText(order.getCustomerAddress());
		viewHolder.txtToMobNo.setText(order.getCustomerMobileNo());

		viewHolder.txtOrderNo.setText(order.getOrderNo());

		viewHolder.txtPaymentMode.setText("Payment Mode : " + order.getPaymentMode());
		viewHolder.txtInstruction.setText("Amount : " + order.getInstruction());

		// viewHolder.txtToName.setTypeface(Fonts.getSansLight(context));
		
		if (getItemViewType(position) == MainActivity.MY_ORDERS) {
			viewHolder.btnView.setOnClickListener(new ClickOrderListener(position));
			viewHolder.chk.setVisibility(View.VISIBLE);
			viewHolder.chk.setChecked(order.isCurrentOrder());
			viewHolder.chk.setEnabled(false);
			viewHolder.btnView.setVisibility(View.VISIBLE);
			viewHolder.btnUpdateStatus.setVisibility(View.VISIBLE);
			viewHolder.btnViewAll.setVisibility(View.GONE);
			viewHolder.btnUpdateStatus.setOnClickListener(new ClickListener(position));
		} else {
			viewHolder.chk.setVisibility(View.GONE);
			viewHolder.btnView.setVisibility(View.GONE);
			viewHolder.btnUpdateStatus.setVisibility(View.GONE);
			viewHolder.btnSpace.setVisibility(View.GONE);
			viewHolder.btnViewAll.setVisibility(View.VISIBLE);
			viewHolder.btnViewAll.setOnClickListener(new ClickListener(position));
		}
		// viewHolder.chk.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		//
		// }
		// });
		return convertView;

	}

	public static class ViewHolder

	{
		TextView txtOrderNo, txtToName, txtToAddr, txtToMobNo, txtFromName, txtFromAddr, txtFromMobNo, txtPaymentMode,
				txtInstruction;
		CheckBox chk;
		Button btnView, btnUpdateStatus,btnSpace,btnViewAll;
	}

	private class ClickListener implements OnClickListener {
		int position;
		String orderStatusId;

		public ClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			boolean show = false;

			if (getItem(position).getStatus().contains("Delivered")
					|| getItem(position).getStatus().contains("Delivery")) {

			}

			if (arrOrderStatus != null) {
				// items = new CharSequence[1];
				for (int i = 0; i < arrOrderStatus.size(); i++) {
					if (show && i < arrOrderStatus.size()) {
						items = new CharSequence[1];
						items[0] = arrOrderStatus.get(i).getStatusText();
						orderStatusId = arrOrderStatus.get(i).getStatusId();
						break;
					}
					if (arrOrderStatus.get(i).getStatusText().equalsIgnoreCase(getItem(position).getStatus())) {
						show = true;
					}
					Log.e("", "Cur status-" + getItem(position).getStatus() + "," + " i-"
							+ arrOrderStatus.get(i).getStatusText());
				}
				if (items == null || items.length == 0) {
					items = new CharSequence[1];
					items[0] = arrOrderStatus.get(0).getStatusText();
					orderStatusId = arrOrderStatus.get(0).getStatusId();
				}
			}

			if (TYPE == MainActivity.ALL_ORDERS) {
				Intent i = new Intent(act, OrderDetailsActivity.class);
				i.putExtra("type", "allorders");
				i.putExtra("order", getItem(position));
				i.putExtra("isCurrentOrderSet", isCurrentOrderSet);
				act.startActivityForResult(i, 100);
			}else if (TYPE == MainActivity.MY_ORDERS) {
				if (items == null) {
					return;
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(act);
				builder.setTitle("Pick Status");
				builder.setItems(items, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int item) {
						new AsyncUpdateOrderStatus(getItem(position).getOrderId(), orderStatusId).execute();
					}

				});

				AlertDialog alert = builder.create();

				alert.show();
			}else if(TYPE==MainActivity.HISTORY_ORDERS){
				Intent i = new Intent(act, OrderDetailsActivity.class);
				i.putExtra("type", "orderhistory");
				i.putExtra("order", getItem(position));
				i.putExtra("isCurrentOrderSet", true);
				act.startActivityForResult(i, 100);
			}

		}
	};

	private class ClickOrderListener implements OnClickListener {
		int position;

		public ClickOrderListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {

			if (TYPE == MainActivity.ALL_ORDERS) {
				Intent i = new Intent(act, OrderDetailsActivity.class);
				i.putExtra("order", getItem(position));
				i.putExtra("type", "allorders");
				i.putExtra("isCurrentOrderSet", isCurrentOrderSet);
				act.startActivityForResult(i, 100);
			} else {
				Intent i = new Intent(act, OrderDetailsActivity.class);
				i.putExtra("order", getItem(position));
				i.putExtra("type", "myorders");
				i.putExtra("isCurrentOrderSet", isCurrentOrderSet);
				act.startActivityForResult(i, 100);
			}

		}
	};

	private class AsyncUpdateOrderStatus extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private boolean isSuccess = false;
		private String orderId, statusId;

		AsyncUpdateOrderStatus(String orderId, String statusId) {
			this.orderId = orderId;
			this.statusId = statusId;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			dialog.dismiss();

			if (isSuccess && result != null) {
				if (result.equalsIgnoreCase("0")) {
					Toast.makeText(act, "Order updated successfully", Toast.LENGTH_LONG).show();
					((MainActivity) act).updateList();
				}
				removeRetry();
			} else {
				showRetryDialog(orderId, statusId);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(act);
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

				cv.put("OrderId", orderId);
				cv.put("OrderStatusId", statusId);
				res = hr.getDataFromServer(cv, "OrderStatusUpdate");

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

	private void showRetryDialog(final String orderId, final String statusId) {
		/*
		 * AlertDialog.Builder builder = new AlertDialog.Builder(act);
		 * builder.setMessage("Problem updating status");
		 * builder.setCancelable(true); builder.setPositiveButton("Retry", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int id) { dialog.cancel();
		 * 
		 * 
		 * 
		 * } }); builder.setNegativeButton("Close", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int id) { dialog.cancel();
		 * 
		 * } });
		 * 
		 * AlertDialog alert11 = builder.create(); alert11.show();
		 */
		ViewGroup viewGroup = (ViewGroup) lvListView.getParent();
		try {
			viewGroup.removeView(view);
		} catch (Exception e) {
			// TODO: handle exception
		}

		viewGroup = (ViewGroup) lvListView.getParent();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		viewGroup.addView(view, params);
		ImageButton btnRetry = (ImageButton) view.findViewById(R.id.btnRetry);

		btnRetry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// viewGroup.removeView(view);

				new AsyncUpdateOrderStatus(orderId, statusId).execute();
			}
		});
	}

	private void removeRetry() {

		ViewGroup viewGroup = (ViewGroup) lvListView.getParent();
		try {
			viewGroup.removeView(view);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}