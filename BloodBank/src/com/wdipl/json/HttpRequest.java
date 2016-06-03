package com.wdipl.json;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import static com.wdipl.json.JsonParse.NAMESPACE;
import static com.wdipl.json.JsonParse.URL;

public class HttpRequest {

	public String getDataFromServer(ContentValues cv, String methodName) throws SocketException, SocketTimeoutException, XmlPullParserException, IOException {
		String response = null;

		SoapObject request = new SoapObject(NAMESPACE, methodName);
		request = addRequestParameter(request, cv);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 2 * 60 * 1000);

		androidHttpTransport.call(NAMESPACE + methodName, envelope);
		Log.i("Data", envelope.getResponse().toString());
		response = envelope.getResponse().toString();

		return response;
	}

	public SoapObject getSoapObjectFromServer(ContentValues cv, String methodName) throws SocketException, SocketTimeoutException, XmlPullParserException, IOException {
		SoapObject response = null;

		SoapObject request = new SoapObject(NAMESPACE, methodName);
		if (cv != null)
			request = addRequestParameter(request, cv);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 5 * 60 * 1000);

		androidHttpTransport.call(NAMESPACE + methodName, envelope);
		// Log.i("Data", envelope.getResponse().toString());

		response = (SoapObject) envelope.getResponse();

		return response;
	}

	public SoapObject parseLoginResponse(SoapObject response) {

		SoapObject newDataSetObject = (SoapObject) response.getProperty("NewDataSet");

		SoapObject tableObject = (SoapObject) newDataSetObject.getProperty(0);

		return tableObject;
	}

	public ArrayList<StateModel> parseStateList(SoapObject response) throws JSONException {
		ArrayList<StateModel> stateList = new ArrayList<StateModel>();

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<StateModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {

			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			StateModel state = new StateModel();
			state.setStateId(data.getPropertySafelyAsString("StateId", ""));
			state.setStateName(data.getPropertySafelyAsString("StateName", ""));

			stateList.add(state);
		}

		return stateList;
	}

	public ArrayList<DistrictModel> parseDistrictList(SoapObject response) {
		ArrayList<DistrictModel> districtList = new ArrayList<DistrictModel>();
		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");
		SoapObject newDataSetObject;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<DistrictModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {

			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			DistrictModel district = new DistrictModel();
			district.setDistrictId(data.getPropertySafelyAsString("DistrictId", ""));
			district.setDistrictName(data.getPropertySafelyAsString("DistrictName", ""));

			districtList.add(district);
		}

		return districtList;
	}
	
	public ArrayList<BloodBankModel> parseBloodBankList(SoapObject response) {
		ArrayList<BloodBankModel> bloodbankList = new ArrayList<BloodBankModel>();
		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");
		SoapObject newDataSetObject;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return new ArrayList<BloodBankModel>();
		}

		for (int i = 0; i < newDataSetObject.getPropertyCount(); i++) {

			SoapObject data = (SoapObject) newDataSetObject.getProperty(i);
			BloodBankModel bloodbank = new BloodBankModel();
			bloodbank.setDistrictId(data.getPropertySafelyAsString("DistrictId", ""));
			bloodbank.setBloodbankName(data.getPropertySafelyAsString("BloodBankName", ""));
			bloodbank.setBloodbankId(data.getPropertySafelyAsString("BloodBankId", ""));
			bloodbank.setContactPerson(data.getPropertySafelyAsString("ContactPerson", ""));
			bloodbank.setContactNo(data.getPropertySafelyAsString("Telephone", ""));
			bloodbank.setMobileNo(data.getPropertySafelyAsString("MobileNo", ""));

			bloodbankList.add(bloodbank);
		}

		return bloodbankList;
	}

	@SuppressLint("NewApi")
	private SoapObject addRequestParameter(SoapObject request, ContentValues cv) {
		if (cv != null) {
			for (int i = 0; i < cv.size(); i++) {
				Iterator<String> keys = cv.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					request.addProperty(key, cv.get(key));
				}
			}
		}

		return request;
	}

	public static boolean hasConnection(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}
}
