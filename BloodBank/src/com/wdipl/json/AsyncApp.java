package com.wdipl.json;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;

public class AsyncApp extends AsyncTask<String,Void,ArrayList<DonorDetail>>
{
	ArrayList<DonorDetail> list;
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	
	@Override
	protected ArrayList<DonorDetail> doInBackground(String... params) {
		JsonParse js=new JsonParse();
		try {
			 list=js.getDonorDetail(params[0],params[1],params[2]);
			return list;
		} catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (XmlPullParserException e1) {
			
			e1.printStackTrace();
		}
		return null;
	}
	@Override
	protected void onPostExecute(ArrayList<DonorDetail> result) {
		
		super.onPostExecute(result);
	}

	
	
}