package com.linkloving.helan.http.basic;

import android.text.TextUtils;

import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.StringRequest;

/**
 * Created by Administrator on 2016/6/3.
 */
public class MyJsonRequest extends StringRequest {

    public MyJsonRequest(String url) {
        super(url, RequestMethod.POST);
        setContentTypeJson();
    }

    public MyJsonRequest(String url,RequestMethod method) {
        super(url,method);
        setContentTypeJson();
    }

    private String contentType;

    public void setContentTypeJson() {
        this.contentType = "application/json";
    }

    @Override
    public String getContentType() {
        return TextUtils.isEmpty(contentType) ? super.getContentType() : contentType;
    }
//
//    @Override
//    public String getUserAgent() {
//        return "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36";
//    }


    @Override
    public String getAccept() {
        return "application/json,application/xml,text/html,application/xhtml+xml";
    }
}
