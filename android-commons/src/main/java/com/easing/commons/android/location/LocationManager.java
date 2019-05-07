package com.easing.commons.android.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.easing.commons.android.code.Console;
import com.easing.commons.android.manager.ServiceUtil;
import com.easing.commons.android.ui.app.CommonApplication;
import com.easing.commons.android.ui.dialog.TipBox;

import lombok.SneakyThrows;

import java.util.List;

@SuppressWarnings("all")
public class LocationManager {

    public static final String MIX_PROVIDER = "fused";

    @SneakyThrows
    public static void requestLocation(Context ctx) {
        android.location.LocationManager manager = (android.location.LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
            }
        };
        //建议手动每隔一段时间请求一次坐标
        //自带的定时请求接口运行效果不稳定
        Runnable r = new Runnable() {
            @Override
            public void run() {
                manager.requestSingleUpdate(MIX_PROVIDER, listener, CommonApplication.handler.getLooper());
                CommonApplication.handler.postDelayed(this, 2000);
            }
        };
        r.run();
    }


    public static Location getLocation(Context ctx) {
        android.location.LocationManager manager = (android.location.LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = manager.getProviders(true);
        if (providers == null || providers.size() == 0) {
            TipBox.tip("请打开GPS和位置服务");
            return null;
        }

        try {
            Location location = manager.getLastKnownLocation(LocationManager.MIX_PROVIDER);
            if (location != null)
                return location;
            location = manager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
            if (location != null)
                return location;
            location = manager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
            if (location != null)
                return location;
            location = manager.getLastKnownLocation(providers.get(0));
            if (location != null)
                return location;
        } catch (SecurityException e) {
            Console.error(e);
        }

        return null;
    }

    public static boolean isGpsLocateEnable(Context context) {
        android.location.LocationManager locationManager = ServiceUtil.getLocationManager(context);
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    public static boolean isNetLocateEnable(Context context) {
        android.location.LocationManager locationManager = ServiceUtil.getLocationManager(context);
        return locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
    }
}
