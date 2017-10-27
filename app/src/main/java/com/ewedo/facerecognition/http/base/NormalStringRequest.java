package com.ewedo.facerecognition.http.base;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by fozei on 17-10-26.
 */

public class NormalStringRequest extends StringRequest {
    protected Map<String, String> mParams;
    protected Map<String, String> mHeaders;
    protected byte[] mBody;
    protected boolean mIsContain;

    public NormalStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public NormalStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
        } catch (UnsupportedEncodingException var4) {
            parsed = new String(response.data);
        }

        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return this.mParams;
    }

    protected void setParams(Map<String, String> params) {
        this.mParams = params;
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        if (this.mHeaders != null) {
            if (this.mIsContain) {
                Map<String, String> headers = super.getHeaders();
                if (headers.size() == 0) {
                    headers = this.mHeaders;
                } else {
                    headers.putAll(this.mHeaders);
                }

                return headers;
            } else {
                return this.mHeaders;
            }
        } else {
            return super.getHeaders();
        }
    }

    protected void setHeaders(Map<String, String> headers, boolean isContainSuperHeaders) {
        this.mIsContain = isContainSuperHeaders;
        this.mHeaders = headers;
    }

    public byte[] getBody() throws AuthFailureError {
        return this.mBody != null ? this.mBody : super.getBody();
    }

    protected void setBody(byte[] body) {
        this.mBody = body;
    }
}
