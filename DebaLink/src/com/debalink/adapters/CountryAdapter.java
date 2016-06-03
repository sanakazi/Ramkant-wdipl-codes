package com.debalink.adapters;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.debalink.R;
import com.debalink.models.CategoryModel;
import com.debalink.models.CountryModel;
import com.debalink.utils.Fonts;

public class CountryAdapter extends ArrayAdapter<CountryModel> {
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<CountryModel> countryList;

	public CountryAdapter(Context context, ArrayList<CountryModel> countryList) {
		super(context, android.R.layout.simple_list_item_1);
		this.context = context;
		this.countryList = countryList;
		inflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		return countryList.size();
	}

	@Override
	public CountryModel getItem(int index) {
		return countryList.get(index);
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

		viewHolder.txtToName.setText(getItem(position).getCountryName());
		viewHolder.txtToName.setTypeface(Fonts.getRobotoMedium(context));

		return convertView;

	}

	public static class ViewHolder

	{
		TextView txtToName;
	}
}