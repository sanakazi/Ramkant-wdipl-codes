package com.debalink.adapters;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.debalink.MessageDetailFragment;
import com.debalink.R;
import com.debalink.models.MessageModel;
import com.debalink.utils.AsynDownloader;
import com.debalink.utils.Fonts;
import com.debalink.utils.ImageThreadDownloader;
import com.debalink.webservices.HttpRequest;

public class OutboxAdapter extends ArrayAdapter<MessageModel> {
	private ArrayList<MessageModel> messageList;
	private final int VIEW_COUNT = 2, VIEW_SELECT = 0, VIEW_DESELECT = 1;
	private LayoutInflater inflater;
	Context context;
	private FragmentActivity activity;
	private ImageThreadDownloader imageDownloader;
	int imageHeight, imageWidth;
	ImageButton imgButton;

	public OutboxAdapter(FragmentActivity activity, ArrayList<MessageModel> messageList) {

		super(activity, R.layout.message_inbox_row_new, messageList);
		this.messageList = messageList;
		context = activity.getApplicationContext();
		inflater = LayoutInflater.from(context);
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = metrics.widthPixels;

		int plus = (int) (width * (0.15));
		System.out.println("plus " + plus);
		imageWidth = width / 5;

		imageDownloader = new ImageThreadDownloader(activity, activity, imageWidth, imageWidth);

	}

	public int getCount() {
		return messageList.size();
	}

	public long getItemId(int position) {
		return position;

	}

	@Override
	public int getViewTypeCount() {
		return VIEW_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		if (messageList.get(position).isMarked()) {
			return VIEW_SELECT;
		} else {
			return VIEW_DESELECT;
		}
	}

	public static class ViewHolder

	{
		TextView txtToName, txtDate, txtSubject;
		ImageView profilePic;
		Button btnView;
		CheckBox cbk;
		ImageView btnBullet;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		MessageModel planet = (MessageModel) this.getItem(position);

		ViewHolder viewHolder;
		if (convertView == null) {
			int type = getItemViewType(position);

			viewHolder = new ViewHolder();

			switch (type) {
			case VIEW_SELECT:
				convertView = inflater.inflate(R.layout.message_inbox_row_new, null);
				viewHolder.txtToName = (TextView) convertView.findViewById(R.id.txtTo);
				viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
				viewHolder.txtSubject = (TextView) convertView.findViewById(R.id.txtSubject);
				viewHolder.btnView = (Button) convertView.findViewById(R.id.btnView);
				viewHolder.cbk = (CheckBox) convertView.findViewById(R.id.ckbSelect);
				viewHolder.btnBullet = (ImageView) convertView.findViewById(R.id.btnBullet);
				viewHolder.cbk.setVisibility(View.VISIBLE);

				break;

			case VIEW_DESELECT:
				convertView = inflater.inflate(R.layout.message_inbox_row_new, null);
				viewHolder.txtToName = (TextView) convertView.findViewById(R.id.txtTo);
				viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
				viewHolder.txtSubject = (TextView) convertView.findViewById(R.id.txtSubject);
				viewHolder.btnView = (Button) convertView.findViewById(R.id.btnView);
				viewHolder.cbk = (CheckBox) convertView.findViewById(R.id.ckbSelect);
				viewHolder.btnBullet = (ImageView) convertView.findViewById(R.id.btnBullet);
				viewHolder.cbk.setVisibility(View.GONE);

				break;
			}

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.txtToName.setText(planet.getTo());
		viewHolder.txtDate.setText(planet.getDate());
		viewHolder.txtSubject.setText(planet.getSubject());
		//viewHolder.btnView.setOnClickListener(new OnProfileClickListener(position));
		viewHolder.cbk.setOnCheckedChangeListener(new OnCheckListener(position));
		viewHolder.cbk.setChecked(planet.isSelected());
		viewHolder.txtToName.setTypeface(Fonts.getRobotoMedium(activity));
		viewHolder.txtDate.setTypeface(Fonts.getRobotoRegular(activity));

		if (planet.getIsRead().equals("false")) {
			viewHolder.txtSubject.setTypeface(Fonts.getRobotoMedium(activity));
			viewHolder.btnBullet.setVisibility(View.VISIBLE);
			// viewHolder.cbk.setVisibility(View.VISIBLE);
		} else {

			viewHolder.txtSubject.setTypeface(Fonts.getRobotoRegular(activity));
			viewHolder.btnBullet.setVisibility(View.INVISIBLE);
			// viewHolder.cbk.setVisibility(View.INVISIBLE);
		}
		viewHolder.btnView.setOnClickListener(new OnProfileClickListener(position));
		return convertView;

	}

	private class OnProfileClickListener implements OnClickListener {
		private int position;

		OnProfileClickListener(int position) {
			this.position = position;

		}

		@Override
		public void onClick(View v) {
			new Async(activity, "Loading...", messageList.get(position).getMessageId()).execute();
		}
	}

	private String getDateFormated(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:SS.sssz");

		Locale current = activity.getResources().getConfiguration().locale;

		SimpleDateFormat newSdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", current);

		try {
			Date dt = sdf.parse(date);

			String newDate = newSdf.format(dt);

			return newDate;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	private class OnCheckListener implements OnCheckedChangeListener {
		int pos;

		public OnCheckListener(int pos) {
			this.pos = pos;
		}

		@Override
		public void onCheckedChanged(CompoundButton cbk, boolean isChecked) {
			messageList.get(pos).setSelected(isChecked);

		}

	}

	private class Async extends AsynDownloader {

		public Async(Context ctx, String message, String messageId) {
			super(ctx, message);
			this.messageId = messageId;

		}

		private String res, resUpdate;
		private int errorFlag = 0;
		private String messageId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {
			HttpRequest hr = new HttpRequest();
			try {

				ContentValues cv = new ContentValues();
				cv.put("MessageId", messageId);

				res = hr.getDataFromServer(cv, "SelectMeassage");

				cv.put("State", "Outbox");

				resUpdate = hr.getDataFromServer(cv, "UpdateReadMeassage");
				
				

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
			return res;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorFlag == 0) {
				Fragment messageDetail = new MessageDetailFragment();
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				Bundle bundle = new Bundle();
				bundle.putString("details", result);
				messageDetail.setArguments(bundle);
				ft.add(R.id.fragment, messageDetail, "messageDetail");
				ft.commit();
			}

		}

	}

	public ArrayList<MessageModel> getList() {
		return messageList;
	}

}
