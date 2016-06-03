package com.debalink.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import com.debalink.R;
import com.debalink.models.PollOptionModel;
import com.debalink.utils.Fonts;

public class PollOptionsAdapter extends ArrayAdapter<PollOptionModel> {
	ArrayList<PollOptionModel> pollList;
	private LayoutInflater inflater;

	private Context context;

	public PollOptionsAdapter(Context context,ArrayList<PollOptionModel> pollList) {
		super(context, android.R.layout.simple_list_item_1);
		this.context = context;
		this.pollList = pollList;
		inflater = LayoutInflater.from(context);
		
		for(PollOptionModel model:pollList){
			model.isSelected();
		}
		
	}

	@Override
	public int getCount() {
		return pollList.size();
	}

	@Override
	public PollOptionModel getItem(int index) {
		return pollList.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.poll_list_row, null);
			viewHolder.txtOption = (RadioButton) convertView.findViewById(R.id.rbOptions);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.txtOption.setText(getItem(position).getVoteName());
		viewHolder.txtOption.setTypeface(Fonts.getRobotoMedium(context));
		
		Log.i("isSelected", position+" "+getItem(position).isSelected()+"");
		
		viewHolder.txtOption.setChecked(getItem(position).isSelected());

		return convertView;

	}

	public static class ViewHolder

	{
		RadioButton txtOption;
	}
}