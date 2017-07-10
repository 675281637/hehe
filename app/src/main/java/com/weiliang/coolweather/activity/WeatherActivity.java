package com.weiliang.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weiliang.coolweather.HttpUtils;
import com.weiliang.coolweather.R;
import com.weiliang.coolweather.Utility;
import com.weiliang.coolweather.gson.Forecast;
import com.weiliang.coolweather.gson.Weather;
import com.weiliang.coolweather.service.AutoUpdateService;

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
    private ImageView mBingpicImg;
    public SwipeRefreshLayout mSwipeRefresh;
    public String mWeatherId;
    public DrawerLayout mDrawerLayout;
    private Button mNavButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将图片文字融入
        if (Build.VERSION.SDK_INT >=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
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
        mBingpicImg = (ImageView) findViewById(R.id.bing_pic_img);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavButton = (Button) findViewById(R.id.nav_button);
        mNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String bing_pic = sp.getString("bing_pic", null);
        if (bing_pic!=null){
            //glide加载
            Glide.with(this).load(bing_pic).into(mBingpicImg);
        }else{
            //从网上加载
            loadBingPic();
        }

        //sp
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mWeather = prefs.getString("weather", null);
        if (mWeather !=null){
            //有缓存时解析天气数据
            Weather weather = Utility.handleWeatherResponse(mWeather);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
             mWeatherId = getIntent().getStringExtra("weather_id");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);

        }
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //从服务器上重新申请一遍
                requestWeather(mWeatherId);
            }
        });
    }

    private void loadBingPic() {
     String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtils.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String bingPic = response.body().string();//url
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    edit.putString("bing_pic", bingPic);
                edit.commit();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(mBingpicImg);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        //获取数据后 展示
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        mTitleCity.setText(cityName);
        mTitleUpdateTime.setText(updateTime);
        mWeatherInfoText.setText(weatherInfo);
        mDegressText.setText(degree);
        mForecaseLayout.removeAllViews();//将天气预报的信息全删除
        //遍历天气预报的集合
        for (Forecast forecast : weather.forecastList) {
            //天气预报条目布局
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,mForecaseLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText= (TextView) view.findViewById(R.id.max_text);
            TextView minText= (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            mForecaseLayout.addView(view);
        }

        if (weather.aqi!=null){
            mAqiText.setText(weather.aqi.city.aqi);
            mPm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度"+weather.suggestion.comfort.info;
        String CarWash="洗车指数"+weather.suggestion.carWash.info;
        String sport="运动建议"+weather.suggestion.sport.info;
        mComfortText.setText(comfort);
        mCarWashText.setText(CarWash);
        mSportText.setText(sport);
        mWeatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

    }

    public void requestWeather(String weatherId) {
        String address="http://guolin.tech/api/weather?cityid="+weatherId+"&key=d67ca5531c72454fb927538b3a1494ad";
        HttpUtils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mSwipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //服务器请求数据
                final String responseText = response.body().string();
                //并解析
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null&& "ok".equals(weather.status)){
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("weather",responseText);
                            edit.commit();
                            showWeatherInfo(weather);



                        }else{
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        //申请玩之后，下拉进度条消失
                        mSwipeRefresh.setRefreshing(false);
                    }
                });

            }
        });

    }


}
