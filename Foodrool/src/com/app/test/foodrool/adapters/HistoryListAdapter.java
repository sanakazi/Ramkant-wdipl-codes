package com.app.test.foodrool.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.app.test.foodrool.MainActivity;
import com.app.test.foodrool.OrderDetailsActivity;
import com.app.test.foodrool.R;
import com.app.test.foodrool.model.OrderModel;

@SuppressLint("InflateParams")
public class HistoryListAdapter extends ArrayAdapter<OrderModel> {
	private LayoutInflater inflater;
	private Activity act;
	private ArrayList<OrderModel> countryList;
	private int TYPE;
	public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S";
	public static final String ISO_8601_24H_FULL_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss";

	public HistoryListAdapter(Activity act, ArrayList<OrderModel> countryList) {
		super(act, android.R.layout.simple_list_item_1);
		this.act = act;
		this.countryList = countryList;
		inflater = LayoutInflater.from(act);
	}

	@Override
	public int getCount() {
		return countryList.size();
	}

	@Override
	public OrderModel getItem(int index) {
		return countryList.get(index);
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
			convertView = inflater.inflate(R.layout.history_order_list_row, null);
			viewHolder.txtCustomerName = (TextView) convertView.findViewById(R.id.txtCustomerName);
			viewHolder.txtRestaurantName = (TextView) convertView.findViewById(R.id.txtRestaurantName);
			viewHolder.txtOrderDate = (TextView) convertView.findViewById(R.id.txtOrderDate);
			viewHolder.txtDeliveryDate = (TextView) convertView.findViewById(R.id.txtDeliveryDate);
			viewHolder.txtPaymentMode = (TextView) convertView.findViewById(R.id.txtPaymentMode);
			viewHolder.txtDeliveryCharges = (TextView) convertView.findViewById(R.id.txtDeliveryCharges);
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

		viewHolder.txtCustomerName.setText(order.getFirstName()+" "+order.getLastName());
		viewHolder.txtOrderDate.setText(parseDate(order.getOrderDate()));
		viewHolder.txtDeliveryDate.setText(parseDate2(order.getDeliveryDate()));

		viewHolder.txtRestaurantName.setText(order.getRestaurantName());

		viewHolder.txtOrderNo.setText(order.getOrderNo());

		viewHolder.txtPaymentMode.setText("Payment Mode : " + order.getPaymentMode());
		viewHolder.txtDeliveryCharges.setText("Rs. " + order.getDeliveryCharges());

		viewHolder.chk.setEnabled(false);
		viewHolder.btnView.setVisibility(View.GONE);
		viewHolder.btnUpdateStatus.setVisibility(View.GONE);
		viewHolder.chk.setVisibility(View.GONE);
		viewHolder.btnSpace.setVisibility(View.GONE);
		viewHolder.btnViewAll.setVisibility(View.VISIBLE);
		viewHolder.btnViewAll.setOnClickListener(new ClickListener(position));

		return convertView;

	}

	public static class ViewHolder

	{
		TextView txtOrderNo, txtCustomerName, txtRestaurantName, txtOrderDate, txtDeliveryDate, txtPaymentMode,
				txtDeliveryCharges;
		CheckBox chk;
		Button btnView, btnUpdateStatus, btnSpace, btnViewAll;
	}

	private class ClickListener implements OnClickListener {
		int position;

		public ClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {

			Intent i = new Intent(act, OrderDetailsActivity.class);
			i.putExtra("type", "orderhistory");
			i.putExtra("order", getItem(position));
			i.putExtra("isCurrentOrderSet", true);
			act.startActivityForResult(i, 100);

		}
	};

	private String parseDate(String strDate) {
		final SimpleDateFormat sdfIn = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);
		final SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
		String strOut = null;
		try {
			Date dt = sdfIn.parse(strDate);
			strOut = sdfOut.format(dt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return strOut;
	}

	private String parseDate2(String strDate) {
		final SimpleDateFormat sdfIn = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT2);
		final SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
		String strOut = null;
		try {
			Date dt = sdfIn.parse(strDate);
			strOut = sdfOut.format(dt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return strOut;
	}
}