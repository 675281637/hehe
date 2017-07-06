package com.weiliang.coolweather.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.weiliang.coolweather.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //自己定义的java对象里的属性名跟json里的字段名是不一样,使用@SerializedName注解来将对象里的属性跟json里字段对应值匹配起来

    }
}
