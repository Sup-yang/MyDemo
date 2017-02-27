package com.example.yang.kuo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.kuo.R;
import com.example.yang.kuo.model.City;
import com.example.yang.kuo.model.CoolWeatherDB;

import com.example.yang.kuo.model.County;
import com.example.yang.kuo.model.Province;
import com.example.yang.kuo.util.HttpCallbackListener;
import com.example.yang.kuo.util.HttpUtil;
import com.example.yang.kuo.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang on 2017/2/27.
 */

public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE=0;

    public static final int LEVEL_CITY=1;

    public static final int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;
    private ListView listView;
    private TextView textView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList=new ArrayList<String>();



    /**
     * 省列表
     * */

    private List<Province> provinceList;

    /**
     * 城市列表
     * */

    private List<City> cityList;

    /**
     * 县列表
     * */

    private List<County> countryList;

    /***
     * 选中的省份
     *
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     * */
    private City selectedCity;
    /**
     *
     * 当前选中的级别
     * */
    private int currentLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected",false)){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView= (ListView) findViewById(R.id.list_view);
        textView= (TextView) findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);

        listView.setAdapter(adapter);

        coolWeatherDB= CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();

                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if (currentLevel==LEVEL_COUNTY){

                    String countyCode=countryList.get(position).getCountryCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);

                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                   finish();
                }
            }
        });

        queryProvinces();

    }


    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     * */


    private void queryProvinces(){

        provinceList=coolWeatherDB.loadProvinces();
        if (provinceList.size()>0){

            dataList.clear();
            for (Province province:provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);

            textView.setText("中国");

            currentLevel=LEVEL_PROVINCE;
        }else {

            queryFromServer(null,"province");
        }


    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     * */

    private void queryCities(){
        cityList=coolWeatherDB.laodCities(selectedProvince.getId());

        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }

    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有，再去服务器上查询
     * */

    private void queryCounties(){
        //countryList =coolWeatherDB.loadCountties(selectedCity.getId());

        countryList=coolWeatherDB.loadCountties(selectedCity.getId());
        if (countryList.size()>0){
            dataList.clear();
            for (County county:countryList){
                dataList.add(county.getCountryName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;


        }else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }

    }

    private void queryFromServer(final String code,final String type) {
        String address;

        if (!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else {
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if ("province".equals(type)){
                    result= Utility.handleProvincesResponse(coolWeatherDB,response);

                }else if("city".equals(type)){
                    result=Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if ("county".equals(type)){

                    result=Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }
                if (result){
                    //通过runOnuiThread方法回到主线程处理
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();

                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
/**
 * 显式进度对话框
 * */
    private void showProgressDialog() {

        if (progressDialog==null){
            progressDialog =new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);

        }
        progressDialog.show();
    }

    /**
     * 关闭对话框
     * */

    private void closeProgressDialog(){
        if (progressDialog!=null){

            progressDialog.dismiss();
        }
    }

    /**
     * 捕获back按键，根据当前级别来判断，此时应该返回的是什么；
     * */

    @Override
    public void onBackPressed() {
        if (currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {

           
            finish();
        }
    }
}

