package com.easing.commons.android.manager;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    public static String paramToString(Map<String, ?> paramMap) {
        String appendUrl = "";
        for (String key : paramMap.keySet()) {
            Object value = paramMap.get(key);
            if (value != null) {
                if (appendUrl.startsWith("?"))
                    appendUrl = appendUrl + "&" + key + "=" + value;
                else
                    appendUrl = appendUrl + "?" + key + "=" + value;
            }
        }
        return appendUrl;
    }

    public static void saveCookies(Context ctx, String name, List<Cookie> cookies) {
        SharedPreferences sp = ctx.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (Cookie cookie : cookies)
            editor.putString(cookie.name(), cookie.value());
        editor.commit();
    }

    public static List<Cookie> loadCookies(Context ctx, String name) {
        SharedPreferences sp = ctx.getSharedPreferences(name, Context.MODE_PRIVATE);
        Map<String, ?> kvs = sp.getAll();
        List<Cookie> cookies = new ArrayList();
        for (String key : kvs.keySet()) {
            Cookie.Builder builder = new Cookie.Builder();
            builder.name(key);
            builder.value(kvs.get(key).toString());
            Cookie cookie = builder.build();
            cookies.add(cookie);
        }
        return cookies;
    }

    public static List<Cookie> EMPTY_COOKIE() {
        return new ArrayList();
    }

    public static FormBody paramToForm(Map<String, ?> paramMap) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramMap.keySet()) {
            Object value = paramMap.get(key);
            if (value != null)
                builder.add(key, value.toString());
        }
        FormBody formBody = builder.build();
        return formBody;
    }

    public static MultipartBody paramToMultipartForm(Map<String, Object> paramMap, Map<File, String> fileMap) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : paramMap.keySet()) {
            Object value = paramMap.get(key);
            if (value != null)
                builder.addFormDataPart(key, value.toString());
        }
        for (File file : fileMap.keySet()) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), file);
            builder.addFormDataPart(fileMap.get(file), file.getName(), fileBody);
        }
        MultipartBody formBody = builder.build();
        return formBody;
    }
}
