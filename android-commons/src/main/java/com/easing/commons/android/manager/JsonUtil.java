package com.easing.commons.android.manager;

import com.google.gson.Gson;

import lombok.SneakyThrows;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

public class JsonUtil {
  public static final Gson gson = new Gson();
  
  public static String toJson(Object obj) {
    return gson.toJson(obj);
  }
  
  public static <T> T fromJson(String json, Class<T> clz) {
    return gson.fromJson(json, clz);
  }
  
  public static <T> T fromJson(String json, Type type) {
    return (T) gson.fromJson(json, type);
  }
  
  @SneakyThrows
  public static <T> T fromMap(Map<String, Object> paramMap, Class<T> clz) {
    JSONObject jsonObject = new JSONObject();
    for (String key : paramMap.keySet())
      jsonObject.put(key, paramMap.get(key));
    return JsonUtil.fromJson(jsonObject.toString(), clz);
  }
  
  @SneakyThrows
  public static String getString(String json, String key) {
    return new JSONObject(json).getString(key);
  }
  
  @SneakyThrows
  public static int getInt(String json, String key) {
    return new JSONObject(json).getInt(key);
  }
  
  @SneakyThrows
  public static Integer getInt(JSONObject json, String key) {
    return json.getInt(key);
  }
  
  @SneakyThrows
  public static String getString(JSONObject json, String key) {
    if (json.has(key) && !json.isNull(key))
      return json.getString(key);
    return null;
  }
  
  @SneakyThrows
  public static JSONObject getObject(JSONObject json, String key) {
    return json.getJSONObject(key);
  }
  
  @SneakyThrows
  public static JSONArray getArray(JSONObject json, String key) {
    return json.getJSONArray(key);
  }
  
  @SneakyThrows
  public static JSONObject toObject(String json) {
    return new JSONObject(json);
  }
}
