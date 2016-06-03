package com.wdipl.adapter;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.wdipl.bloodbank.BloodbankDetails;
import com.wdipl.bloodbank.R;
import com.wdipl.json.BloodBankModel;

public class BloodbankListAdapter extends ArrayAdapter<BloodBankModel> {
	private Activity context;
	private int layout;
	private LayoutInflater inflater;
	private ArrayList<BloodBankModel> list;
	private String state, district;

	public BloodbankListAdapter(Activity context, int resource, ArrayList<BloodBankModel> objects, String state, String district) {
		super(context, resource, objects);
		this.context = context;
		this.layout = resource;
		this.state = state;
		this.district = district;
		list = objects;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		Holder holder = new Holder();
		if (convertView == null) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layout, null);
			holder.txtname = (TextView) convertView.findViewById(R.id.txtname);
			holder.txtMobile = (TextView) convertView.findViewById(R.id.txtMobile);

			holder.llCustomeView = (View) convertView.findViewById(R.id.llCustomView);
			holder.btnView = (ImageButton) convertView.findViewById(R.id.btnView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		BloodBankModel temp = new BloodBankModel();
		temp = list.get(position);
		holder.txtname.setText(temp.getBloodbankName());
		holder.txtMobile.setText(temp.getMobileNo());
		holder.btnView.setOnClickListener(new OnViewClickListerner(position));
		holder.txtMobile.setOnClickListener(new OnCallClickListerner(position));
		return convertView;
	}

	class Holder {
		TextView txtname, txtMobile;
		View llCustomeView;
		ImageButton btnView;
	}

	private class OnViewClickListerner implements OnClickListener {
		private int position;

		public OnViewClickListerner(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(context, BloodbankDetails.class);
			i.putExtra("contact_no", list.get(position).getContactNo());
			i.putExtra("BankName", list.get(position).getBloodbankName());
			i.putExtra("contact_person", list.get(position).getContactPerson());
			i.putExtra("id", list.get(position).getBloodbankId());
			i.putExtra("mobile_no", list.get(position).getMobileNo());
			i.putExtra("state", state);
			i.putExtra("district", district);
			context.startActivity(i);
		}
	}

	private class OnCallClickListerner implements OnClickListener {
		private int position;

		public OnCallClickListerner(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {

			new AlertDialog.Builder(context).setTitle("Alert").setMessage("Are you sure you want to call "+list.get(position).getMobileNo()+"?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
							callIntent.setData(Uri.parse("tel:" + list.get(position).getMobileNo()));
							callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(callIntent);
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					}).setIcon(android.R.drawable.ic_dialog_alert).show();
		}
	}

}
