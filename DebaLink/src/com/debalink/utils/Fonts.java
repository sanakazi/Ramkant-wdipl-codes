package com.debalink.utils;

import android.content.Context;
import android.graphics.Typeface;

public class Fonts {
	private  static Typeface normal, bold,futuraMedium,franklingGothic,franklingGothicHeavy,robotoLight,robotoCondenseLight,robotoMedium,robotoRegular;

	public static Typeface getNormal(Context ctx) {
		if (normal == null)
			normal = Typeface.createFromAsset(ctx.getAssets(), "GADUGI.TTF");

		return normal;
	}

	public static Typeface getBold(Context ctx) {
		if (bold == null)
			bold = Typeface.createFromAsset(ctx.getAssets(), "GADUGIB.TTF");

		return bold;
	}
	
	public static Typeface getFuturaMedium(Context ctx) {
		if (futuraMedium == null)
			futuraMedium = Typeface.createFromAsset(ctx.getAssets(), "futuramedium.TTF");

		return futuraMedium;
	}//
	
	public static Typeface getFranklingGothic(Context ctx) {
		if (franklingGothic == null)
			franklingGothic = Typeface.createFromAsset(ctx.getAssets(), "frankling_gothic.TTF");

		return franklingGothic;
	}//
	
	public static Typeface getFranklingGothicHeavy(Context ctx) {
		if (franklingGothicHeavy == null)
			franklingGothicHeavy = Typeface.createFromAsset(ctx.getAssets(), "frankling_gothic_heavy.TTF");

		return franklingGothicHeavy;
	}//
	
	public static Typeface getRobotoCondenseLight(Context ctx) {
		if (robotoCondenseLight == null)
			robotoCondenseLight = Typeface.createFromAsset(ctx.getAssets(), "robotocondensedlight.ttf");

		return robotoCondenseLight;
	}//
	
	public static Typeface getRobotoLight(Context ctx) {
		if (robotoLight == null)
			robotoLight = Typeface.createFromAsset(ctx.getAssets(), "robotolight.ttf");

		return robotoLight;
	}//
	
	public static Typeface getRobotoMedium(Context ctx) {
		if (robotoMedium == null)
			robotoMedium = Typeface.createFromAsset(ctx.getAssets(), "robotomedium.ttf");

		
		
		return robotoMedium;
	}//
	
	public static Typeface getRobotoRegular(Context ctx) {
		if (robotoRegular == null)
			robotoRegular = Typeface.createFromAsset(ctx.getAssets(), "robotoregular.ttf");

		return robotoRegular;
	}//
	
	public static String toUpper(String s) {
	    if (s!=null && s.length() > 0) {
	        return s.substring(0, 1).toUpperCase() + s.substring(1);
	    }
	    else
	       return s;
	}
}
