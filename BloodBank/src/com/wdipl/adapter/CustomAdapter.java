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
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wdipl.bloodbank.MessageActivity;
import com.wdipl.bloodbank.R;
import com.wdipl.json.DonorDetail;
import com.wdipl.json.JsonParse;

public class CustomAdapter extends ArrayAdapter<DonorDetail> {
	Activity context;
	int layout;
	LayoutInflater inflater;
	ArrayList<DonorDetail> list;
	View num;

	// private GestureDetector gestureDetector;

	public CustomAdapter(Activity context, int resource, ArrayList<DonorDetail> objects) {
		super(context, resource, objects);
		this.context = context;
		this.layout = resource;
		list = objects;
		// gestureDetector = new GestureDetector(context, new
		// SwipeGestureDetector());

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
			holder.btnAddToFavourites = (ImageButton) convertView.findViewById(R.id.btnFavourites);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		DonorDetail temp = new DonorDetail();
		temp = list.get(position);
		holder.txtname.setText(temp.getDonarName());
		holder.txtnumber.setText(temp.getContactNo());
		holder.txtstatus.setText(temp.getAddress());
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

		// holder.llCustomeView.setOnTouchListener(this);
		// holder.txtname.setOnTouchListener(this);
		// holder.img.setOnTouchListener(this);
		// holder.txtnumber.setOnTouchListener(this);
		// holder.txtstatus.setOnTouchListener(this);
		// convertView.setOnTouchListener(this);
		// holder.llCustomeView.setOnClickListener(this);
		// holder.txtname.setOnClickListener(this);
		// holder.img.setOnClickListener(this);
		// holder.txtnumber.setOnClickListener(this);

		holder.btnAddToFavourites.setSelected(temp.isAddedToFavourite());
		holder.btnAddToFavourites.setOnClickListener(new OnFavouritesClickListerner(position));
		holder.btnView.setOnClickListener(new OnViewClickListerner(position));
		holder.txtstatus.setVisibility(View.GONE);
		
		
		return convertView;
	}

	class Holder {
		ImageView img;
		TextView txtname, txtnumber, txtstatus;
		View llCustomeView;
		ImageButton btnView, btnAddToFavourites;
	}

	/*
	 * private class SwipeGestureDetector extends SimpleOnGestureListener { //
	 * Swipe properties, you can change it to make the swipe // longer or
	 * shorter and speed
	 * 
	 * private static final int SWIPE_MIN_DISTANCE = 80; private static final
	 * int SWIPE_MAX_OFF_PATH = 120; private static final int
	 * SWIPE_THRESHOLD_VELOCITY = 100;
	 * 
	 * @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float
	 * velocityX, float velocityY) {
	 * 
	 * Log.i("On FLIng Start", "Enterin fling   " + Math.abs(velocityX));
	 * 
	 * try { float diffAbs = Math.abs(e1.getY() - e2.getY()); float diff =
	 * e1.getX() - e2.getX();
	 * 
	 * if (diffAbs > SWIPE_MAX_OFF_PATH) return false;
	 * 
	 * // Left swipe if (diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) >
	 * SWIPE_THRESHOLD_VELOCITY) { // CustomAdapter.this.onLeftSwipe();
	 * 
	 * // Right swipe } else if (-diff > SWIPE_MIN_DISTANCE &&
	 * Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	 * 
	 * CustomAdapter.this.onRightSwipe(); } } catch (Exception e) {
	 * Log.e("YourActivity", "Error on gestures"); } return false; } }
	 */

	private class OnFavouritesClickListerner implements OnClickListener {
		private int position;

		public OnFavouritesClickListerner(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (v.isSelected()) {
				list.get(position).setAddedToFavourite(false);
				v.setSelected(false);
				new AsyncApp(position, 0).execute();

			} else {
				list.get(position).setAddedToFavourite(true);
				v.setSelected(true);
				new AsyncApp(position, 1).execute();
			}

		}

	}

	private class OnViewClickListerner implements OnClickListener {
		private int position;

		public OnViewClickListerner(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(context, MessageActivity.class);
			i.putExtra("Mobile", list.get(position).getContactNo());
			i.putExtra("Name", list.get(position).getDonarName());
			i.putExtra("Address", list.get(position).getAddress());
			i.putExtra("id", list.get(position).getId());
			i.putExtra("blood_group", list.get(position).getBloodGroup());
			context.startActivity(i);

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
