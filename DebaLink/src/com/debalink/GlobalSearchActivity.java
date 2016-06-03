package com.debalink;

import java.util.ArrayList;
import com.debalink.adapters.GlobalSearchAdapter;
import com.debalink.adapters.GlobalSearchDetailAdapter;
import com.debalink.customviews.CustomAutoCompleteTextView;
import com.debalink.customviews.DrawableClickListener;
import com.debalink.models.SearchModel;
import com.debalink.utils.Fonts;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class GlobalSearchActivity extends FragmentActivity {

	private CustomAutoCompleteTextView edtSearch;
	private ListView lvSearchList;
	private GlobalSearchAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.global_search);
		edtSearch = (CustomAutoCompleteTextView) findViewById(R.id.edtSearch);
		edtSearch.setTypeface(Fonts.getFuturaMedium(this));
		lvSearchList = (ListView) findViewById(R.id.listView1);

		adapter = new GlobalSearchAdapter(this);

		edtSearch.setAdapter(adapter);

		edtSearch.setThreshold(2);

		edtSearch.setDrawableClickListener(new DrawableClickListener() {

			public void onClick(DrawablePosition target) {
				switch (target) {
				case RIGHT:
					if (edtSearch.getText().toString().length() > 0) {

					}
					break;
				default:
				}
			}

		});

		/*
		 * edtSearch.addTextChangedListener(new TextWatcher() { public void
		 * onTextChanged(CharSequence s, int start, int before, int count) {
		 * if(s.length()>1){ adapter.getFilter().filter(s); } else{
		 * adapter.clear(); } }
		 * 
		 * public void beforeTextChanged(CharSequence s, int start, int count,
		 * int after) { }
		 * 
		 * public void afterTextChanged(Editable s) { } });
		 */

		edtSearch.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				edtSearch.setText(adapter.getItem(position).getName());
				ArrayList<SearchModel> list = new ArrayList<SearchModel>();
				list.add(adapter.getItem(position));
				lvSearchList.setAdapter(new GlobalSearchDetailAdapter(GlobalSearchActivity.this, list));
			}
		});

	}
}
