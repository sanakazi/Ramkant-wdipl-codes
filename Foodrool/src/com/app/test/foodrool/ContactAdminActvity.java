package com.app.test.foodrool;

import com.app.test.foodrool.menu.Header;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ContactAdminActvity extends ActionBarActivity{
	private TextView txtAdminName, txtAdminName2, txtAdminNo, txtAdminNo2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_activity);
		new Header().showMenu(ContactAdminActvity.this, View.VISIBLE, "Contact Admin");
		txtAdminName = (TextView) findViewById(R.id.txtContactName);
		txtAdminName2 = (TextView) findViewById(R.id.txtContactName2);
		txtAdminNo = (TextView) findViewById(R.id.txtContactNo);
		txtAdminNo2 = (TextView) findViewById(R.id.txtContactNo2);
		
		txtAdminName.setText("Nipun Goel");
		txtAdminNo.setText("+919717358186");
		txtAdminName2.setText("Tanmay Garg");
		txtAdminNo2.setText("+919717398602");
		
		txtAdminNo2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_CALL);

				intent.setData(Uri.parse("tel:+919717398602"));
				startActivity(intent);
			}
		});
		
		txtAdminNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_CALL);

				intent.setData(Uri.parse("tel:+919717358186"));
				startActivity(intent);
			}
		});
		
	}
}
