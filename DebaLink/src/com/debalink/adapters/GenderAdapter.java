package com.debalink.adapters;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.debalink.R;
import com.debalink.utils.Fonts;

public class GenderAdapter extends ArrayAdapter<String> {
	private String[] mSubData;
	private LayoutInflater inflater;
	private Typeface boldFont;
	private Context context;
	public GenderAdapter(Context context,String[] mSubData) {
		super(context, android.R.layout.simple_list_item_1);
		this.context=context;
		this.mSubData=mSubData;
		inflater = LayoutInflater.from(context);
		boldFont=Fonts.getRobotoMedium(context);
	}

	@Override
	public int getCount() {
		return mSubData.length;
	}

	@Override
	public String getItem(int index) {
		return mSubData[index];
	}

	public View getView(int position, View convertView, ViewGroup parent) {

	

		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.gender_list_row, null);
			viewHolder.txtToName = (TextView) convertView.findViewById(R.id.textView1);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.txtToName.setText(getItem(position));
		viewHolder.txtToName.setTypeface(Fonts.getRobotoMedium(context));

		
		return convertView;

	}

	public static class ViewHolder

	{
		TextView txtToName;
	}
}