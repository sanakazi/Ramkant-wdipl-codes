package com.debalink.utils;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPreferences {
	public static void saveUserPreferences(Context ctx, SoapObject res) {
		SharedPreferences sp = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);

		// res.getPropertySafelyAsString("Name","");
		// res.getPropertySafelyAsString("DOB","");
		// res.getPropertySafelyAsString("Gender","");
		// res.getPropertySafelyAsString("UserName","");
		// res.getPropertySafelyAsString("Password","");
		// res.getPropertySafelyAsString("EmailId","");
		// res.getPropertySafelyAsString("IsActive","");
		// res.getPropertySafelyAsString("RegisterFrom","");
		// res.getPropertySafelyAsString("CreatedDate","");
		// res.getPropertySafelyAsString("IsAccess","");
		// res.getPropertySafelyAsString("LoginDateTime","");
		// res.getPropertySafelyAsString("IPAddress","");
		// res.getPropertySafelyAsString("ProfilePic","");

		Editor editor = sp.edit();
		editor.putString("userId", res.getPropertySafelyAsString("UserId", ""));
		editor.putString("dob", res.getPropertySafelyAsString("DOB", ""));
		editor.putString("gender", res.getPropertySafelyAsString("Gender", ""));
		editor.putString("username", res.getPropertySafelyAsString("UserName", ""));
		editor.putString("password", res.getPropertySafelyAsString("Password", ""));
		editor.putString("emailId", res.getPropertySafelyAsString("EmailId", ""));
		editor.putString("isActive", res.getPropertySafelyAsString("IsActive", ""));
		editor.putString("registerFrom", res.getPropertySafelyAsString("RegisterFrom", ""));
		editor.putString("createDate", res.getPropertySafelyAsString("CreatedDate", ""));
		editor.putString("isAccess", res.getPropertySafelyAsString("IsAccess", ""));
		editor.putString("loginDateTime", res.getPropertySafelyAsString("LoginDateTime", ""));
		editor.putString("ipAddress", res.getPropertySafelyAsString("IPAddress", ""));
		editor.putString("profilePic", res.getPropertySafelyAsString("ProfilePic", ""));
		editor.commit();
	}

	public static String getUserId(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);

		return sp.getString("userId", "-1");
	}

	public static void clearPreferences(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}

	public static String getEmail(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);

		return sp.getString("emailId", "-1");
	}
	public static String getUserName(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);

		return sp.getString("username", "-1");
	}
	public static String getProfilePic(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);

		return sp.getString("profilePic", "-1");
	}
	
	public static void setProfilePicture(Context ctx,String picName){
		SharedPreferences sp = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("profilePic", picName);
	}
	
	public static void saveUserPreferencesFromFbRegister(Context ctx, SoapObject res) {
		SharedPreferences sp = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);

		Editor editor = sp.edit();
		editor.putString("userId", res.getPropertySafelyAsString("UserId", ""));
		editor.putString("dob", res.getPropertySafelyAsString("DOB", ""));
		editor.putString("gender", res.getPropertySafelyAsString("Gender", ""));
		editor.putString("username", res.getPropertySafelyAsString("UserName", ""));
		editor.putString("password", res.getPropertySafelyAsString("Password", ""));
		editor.putString("emailId", res.getPropertySafelyAsString("EmailId", ""));
		editor.putString("isActive", res.getPropertySafelyAsString("IsActive", ""));
		editor.putString("registerFrom", res.getPropertySafelyAsString("RegisterFrom", ""));
		editor.putString("createDate", res.getPropertySafelyAsString("CreatedDate", ""));
		editor.putString("isAccess", res.getPropertySafelyAsString("IsAccess", ""));
		editor.putString("loginDateTime", res.getPropertySafelyAsString("LoginDateTime", ""));
		editor.putString("ipAddress", res.getPropertySafelyAsString("IPAddress", ""));
		editor.putString("profilePic", res.getPropertySafelyAsString("PhotoUrl", ""));
		editor.commit();
	}
}
