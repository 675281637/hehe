package com.weiliang.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weiliang.coolweather.HttpUtils;
import com.weiliang.coolweather.Utility;
import com.weiliang.coolweather.gson.Weather;

import java.io.IOException;

/**
 * 创建日期：2017/7/10 0010
 * 描述:
 * 作者:刘敏
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //更新天气以及更新背景bing图片
        updateWeather();
        updateBingPic();
        //隔一段时间更新,闹钟叫醒
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        //这个没有缓存，直接从接口获得
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtils.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String bingPic = response.body().string();
                //保存到缓存
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                edit.putString("bing_pic",bingPic);
                edit.commit();
            }
        });

    }

    private void updateWeather() {
        //从缓存里拿数据然后解析或者去服务器拿数据解析
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weather = sp.getString("weather", null);
        if (weather!=null){
            Weather weatherText = Utility.handleWeatherResponse(weather);
            String weatherId = weatherText.basic.weatherId;
            String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=d67ca5531c72454fb927538b3a1494ad";
            HttpUtils.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weatherText1 = Utility.handleWeatherResponse(responseText);
                    if (weatherText1!=null&&"ok".equals(weatherText1.status)){
                        //将获得数据保存到缓存里
                        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        edit.putString("weather",responseText);
                        edit.commit();

                    }
                }
            });
        }


    }
}
