package com.wdipl.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.wdipl.bloodbank.R;

public class JsonParse {
	static String NAMESPACE = "http://tempuri.org/";
	static String URL = "http://dev.kasshin.tw/webservices/BloodDonorWebService.asmx";
	InputStream is = null;
	JSONObject jObj = null;
	String json = null;

	public ArrayList<DonorDetail> getDonorDetail(String bloodgroup, String state, String userid) throws IOException, SocketTimeoutException, SocketException,
			XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/GetBloodDonorList";
		String METHOD = "GetBloodDonorList";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		PropertyInfo property = new PropertyInfo();
		property.setName("bloodgroup");
		property.setValue(bloodgroup);
		property.setType(String.class);
		request.addProperty(property);

		/*
		 * PropertyInfo property2 = new PropertyInfo();
		 * property2.setName("city"); property2.setValue(city);
		 * property2.setType(String.class); request.addProperty(property2);
		 * 
		 * PropertyInfo property3 = new PropertyInfo();
		 * property3.setName("district"); property3.setValue(district);
		 * property3.setType(String.class); request.addProperty(property3);
		 */

		PropertyInfo property4 = new PropertyInfo();
		property4.setName("state");
		property4.setValue(state);
		property4.setType(String.class);
		request.addProperty(property4);

		PropertyInfo property5 = new PropertyInfo();
		property5.setName("userid");
		property5.setValue(userid);
		property5.setType(String.class);
		request.addProperty(property5);

