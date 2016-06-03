package com.app.test.foodrool.utils;

import android.content.Context;
import android.graphics.Typeface;

public class Fonts {
	private  static Typeface sansBold, sansLight;

	public static Typeface getSansBold(Context ctx) {
		if (sansBold == null)
			sansBold = Typeface.createFromAsset(ctx.getAssets(), "OPENSANS_BOLD.TTF");

		return sansBold;
	}

	public static Typeface getSansLight(Context ctx) {
		if (sansLight == null)
			sansLight = Typeface.createFromAsset(ctx.getAssets(), "OPENSANS_LIGHT.TTF");

		return sansLight;
	}
	
	
}
