package com.weiliang.coolweather.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weiliang.coolweather.HttpUtils;
import com.weiliang.coolweather.R;
import com.weiliang.coolweather.Utility;
import com.weiliang.coolweather.gson.Weather;

import java.io.IOException;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView mWeatherLayout;
    private TextView mTitleCity;
    private TextView mTitleUpdateTime;
    private TextView mDegressText;
    private TextView mWeatherInfoText;
    private LinearLayout mForecaseLayout;
    private TextView mAqiText;
    private TextView mPm25Text;
    private TextView mComfortText;
    private TextView mCarWashText;
    private TextView mSportText;
    private String mWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化控件
        mWeatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        mTitleCity = (TextView) findViewById(R.id.title_city);
        mTitleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        mDegressText = (TextView) findViewById(R.id.degress_text);
        mWeatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        mForecaseLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        mAqiText = (TextView) findViewById(R.id.aqi_text);
        mPm25Text = (TextView) findViewById(R.id.pm25_text);
        mComfortText = (TextView) findViewById(R.id.confort_text);
        mCarWashText = (TextView) findViewById(R.id.car_wash_text);
        mSportText = (TextView) findViewById(R.id.sport_text);
        //sp
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mWeather = prefs.getString("weather", null);
        if (mWeather !=null){
            //有缓存时解析天气数据
            Weather weather = Utility.handleWeatherResponse(mWeather);
           showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);

        }
    }

    private void showWeatherInfo(Weather weather) {
        //获取数据后 展示
        String cityName = weather.basic.cityName;
        String temperature = weather.now.temperature + "℃";
    }

    private void requestWeather(String weatherId) {
        String address="http://guolin.tech/api/weather?cityid="+weatherId+"&key=d67ca5531c72454fb927538b3a1494ad";
        HttpUtils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //服务器请求数据
                String responseText = response.body().string();
                //并解析
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("weather",null);
                            edit.commit();

                        }else{
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }


}
