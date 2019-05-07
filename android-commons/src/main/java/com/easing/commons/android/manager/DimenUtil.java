package com.easing.commons.android.manager;

import android.content.Context;

public class DimenUtil {
	
	//dp转px
	public static int toPx(Context ctx, float dp) {
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
	
	//px转dp
	public static int toDp(Context ctx, float px) {
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}
}
