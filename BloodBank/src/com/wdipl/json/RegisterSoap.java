package com.wdipl.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class RegisterSoap {

	String NAMESPACE = "http://tempuri.org/";
	String URL = "http://dev.kasshin.tw/webservices/BloodDonorWebService.asmx";
	InputStream is = null;
	JSONObject jObj = null;
	String json = null;

	public Integer donorRegister(String name, String bloodgroup, String gender, String dob, String mobile, String street, String city, String district, String state,
			String country, String password,String pincode,String email) throws IOException, SocketTimeoutException, SocketException, XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/BloodDonorRegister";
		String METHOD = "BloodDonorRegister";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		PropertyInfo property = new PropertyInfo();
		property.setName("DonorName");
		property.setValue(name);
		property.setType(String.class);
		request.addProperty(property);
		PropertyInfo property1 = new PropertyInfo();
		property1.setName("bloodGroup");
		property1.setValue(bloodgroup);
		property1.setType(String.class);
		request.addProperty(property1);
		PropertyInfo property2 = new PropertyInfo();
		property2.setName("gender");
		property2.setValue(gender);
		property2.setType(String.class);
		request.addProperty(property2);
		PropertyInfo property3 = new PropertyInfo();
		property3.setName("dateOfBirth");
		property3.setValue(dob);
		property3.setType(String.class);
		request.addProperty(property3);
		PropertyInfo property4 = new PropertyInfo();
		property4.setName("contactNo");
		property4.setValue(mobile);
		property4.setType(String.class);
		request.addProperty(property4);
		PropertyInfo property5 = new PropertyInfo();
		property5.setName("street");
		property5.setValue(street);
		property5.setType(String.class);
		request.addProperty(property5);
		PropertyInfo property6 = new PropertyInfo();
		property6.setName("city");
		property6.setValue(city);
		property6.setType(String.class);
		request.addProperty(property6);

		PropertyInfo property9 = new PropertyInfo();
		property9.setName("district");
		property9.setValue(district);
		property9.setType(String.class);
		request.addProperty(property9);

		PropertyInfo property7 = new PropertyInfo();
		property7.setName("state");
		property7.setValue(state);
		property7.setType(String.class);
		request.addProperty(property7);

		PropertyInfo property8 = new PropertyInfo();
		property8.setName("country");
		property8.setValue(country);
		property8.setType(String.class);
		request.addProperty(property8);

		PropertyInfo property10 = new PropertyInfo();
		property10.setName("password");
		property10.setValue(password);
		property10.setType(String.class);
		request.addProperty(property10);
		
		PropertyInfo property11 = new PropertyInfo();
		property11.setName("pincode");
		property11.setValue(pincode);
		property11.setType(String.class);
		request.addProperty(property11);

		PropertyInfo property12 = new PropertyInfo();
		property12.setName("email");
		property12.setValue(email);
		property12.setType(String.class);
		request.addProperty(property12);
		
		return requestForQuery(request, SOAP_ACTION);

	}

	private int requestForQuery(SoapObject request, String sOAP_ACTION) throws SocketException, SocketTimeoutException, IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE httpTrasportSE = new HttpTransportSE(URL);

		httpTrasportSE.call(sOAP_ACTION, envelope);
		// SoapObject response = (SoapObject) envelope.getResponse();
		// Log.i("Value of res :", soapObject);

		// SoapObject diffgramObject = (SoapObject)
		// response.getProperty("diffgram");

		// SoapObject newDataSetObject;
		;

		// if (diffgramObject.hasProperty("NewDataSet")) {
		// newDataSetObject = (SoapObject)
		// diffgramObject.getProperty("NewDataSet");
		// } else {
		// return 0;
		// }
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

		// SoapObject data = (SoapObject) newDataSetObject.getProperty(0);
		// int res = Integer.parseInt(data.getPropertySafelyAsString("Result",
		// ""));
		int res = Integer.parseInt(response.toString());

		return res;

	}

	private SoapObject requestForLoginQuery(SoapObject request, String sOAP_ACTION) throws SocketException, SocketTimeoutException, IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE httpTrasportSE = new HttpTransportSE(URL);

		httpTrasportSE.call(sOAP_ACTION, envelope);
		SoapObject response = (SoapObject) envelope.getResponse();
		// Log.i("Value of res :", soapObject);

		SoapObject diffgramObject = (SoapObject) response.getProperty("diffgram");

		SoapObject newDataSetObject;
		;

		if (diffgramObject.hasProperty("NewDataSet")) {
			newDataSetObject = (SoapObject) diffgramObject.getProperty("NewDataSet");
		} else {
			return null;
		}
		// SoapPrimitive response =(SoapPrimitive) envelope.getResponse();

		SoapObject data = (SoapObject) newDataSetObject.getProperty(0);
		//int res = Integer.parseInt(data.getPropertySafelyAsString("Result", ""));
		// int res = Integer.parseInt(response.toString());

		return data;

	}

	public SoapObject donorLogin(String contactNo, String password) throws IOException, SocketTimeoutException, SocketException, XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/Login";
		String METHOD = "Login";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		PropertyInfo property = new PropertyInfo();
		property.setName("ContactNo");
		property.setValue(contactNo);
		property.setType(String.class);
		request.addProperty(property);

		PropertyInfo property1 = new PropertyInfo();
		property1.setName("Password");
		property1.setValue(password);
		property1.setType(String.class);
		request.addProperty(property1);

		return requestForLoginQuery(request, SOAP_ACTION);

	}
	
	public Integer updateDeviceId(String bldId, String deviceid) throws IOException, SocketTimeoutException, SocketException, XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/InsertUpdateNotification";
		String METHOD = "InsertUpdateNotification";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		PropertyInfo property = new PropertyInfo();
		property.setName("BldId");
		property.setValue(bldId);
		property.setType(String.class);
		request.addProperty(property);

		PropertyInfo property1 = new PropertyInfo();
		property1.setName("keyname");
		property1.setValue(deviceid);
		property1.setType(String.class);
		request.addProperty(property1);

		return requestForUpdateNotification(request, SOAP_ACTION);

	}
	
	private int requestForUpdateNotification(SoapObject request, String sOAP_ACTION) throws SocketException, SocketTimeoutException, IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE httpTrasportSE = new HttpTransportSE(URL);
		httpTrasportSE.call(sOAP_ACTION, envelope);
		Object response = envelope.getResponse();
		Log.i("Value of res :", response.toString());

		int res = Integer.parseInt(response.toString());
		return res;

	}

	public String getProfile(String bldId) throws IOException, SocketTimeoutException, SocketException, XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/SelectProfile";
		String METHOD = "SelectProfile";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		PropertyInfo property = new PropertyInfo();
		property.setName("BldId");
		property.setValue(bldId);
		property.setType(String.class);
		request.addProperty(property);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE httpTrasportSE = new HttpTransportSE(URL);
		httpTrasportSE.call(SOAP_ACTION, envelope);
		Object response = envelope.getResponse();
		
		return response.toString();

	}
	
	public Integer updateProfile(String blidId,String name, String bloodgroup, String gender, String dob, String mobile, String street, String city, String district, String state,
			String country, String password,String pincode,String email) throws IOException, SocketTimeoutException, SocketException, XmlPullParserException {
		String SOAP_ACTION = "http://tempuri.org/UpdateProfile";
		String METHOD = "UpdateProfile";
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		
		PropertyInfo bldId = new PropertyInfo();
		bldId.setName("DonorName");
		bldId.setValue(name);
		bldId.setType(String.class);
		request.addProperty(bldId);
		
		
		PropertyInfo property = new PropertyInfo();
		property.setName("BldId");
		property.setValue(blidId);
		property.setType(String.class);
		request.addProperty(property);
		
		
		PropertyInfo property1 = new PropertyInfo();
		property1.setName("bloodGroup");
		property1.setValue(bloodgroup);
		property1.setType(String.class);
		request.addProperty(property1);
		PropertyInfo property2 = new PropertyInfo();
		property2.setName("gender");
		property2.setValue(gender);
		property2.setType(String.class);
		request.addProperty(property2);
		PropertyInfo property3 = new PropertyInfo();
		property3.setName("dateOfBirth");
		property3.setValue(dob);
		property3.setType(String.class);
		request.addProperty(property3);
		PropertyInfo property4 = new PropertyInfo();
		property4.setName("contactNo");
		property4.setValue(mobile);
		property4.setType(String.class);
		request.addProperty(property4);
		PropertyInfo property5 = new PropertyInfo();
		property5.setName("street");
		property5.setValue(street);
		property5.setType(String.class);
		request.addProperty(property5);
		PropertyInfo property6 = new PropertyInfo();
		property6.setName("city");
		property6.setValue(city);
		property6.setType(String.class);
		request.addProperty(property6);

		PropertyInfo property9 = new PropertyInfo();
		property9.setName("district");
		property9.setValue(district);
		property9.setType(String.class);
		request.addProperty(property9);

		PropertyInfo property7 = new PropertyInfo();
		property7.setName("state");
		property7.setValue(state);
		property7.setType(String.class);
		request.addProperty(property7);

		PropertyInfo property8 = new PropertyInfo();
		property8.setName("country");
		property8.setValue(country);
		property8.setType(String.class);
		request.addProperty(property8);

		PropertyInfo property10 = new PropertyInfo();
		property10.setName("password");
		property10.setValue(password);
		property10.setType(String.class);
		request.addProperty(property10);
		
		PropertyInfo property11 = new PropertyInfo();
		property11.setName("Pincode");
		property11.setValue(pincode);
		property11.setType(String.class);
		request.addProperty(property11);

		PropertyInfo property12 = new PropertyInfo();
		property12.setName("Email");
		property12.setValue(email);
		property12.setType(String.class);
		request.addProperty(property12);
		
		return requestForQuery(request, SOAP_ACTION);

	}
	

}
