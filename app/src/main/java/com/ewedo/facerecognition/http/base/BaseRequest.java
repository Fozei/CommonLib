package com.ewedo.facerecognition.http.base;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.ewedo.facerecognition.BaseApplication;
import com.ewedo.facerecognition.http.NetworkUtils;
import com.ewedo.facerecognition.http.ThreadUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by fozei on 17-10-26.
 * 网络请求
 * 支持设置请求头、请求体
 */

public abstract class BaseRequest<E extends BaseBean> {
    protected RequestQueue mQueue;
    protected BaseCallback<E> mCall;
    protected Class<E> mClazz;
    protected Map<String, String> mParams;
    protected Map<String, String> mHeaders;
    protected byte[] mBody;
    protected boolean mIsContain;
    protected String mEncoding = "utf-8";
    protected Response.Listener<String> mListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String data) {
            if (mCall != null) {
                if (mCall instanceof BaseRequest.JsonCallBack) {
                    JsonCallBack<E> call = (JsonCallBack<E>) mCall;
                    E e = parseData(data);
                    call.result(200, e);
                } else if (mCall instanceof BaseRequest.ArrayCallBack) {
                    ArrayCallBack<E> call = (ArrayCallBack<E>) mCall;
                    call.result(200, parseArrayData(data));
                }
            }
        }
    };
    protected Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError volleyError) {
            NetworkResponse networkResponse = volleyError.networkResponse;

            if (mCall != null) {
                if (mCall instanceof BaseRequest.JsonCallBack) {
                    JsonCallBack<E> call = (JsonCallBack<E>) mCall;
                    if (networkResponse != null) {
                        call.result(networkResponse.statusCode, null);
                    } else if (volleyError instanceof TimeoutError) {
                        call.result(-2, null);
                    } else {
                        call.result(-1, null);
                    }
                } else if (mCall instanceof BaseRequest.ArrayCallBack) {
                    ArrayCallBack<E> call = (ArrayCallBack<E>) mCall;
                    if (networkResponse != null) {
                        call.result(networkResponse.statusCode, null);
                    } else if (volleyError instanceof TimeoutError) {
                        call.result(-2, null);
                    } else {
                        call.result(-1, null);
                    }
                }
            }

        }
    };
    private boolean mShouldCache = true;

    public boolean startQuery(JsonCallBack<E> call, Class<E> beanClass) {
        return startQuery(call, beanClass, null);
    }

    public boolean startQuery(int method, JsonCallBack<E> call, Class<E> beanClass) {
        return startQuery(method, call, beanClass, null);
    }

    public boolean startQuery(JsonCallBack<E> call, Class<E> beanClass, Map<String, String> params) {
        return startQuery(0, call, beanClass, params);
    }


    public boolean startQuery(ArrayCallBack<E> call, Class<E> beanClass) {
        return startQuery(call, beanClass, null);
    }

    public boolean startQuery(int method, ArrayCallBack<E> call, Class<E> beanClass) {
        return startQuery(method, call, beanClass, null);
    }

    public boolean startQuery(ArrayCallBack<E> call, Class<E> beanClass, Map<String, String> params) {
        return startQuery(0, call, beanClass, params);
    }

    public <T extends BaseCallback> boolean startQuery(int method, final T call, Class<E> beanClass, Map<String, String> params) {
        if (method >= -1 && method <= 7) {
            if (call != null && beanClass == null) {
                return false;
            } else {
                if (mQueue == null) {
                    mQueue = BaseApplication.getRequestQueue();
                }

                if (mQueue == null) {
                    return false;
                } else if (!NetworkUtils.isNetworkConnected(BaseApplication.getContext())) {
                    ThreadUtils.runInMainThreadLater(new Runnable() {
                        public void run() {
                            if (call != null) {
                                if (call instanceof BaseRequest.JsonCallBack) {
                                    JsonCallBack<E> callback = (JsonCallBack<E>) call;
                                    callback.result(-3, null);
                                } else if (call instanceof BaseRequest.ArrayCallBack) {
                                    ArrayCallBack<E> callback = (ArrayCallBack<E>) call;
                                    callback.result(-3, null);
                                }
                            }

                        }
                    }, 600L);
                    return false;
                } else {
                    mCall = call;
                    mClazz = beanClass;
                    if (mParams != null && mParams.size() > 0) {
                        if (params == null) {
                            params = mParams;
                        } else {
                            params.putAll(mParams);
                        }
                    }

                    String url;
                    if (0 == method) {
                        if (params != null && params.size() > 0) {
                            url = initGetQuestParams(getQueryUrl(), params);
                        } else {
                            url = getQueryUrl();
                        }
                    } else {
                        url = getQueryUrl();
                        if (params != null) {
                            setParams(params);
                        }
                    }

                    if (TextUtils.isEmpty(url)) {
                        return false;
                    } else {
                        NormalStringRequest normalStringRequest = new NormalStringRequest(method, url, mListener, mErrorListener);
                        encodingHeaders(normalStringRequest);
                        encodingParams(normalStringRequest);
                        normalStringRequest.setBody(mBody);
                        if (!TextUtils.isEmpty(requestTag())) {
                            normalStringRequest.setTag(requestTag());
                        }

                        if (createRetryPolicy() != null) {
                            normalStringRequest.setRetryPolicy(createRetryPolicy());
                        } else {
                            normalStringRequest.setRetryPolicy(createDefaultRetryPolicy());
                        }

                        normalStringRequest.setShouldCache(mShouldCache);
                        mQueue.add(normalStringRequest);
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
    }

    private String getQueryUrl() {
        return QueryDomain() + QueryApi();
    }

    protected RetryPolicy createRetryPolicy() {
        return null;
    }

    private RetryPolicy createDefaultRetryPolicy() {
        return new DefaultRetryPolicy(2500, 0, 1.0F);
    }

    protected String requestTag() {
        return null;
    }

    public void cancelAll() {
        if (!TextUtils.isEmpty(requestTag())) {
            mQueue.cancelAll(requestTag());
        }

    }

    public BaseRequest setParams(Map<String, String> params) {
        mParams = params;
        return this;
    }

    public void setRequestShouldCache(boolean shouldCache) {
        mShouldCache = shouldCache;
    }

    public BaseRequest setHeaders(Map<String, String> headers, boolean isContainSuperHeaders) {
        mIsContain = isContainSuperHeaders;
        mHeaders = headers;
        return this;
    }

    public BaseRequest setEncodingFormat(String encoding) {
        mEncoding = encoding;
        return this;
    }

    public BaseRequest setBody(byte[] body) {
        mBody = body;
        return this;
    }

    private E parseData(String data) {
        E bean = null;
        try {
            bean = JSON.parseObject(data, mClazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bean;
    }

    private ArrayList<E> parseArrayData(String data) {
        ArrayList<E> beanList = null;
        try {
            beanList = (ArrayList<E>) JSON.parseArray(data, mClazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return beanList;
    }

    private void encodingHeaders(NormalStringRequest request) {
        try {
            if (mHeaders != null && mHeaders.size() > 0) {
                if (!TextUtils.isEmpty(mEncoding)) {

                    for (String key : mHeaders.keySet()) {
                        if (mHeaders.get(key) == null) {
                            mHeaders.put(key, mHeaders.get(key));
                        } else if ("".equals(mHeaders.get(key))) {
                            mHeaders.put(key, URLEncoder.encode(mHeaders.get(key), mEncoding));
                        } else {
                            mHeaders.put(key, URLEncoder.encode(mHeaders.get(key), mEncoding));
                        }
                    }
                }

                request.setHeaders(mHeaders, mIsContain);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void encodingParams(NormalStringRequest request) {
        try {
            if (mParams != null && mParams.size() > 0) {
                if (!TextUtils.isEmpty(mEncoding)) {
                    Iterator e = mParams.keySet().iterator();

                    while (e.hasNext()) {
                        String key = (String) e.next();
                        if (mParams.get(key) == null) {
                            mParams.put(key, mParams.get(key));
                        } else if ("".equals(mParams.get(key))) {
                            mParams.put(key, URLEncoder.encode(mParams.get(key), mEncoding));
                        } else {
                            mParams.put(key, URLEncoder.encode(mParams.get(key), mEncoding));
                        }
                    }
                }

                request.setParams(mParams);
            }
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

    }

    private String initGetQuestParams(String url, Map<String, String> paramsMap) {
        StringBuilder sb = new StringBuilder();
        if (!url.contains("?")) {
            url = url + "?";
        } else if (!url.endsWith("?")) {
            url = url + "&";
        }

        sb.append(url);

        try {
            if (paramsMap != null && paramsMap.size() > 0) {
                Iterator e;
                String key;
                if (TextUtils.isEmpty(mEncoding)) {
                    e = paramsMap.keySet().iterator();

                    while (e.hasNext()) {
                        key = (String) e.next();
                        sb.append(key).append("=");
                        sb.append(paramsMap.get(key));
                        sb.append("&");
                    }
                } else {
                    for (e = paramsMap.keySet().iterator(); e.hasNext(); sb.append("&")) {
                        key = (String) e.next();
                        sb.append(key).append("=");
                        if (paramsMap.get(key) == null) {
                            sb.append(paramsMap.get(key));
                        } else if ("".equals(paramsMap.get(key))) {
                            sb.append(URLEncoder.encode(paramsMap.get(key), mEncoding));
                        } else {
                            sb.append(URLEncoder.encode(paramsMap.get(key), mEncoding));
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException var6) {
            var6.printStackTrace();
        }

        if (sb.lastIndexOf("&") == sb.length() - 1) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    protected abstract String QueryDomain();

    protected abstract String QueryApi();

    public interface ArrayCallBack<T extends BaseBean> extends BaseCallback {
        void result(int resultCode, ArrayList<T> result);
    }

    public interface JsonCallBack<T extends BaseBean> extends BaseCallback {
        void result(int resultCode, T result);
    }
}
