
package com.wdipl.adapter;

import java.util.ArrayList;
import com.wdipl.bloodbank.R;
import com.wdipl.json.DistrictModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DistrictAdapter extends ArrayAdapter<DistrictModel> {
	
	private LayoutInflater inflater;
	//private Context context;
	private ArrayList<DistrictModel> districtList;
	public DistrictAdapter(Context context,ArrayList<DistrictModel> districtList) {
		super(context,R.layout.gender_list_row);
		//this.context = context;
		this.districtList=districtList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return districtList.size();
	}

	@Override
	public DistrictModel getItem(int index) {
		return districtList.get(index);
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

		viewHolder.txtToName.setText(getItem(position).getDistrictName());
		
		return convertView;

	}

	public static class ViewHolder

	{
		TextView txtToName;
	}
}