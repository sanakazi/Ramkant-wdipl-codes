package com.debalink;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MessageDetailFragment extends Fragment{
	private TextView txtDetails;
	private ImageButton btnBack;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.message_details,null);
		txtDetails=(TextView)view.findViewById(R.id.txtMessageDetails);
		btnBack=(ImageButton)view.findViewById(R.id.btnBack);

		String deatils=getArguments().getString("details", "");
		txtDetails.setText(deatils);
		
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getActivity().getSupportFragmentManager().beginTransaction().remove(MessageDetailFragment.this).commit();
			}
		});
		
		return view;
	}
}
