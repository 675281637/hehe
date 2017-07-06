package com.weiliang.coolweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.weiliang.coolweather.db.City;
import com.weiliang.coolweather.db.County;
import com.weiliang.coolweather.db.Province;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/7/5 0005
 * 描述:
 * 作者:刘敏
 */

public class ChooseAreaFragment extends Fragment {
//省
    private static final int LEVEL_PROVINCE =0 ;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    @Nullable
    List<String> dataList=new ArrayList<>();
    private TextView mTitleText;
    private Button mBackButton;
    private ListView mListview;

    //记录当前选中的级别
    private int currentLevel;
    List<Province> provinceList;
    List<City> cityList;
    List<County> countyList;
    private Province mSelectProvince;
    private City mSelectCity;
    private ArrayAdapter<String> mAdapter;
    private ProgressDialog mProgressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        mTitleText = (TextView) view.findViewById(R.id.title_text);
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mListview = (ListView) view.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        mListview.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果当前选中的是省，创建省集合
                if (currentLevel==LEVEL_PROVINCE){
                    mSelectProvince = provinceList.get(position);
                    //点击省 查询市
                    queryCity();
                }else if(currentLevel==LEVEL_CITY){
                    //如果点击市，显示县
                    mSelectCity = cityList.get(position);
                    queryCounty();


                }
            }
        });
        //点击返回键
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //点击县返回到市
                if (currentLevel==LEVEL_CITY){
                    queryProvince();
                }else if(currentLevel==LEVEL_COUNTY){
                    queryCity();
                }

            }
        });
        //默认是查询省
        queryProvince();

    }
//查询全国的省，先从数据库 然后服务器
    private void queryProvince() {
        mTitleText.setText("中国");
        //在省的时候是没有返回键的
        mBackButton.setVisibility(View.GONE);
       provinceList= DataSupport.findAll(Province.class);
        //判断数据库里有没有，
        if (provinceList.size()>0){
            //将之前数据删除掉，只添加省的信息
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            //滑到第一个位置
           mListview.setSelection(0);
            currentLevel=LEVEL_PROVINCE;

        }else{
           // 没有去服务拿
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");

        }
    }

    private void queryCity() {
        mTitleText.setText(mSelectProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        //根据省的id查询对应的市
        cityList = DataSupport.where("provinceid=?", String.valueOf(mSelectProvince.getId())).find(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListview.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            //从服务器拿
            //先获得省的id
            int provinceCode = mSelectProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    private void queryCounty() {
        mTitleText.setText(mSelectCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        //根据市的id查询对应的市
        countyList = DataSupport.where("cityid=?", String.valueOf(mSelectCity.getId())).find(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            currentLevel=LEVEL_COUNTY;
            mListview.setSelection(0);
        }else{
            int cityCode = mSelectCity.getCityCode();
            int provinceCode = mSelectProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }

    }

    private void closeProgressDialog() {
        if (mProgressDialog!=null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog==null){
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }





    //根据传入的地址和类型从服务器查询省市县的数据
    private void queryFromServer(String address, final String type) {
        //先有个等候
        showProgressDialog();
        //连接服务器，并在后台解析数据
        HttpUtils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //如果连接服务器失败
                //在主线程告知链接失败
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            boolean mResult=false;
            @Override
            public void onResponse(Response response) throws IOException {
                String resonseText= response.body().string();
                //根据类型
                if ("province".equals(type)){
                    //解析省的数据
                    mResult = Utility.handleProvinceResponse(resonseText);
                }else if("city".equals(type)){
                    mResult=Utility.handleCityResponse(resonseText,mSelectProvince.getId());
                }else if("county".equals(type)){
                    mResult=Utility.handleCountyResponse(resonseText,mSelectCity.getId());
                }

                if (mResult){
                    //将数据ui展示
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();

                            }else if("city".equals(type)){
                                queryCity();

                            }else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }

            }
        });

    }

}
