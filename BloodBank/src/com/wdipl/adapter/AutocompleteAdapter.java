package com.wdipl.adapter;

import java.util.ArrayList;

import com.wdipl.bloodbank.R;
import com.wdipl.json.StateModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AutocompleteAdapter extends ArrayAdapter<StateModel> {
	
	private LayoutInflater inflater;
	//private Context context;
	private ArrayList<StateModel> stateList;
	public AutocompleteAdapter(Context context,ArrayList<StateModel> stateList) {
		super(context,R.layout.gender_list_row);
		//this.context = context;
		this.stateList=stateList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return stateList.size();
	}

	@Override
	public StateModel getItem(int index) {
		return stateList.get(index);
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

		viewHolder.txtToName.setText(getItem(position).getStateName());
		
		return convertView;

	}

	public static class ViewHolder

	{
		TextView txtToName;
	}
}