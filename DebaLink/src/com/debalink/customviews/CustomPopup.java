package com.debalink.customviews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.debalink.interfaces.RetryAlertCallback;

public class CustomPopup {

	public static void showPicDialog(Activity activity,int errorCode, final RetryAlertCallback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setTitle("Error").setMessage(getMessage(errorCode));

		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				callback.retry();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.show();
	}
	private static String getMessage(int errorCode){
		if(errorCode==1){
			return "No internet connection";
		}else if(errorCode==2){
			return "No internet connection";
		}else if(errorCode==3){
			return "Server error";
		}else if(errorCode==4){
			return "Server error";
		}else if(errorCode==5){
			return "Something went wrong";
		}else{
			return "Unknown Error";
		}
	}

}