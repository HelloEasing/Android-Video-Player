package com.easing.commons.android.manager;

import com.easing.commons.android.value.http.HttpMethod;

import lombok.SneakyThrows;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//调用顺序：设置Url -> 设置Method -> 设置Head -> 设置Param -> 设置Form -> 设置File -> 设置Option -> 设置ExceptionHandler -> 设置ResponseHandler -> 执行Call
public class Postman {

    private String url;
    private HttpMethod method;
    private Map<String, Object> paramMap = new HashMap();
    private Map<String, Object> formMap = new HashMap();
    private Map<String, Object> headMap = new HashMap();
    private Map<File, String> fileMap = new HashMap();
    private OnException onException;
    private OnResponse onResponse;

    private Request.Builder requestBuilder = new Request.Builder();
    private OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();


    //这四个变量没有实际作用，仅仅是方便外部调试
    private Call call;
    private Response response;
    private Exception exception;
    private Boolean success;

    private boolean useGlobalClient;
    private static final OkHttpClient DEFAULT_HTTP_CLIENT;

    static {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(2000, TimeUnit.MILLISECONDS);
        clientBuilder.readTimeout(10000, TimeUnit.MILLISECONDS);
        clientBuilder.writeTimeout(10000, TimeUnit.MILLISECONDS);
        clientBuilder.retryOnConnectionFailure(true);
        clientBuilder.connectionPool(new ConnectionPool(100, 5, TimeUnit.SECONDS));
        DEFAULT_HTTP_CLIENT = clientBuilder.build();
    }

    public static Postman create() {
        return new Postman();
    }

    private Postman() {
        useGlobalClient(true);
        enableLongConnection(false);
    }

    public Postman head(String key, Object value) {
        this.headMap.put(key, value);
        return this;
    }

    public Postman head(Map<String, Object> headMap) {
        this.headMap.putAll(headMap);
        return this;
    }

    public Postman param(String key, Object value) {
        this.paramMap.put(key, value);
        return this;
    }

    public Postman param(Map<String, Object> paramMap) {
        this.paramMap.putAll(paramMap);
        return this;
    }

    public Postman param(Object entity) {
        BeanUtil.copyAttribute(entity, paramMap);
        return this;
    }

    public Postman form(String key, Object value) {
        this.formMap.put(key, value);
        return this;
    }

    public Postman form(Map<String, Object> formMap) {
        this.formMap.putAll(formMap);
        return this;
    }

    public Postman form(Object entity) {
        BeanUtil.copyAttribute(entity, formMap);
        return this;
    }

    public Postman file(String key, String file) {
        fileMap.put(new File(file), key);
        return this;
    }

    public Postman file(String key, File file) {
        fileMap.put(file, key);
        return this;
    }

    public Postman url(String url) {
        this.url = url;
        requestBuilder.url(url);
        return this;
    }

    public Postman method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public Postman onException(OnException onException) {
        this.onException = onException;
        return this;
    }

    public Postman onResponse(OnResponse onResponse) {
        this.onResponse = onResponse;
        return this;
    }

    public Postman execute(boolean sync) {
        return sync ? executeInSerial() : executeInParallel();
    }

    @SneakyThrows
    public Postman executeInSerial() {
        //设置请求头
        for (String key : headMap.keySet()) {
            String value = headMap.get(key).toString();
            requestBuilder.header(key, value);
        }
        //设置URL
        if (paramMap.size() > 0) {
            this.url = url + HttpUtil.paramToString(paramMap);
            requestBuilder.url(url);
        }
        //设置请求体
        switch (method) {
            case GET: {
                requestBuilder.get();
                break;
            }
            case URL_ENCODED_POST: {
                FormBody formBody = HttpUtil.paramToForm(formMap);
                requestBuilder.post(formBody);
                break;
            }
            case MULTI_FORM_POST: {
                MultipartBody multipartBody = HttpUtil.paramToMultipartForm(formMap, fileMap);
                requestBuilder.post(multipartBody);
                break;
            }
            case RAW_POST: {
                break;
            }
            case BINARY_POST: {
                break;
            }
            case PUT: {
                FormBody formBody = HttpUtil.paramToForm(formMap);
                requestBuilder.put(formBody);
                break;
            }
            case PATCH: {
                FormBody formBody = HttpUtil.paramToForm(formMap);
                requestBuilder.patch(formBody);
                break;
            }
            case DELETE: {
                FormBody formBody = HttpUtil.paramToForm(formMap);
                requestBuilder.delete(formBody);
                break;
            }
        }
        //执行请求
        Request request = requestBuilder.build();
        OkHttpClient client = useGlobalClient ? DEFAULT_HTTP_CLIENT : clientBuilder.build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if (onResponse != null)
                onResponse.onResponse(call, response);
        } catch (Exception e) {
            if (onException != null)
                onException.onException(call, e);
        }
        return this;
    }

    @SneakyThrows
    public Postman executeInParallel() {
        //设置请求头
        for (String key : headMap.keySet()) {
            String value = headMap.get(key).toString();
            requestBuilder.header(key, value);
        }
        //设置URL
        if (paramMap.size() > 0) {
            this.url = url + HttpUtil.paramToString(paramMap);
            requestBuilder.url(url);
        }
        //设置请求体
        //执行请求
        Request request = requestBuilder.build();
        OkHttpClient client = useGlobalClient ? DEFAULT_HTTP_CLIENT : clientBuilder.build();
        Call call = client.newCall(request);
        Postman.this.call = call;
        Callback callback = new Callback() {
            @Override
            @SneakyThrows
            public void onFailure(Call call, IOException e) {
                Postman.this.exception = e;
                if (onException != null)
                    onException.onException(call, e);
            }

            @Override
            @SneakyThrows
            public void onResponse(Call call, Response response) {
                Postman.this.response = response;
                Postman.this.success = response.code() == 200;
                if (onResponse != null)
                    onResponse.onResponse(call, response);
            }
        };
        call.enqueue(callback);
        return this;
    }


    //设置连接超时（与服务器连接成功所需时间）
    public Postman connectTimeOut(long ms) {
        clientBuilder.connectTimeout(ms, TimeUnit.MILLISECONDS);
        return this;
    }

    //设置写数据超时（上传文件，上传请求数据所需时间）
    public Postman writeTimeOut(long ms) {
        clientBuilder.writeTimeout(ms, TimeUnit.MILLISECONDS);
        return this;
    }

    //设置读数据超时（下载文件，读取回复数据所需时间）
    public Postman readTimeOut(long ms) {
        clientBuilder.readTimeout(ms, TimeUnit.MILLISECONDS);
        return this;
    }

    public Postman retry(boolean retry) {
        clientBuilder.retryOnConnectionFailure(retry);
        return this;
    }

    public Postman enableLongConnection(boolean enable) {
        if (enable)
            requestBuilder.header("Connection", "keep-alive");
        else
            requestBuilder.header("Connection", "close");
        return this;
    }

    public Postman useGlobalClient(boolean useGlobalClient) {
        this.useGlobalClient = useGlobalClient;
        return this;
    }

    public interface OnException {
        void onException(Call call, Exception e) throws Exception;
    }

    public interface OnResponse {
        void onResponse(Call call, Response response) throws Exception;
    }
}
