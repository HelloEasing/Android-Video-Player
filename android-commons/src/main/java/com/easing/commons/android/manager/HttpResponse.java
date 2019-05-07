package com.easing.commons.android.manager;

import com.easing.commons.android.helper.data.JsonSerial;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.Response;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class HttpResponse<T> implements JsonSerial {

    public int code;
    public String message;
    public T data;

    public static <T> HttpResponse success(T data) {
        return new HttpResponse(0, "success", data);
    }

    public static HttpResponse fail(String message) {
        return new HttpResponse(-1, message, null);
    }

    public static HttpResponse fail(Exception e) {
        return new HttpResponse(-1, e.getClass().getName(), "");
    }

    @SneakyThrows
    public static <T> HttpResponse<T> parse(String json, Class<T> clazz) {
        JSONObject jsonObject = JsonUtil.toObject(json);
        HttpResponse<T> response = new HttpResponse();
        response.code = jsonObject.getInt("code");
        response.message = jsonObject.getString("message");
        if (jsonObject.has("data"))
            response.data = JsonUtil.fromJson(jsonObject.get("data").toString(), clazz);
        return response;
    }

    @SneakyThrows
    public static <T> HttpResponse<T> parse(Response response, Class<T> clazz) {
        return parse(response.body().string(), clazz);
    }

    public HttpResponse code(int coode) {
        this.code = coode;
        return this;
    }

    public HttpResponse message(String message) {
        this.message = message;
        return this;
    }

    public HttpResponse data(T data) {
        this.data = data;
        return this;
    }
}
