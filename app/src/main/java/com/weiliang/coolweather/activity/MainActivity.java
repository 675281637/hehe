package com.weiliang.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.weiliang.coolweather.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //自己定义的java对象里的属性名跟json里的字段名是不一样,使用@SerializedName注解来将对象里的属性跟json里字段对应值匹配起来
        //做缓存判断
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weather = sp.getString("weather", null);
        if (weather!=null){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
