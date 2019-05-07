package com.easing.commons.android.manager;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.easing.commons.android.value.measure.Size;

import java.lang.reflect.Field;

import lombok.SneakyThrows;

public class DeviceUtil {
	
	//获取屏幕大小
	public static Size getScreenSize(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		return new Size(dm.widthPixels, dm.heightPixels);
	}

	//获取下方导航栏高度
	public static int getNavigationBarHeight(Context context) {
		Resources resources = context.getResources();
		int res_id = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		return resources.getDimensionPixelSize(res_id);
	}

	//获取上方状态栏高度
	@SneakyThrows
	public static int getStatuBarHeight(Context context) {
		Class c = Class.forName("com.android.internal.R$dimen");
		Object obj = c.newInstance();
		Field field = c.getField("status_bar_height");
		int x = Integer.parseInt(field.get(obj).toString());
		return context.getResources().getDimensionPixelSize(x);
	}
}