		return requestForQuery(request, SOAP_ACTION);

	}

	private ArrayList<DonorDetail> requestForQuery(SoapObject request, String sOAP_ACTION) throws SocketException, SocketTimeoutException, IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE httpTrasportSE = new HttpTransportSE(URL);

		httpTrasportSE.call(sOAP_ACTION, envelope);
		Object soapObject = envelope.getResponse();
		return getJsonFromUrl(soapObject);

	}

	public ArrayList<DonorDetail> getJsonFromUrl(Object soapObject) {
		ArrayList<DonorDetail> list = new ArrayList<DonorDetail>();
		try {
			JSONObject obj = new JSONObject(soapObject.toString());
			JSONArray array = obj.getJSONArray("Table");

			for (int i = 0; i < array.length(); i++) {
				DonorDetail dd = new DonorDetail();
				JSONObject jtemp = array.getJSONObject(i);
				dd.setDonarName(jtemp.getString("DonorName"));
				dd.setContactNo(jtemp.getString("ContactNo"));
				dd.setAddress(jtemp.getString("Address"));
				dd.setBloodGroup(jtemp.getString("BloodGroup"));
				dd.setId(jtemp.getString("BldId"));

				if (jtemp.has("isFav")) {
					dd.setAddedToFavourite(jtemp.getString("isFav").equalsIgnoreCase("1")?true:false);
				}

				list.add(dd);
			}

		} catch (JSONException e) {

			e.printStackTrace();
			return null;
		}

		return list;
	}

	public int insertFavourites(String userid, String bldId, String bloodGroup, String flag) throws IOException, SocketTimeoutException, SocketException, XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/InsertUpdateFavoriteList";
		String METHOD = "InsertUpdateFavoriteList";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		PropertyInfo property = new PropertyInfo();
		property.setName("userid");
		property.setValue(userid);
		property.setType(String.class);
		request.addProperty(property);

		PropertyInfo property1 = new PropertyInfo();
		property1.setName("BldId");
		property1.setValue(bldId);
		property1.setType(String.class);
		request.addProperty(property1);

		PropertyInfo property2 = new PropertyInfo();
		property2.setName("BloodGroup");
		property2.setValue(bloodGroup);
		property2.setType(String.class);
		request.addProperty(property2);

		PropertyInfo property3 = new PropertyInfo();
		property3.setName("flag");
		property3.setValue(flag);
		property3.setType(String.class);
		request.addProperty(property3);

		return requestForInsertQuery(request, SOAP_ACTION);

	}

	private int requestForInsertQuery(SoapObject request, String sOAP_ACTION) throws SocketException, SocketTimeoutException, IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE httpTrasportSE = new HttpTransportSE(URL);

		httpTrasportSE.call(sOAP_ACTION, envelope);

		envelope.getResponse();
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		// Log.i("Value of res :", soapObject);

		// SoapObject diffgramObject = (SoapObject)
		// response.getProperty("diffgram");

		// SoapObject newDataSetObject;

		// if (diffgramObject.hasProperty("NewDataSet")) {
		// newDataSetObject = (SoapObject)
		// diffgramObject.getProperty("NewDataSet");
		// } else {
		// return 0;
		// }
		// SoapObject data = (SoapObject) newDataSetObject.getProperty(0);
		int res = Integer.parseInt(response.toString());

		return res;

	}

	public ArrayList<DonorDetail> getFavouriteList(String userId) throws IOException, SocketTimeoutException, SocketException, XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/Getfavoritelist";
		String METHOD = "Getfavoritelist";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		PropertyInfo property = new PropertyInfo();
		property.setName("userid");
		property.setValue(userId);
		property.setType(String.class);
		request.addProperty(property);
		return requestForQuery(request, SOAP_ACTION);
	}

	public ArrayList<DonorDetail> getDonorListByPincode(String bloodgroup, String pincode,String userid) throws IOException, SocketTimeoutException, SocketException, XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/GetBloodDonorListByPincode";
		String METHOD = "GetBloodDonorListByPincode";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		PropertyInfo property = new PropertyInfo();
		property.setName("bloodgroup");
		property.setValue(bloodgroup);
		property.setType(String.class);
		request.addProperty(property);

		PropertyInfo property2 = new PropertyInfo();
		property2.setName("pincode");
		property2.setValue(pincode);
		property2.setType(String.class);
		request.addProperty(property2);
		
		PropertyInfo property5 = new PropertyInfo();
		property5.setName("userid");
		property5.setValue(userid);
		property5.setType(String.class);
		request.addProperty(property5);

		return requestForQuery(request, SOAP_ACTION);

	}

	public ArrayList<DonorDetail> getDonorListByCity(String bloodgroup, String city,String userid) throws IOException, SocketTimeoutException, SocketException, XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/GetBloodDonorListByCity";
		String METHOD = "GetBloodDonorListByCity";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		PropertyInfo property = new PropertyInfo();
		property.setName("bloodgroup");
		property.setValue(bloodgroup);
		property.setType(String.class);
		request.addProperty(property);

		PropertyInfo property2 = new PropertyInfo();
		property2.setName("city");
		property2.setValue(city);
		property2.setType(String.class);
		request.addProperty(property2);
		
		PropertyInfo property5 = new PropertyInfo();
		property5.setName("Userid");
		property5.setValue(userid);
		property5.setType(String.class);
		request.addProperty(property5);

		return requestForQuery(request, SOAP_ACTION);

	}

	public Object sendNotification(String ids, String message, String city, String bloodgroup, String contactno) throws IOException, SocketTimeoutException, SocketException,
			XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/SendNotification";
		String METHOD = "SendNotification";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		PropertyInfo property = new PropertyInfo();
		property.setName("bloodId");
		property.setValue(ids);
		property.setType(String.class);
		request.addProperty(property);

		PropertyInfo property2 = new PropertyInfo();
		property2.setName("msg");
		property2.setValue(message);
		property2.setType(String.class);
		request.addProperty(property2);

		PropertyInfo property3 = new PropertyInfo();
		property3.setName("city");
		property3.setValue(city);
		property3.setType(String.class);
		request.addProperty(property3);

		PropertyInfo property4 = new PropertyInfo();
		property4.setName("bloodgrp");
		property4.setValue(bloodgroup);
		property4.setType(String.class);
		request.addProperty(property4);

		PropertyInfo property5 = new PropertyInfo();
		property5.setName("contactno");
		property5.setValue(contactno);
		property5.setType(String.class);
		request.addProperty(property5);

		return requestForSendNotification(request, SOAP_ACTION);

	}

	private Object requestForSendNotification(SoapObject request, String sOAP_ACTION) throws SocketException, SocketTimeoutException, IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE httpTrasportSE = new HttpTransportSE(URL);

		httpTrasportSE.call(sOAP_ACTION, envelope);
		Object soapObject = envelope.getResponse();
		return soapObject;

	}

	public void showHelp(Activity act) {
		final Dialog dialog = new Dialog(act);
		// dialog.setTitle("Help");
		LayoutInflater inflater = act.getLayoutInflater();

		View layout = inflater.inflate(R.layout.help_popup, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		final ImageButton btnOk = (ImageButton) layout.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.setContentView(layout);
		dialog.show();
	}

}
