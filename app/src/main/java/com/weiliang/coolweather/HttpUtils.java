package com.weiliang.coolweather;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * 创建日期：2017/7/5 0005
 * 描述:请求服务器响应
 * 作者:刘敏
 */

public class HttpUtils {
    public static void sendOkHttpRequest(String address, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
