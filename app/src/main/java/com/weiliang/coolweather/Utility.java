package com.weiliang.coolweather;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.weiliang.coolweather.db.City;
import com.weiliang.coolweather.db.County;
import com.weiliang.coolweather.db.Province;
import com.weiliang.coolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 创建日期：2017/7/5 0005
 * 描述:解析服务器返回过来的数据
 * 作者:刘敏
 */

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvince=new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++) {
                    JSONObject jsonObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();
                }
                return true;

            } catch (JSONException e) {
            }
        }
        return false;

    }

    //解析市级数据
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity=new JSONArray(response);
                for (int i = 0; i < allCity.length(); i++) {
                    JSONObject jsonObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;

            } catch (JSONException e) {
            }
        }
        return false;

    }

    //县级
    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounty=new JSONArray(response);
                for (int i = 0; i < allCounty.length(); i++) {
                    JSONObject jsonObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;

            } catch (JSONException e) {
            }
        }
        return false;

    }

    //解析天气数据

    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);

        } catch (JSONException e) {
        }
        return null;
    }

}
