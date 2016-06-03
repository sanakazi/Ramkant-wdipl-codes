package com.debalink.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class AsynDownloader extends AsyncTask<Void, Void, String> {
	private Context ctx;
	private ProgressDialog pd;
	private String message;

	public AsynDownloader(Context ctx, String message) {
		this.ctx = ctx;
		this.message = message;
		pd = new ProgressDialog(ctx);
		pd.setMessage(message);
		pd.setTitle("Please Wait");
		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd.show();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (pd != null && pd.isShowing())
			pd.dismiss();
	}
}
