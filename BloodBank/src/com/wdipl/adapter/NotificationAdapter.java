package com.wdipl.adapter;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.wdipl.bloodbank.R;
import com.wdipl.json.Database;
import com.wdipl.json.DonorDetail;
import com.wdipl.json.JsonParse;

public class NotificationAdapter extends ArrayAdapter<DonorDetail> {
	Activity context;
	int layout;
	LayoutInflater inflater;
	ArrayList<DonorDetail> list;
	View num;

	// private GestureDetector gestureDetector;

	public NotificationAdapter(Activity context, int resource, ArrayList<DonorDetail> objects) {
		super(context, resource, objects);
		this.context = context;
		this.layout = resource;
		list = objects;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		Holder holder = new Holder();
		if (convertView == null) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layout, null);
			holder.img = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.txtname = (TextView) convertView.findViewById(R.id.txtname);
			holder.txtnumber = (TextView) convertView.findViewById(R.id.txtNumber);
			holder.txtstatus = (TextView) convertView.findViewById(R.id.txtStatus);

			holder.llCustomeView = (View) convertView.findViewById(R.id.llCustomView);
			holder.btnView = (ImageButton) convertView.findViewById(R.id.btnView);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		DonorDetail temp = new DonorDetail();
		temp = list.get(position);
		holder.txtname.setText(temp.getDonarName());
		holder.txtnumber.setText("" + temp.getContactNo());
		holder.txtstatus.setText("Time : " + temp.getAddress());

		Log.i("Bloodgroup", temp.getBloodGroup());

		if (temp.getBloodGroup().equals("A+"))
			holder.img.setImageResource(R.drawable.ap);
		else if (temp.getBloodGroup().equals("A-"))
			holder.img.setImageResource(R.drawable.an);
		else if (temp.getBloodGroup().equals("B+"))
			holder.img.setImageResource(R.drawable.bp);
		else if (temp.getBloodGroup().equals("B-"))
			holder.img.setImageResource(R.drawable.bn);
		else if (temp.getBloodGroup().equals("AB+"))
			holder.img.setImageResource(R.drawable.abp);
		else if (temp.getBloodGroup().equals("AB-"))
			holder.img.setImageResource(R.drawable.abn);
		else if (temp.getBloodGroup().equals("O+"))
			holder.img.setImageResource(R.drawable.op);
		else if (temp.getBloodGroup().equals("O-"))
			holder.img.setImageResource(R.drawable.on);
		num = convertView;

		holder.btnView.setOnClickListener(new OnDeleteClickListerner(position));
		holder.txtnumber.setOnClickListener(new OnCallClickListerner(position));
		return convertView;
	}

	class Holder {
		ImageView img;
		TextView txtname, txtnumber, txtstatus;
		View llCustomeView;
		ImageButton btnView;
	}

	private class OnDeleteClickListerner implements OnClickListener {
		private int position;

		public OnDeleteClickListerner(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(context).setTitle("Alert").setMessage("Are you sure you want to delete?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					try {
						Database db = new Database(context);
						db.removeFromNotification(list.get(position).getId());
						list.remove(position);
						notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			}).setIcon(android.R.drawable.ic_dialog_alert).show();

		}

	}

	private class OnCallClickListerner implements OnClickListener {
		private int position;

		public OnCallClickListerner(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {

			new AlertDialog.Builder(context).setTitle("Alert").setMessage("Are you sure you want to call " + list.get(position).getContactNo() + "?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
							callIntent.setData(Uri.parse("tel:" + list.get(position).getContactNo()));
							callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(callIntent);
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					}).setIcon(android.R.drawable.ic_dialog_alert).show();
		}

	}

	private class AsyncApp extends AsyncTask<String, Void, Integer> {

		private ProgressDialog dialog;
		private boolean isSuccess = false;
		private int position, flag;

		public AsyncApp(int position, int flag) {
			this.position = position;
			this.flag = flag;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setIndeterminate(true);
			//dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.anim.progress_dialog_anim));
			dialog.setCancelable(false);
			dialog.setMessage("Please wait...");
			dialog.show();

		}

		@Override
		protected Integer doInBackground(String... params) {
			JsonParse js = new JsonParse();
			try {
				int result = js.insertFavourites(getUserId() + "", list.get(position).getId(), list.get(position).getBloodGroup(), flag + "");
				isSuccess = true;
				return result;
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
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			dialog.dismiss();
			if (isSuccess) {
			} else {
				showRetryDialog(position, flag);
			}
		}

	}

	private void showRetryDialog(final int position, final int flag) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Problem getting data");
		builder.setCancelable(true);
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				new AsyncApp(position, flag).execute();
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
		SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);

		return Integer.parseInt(sp.getString("user_id", "-1"));
	}

}
