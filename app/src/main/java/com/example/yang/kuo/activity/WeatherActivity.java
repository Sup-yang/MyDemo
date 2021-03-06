package com.example.yang.kuo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yang.kuo.R;
import com.example.yang.kuo.service.AutoUpdateService;
import com.example.yang.kuo.util.HttpCallbackListener;
import com.example.yang.kuo.util.HttpUtil;
import com.example.yang.kuo.util.Utility;

/**
 * Created by yang on 2017/2/27.
 */

public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;

    /**
     * 用于显示城市名
     * */


    private TextView cityNameText;
    /**
     * 发布时间
     * */

    private TextView publisText;

//天气描述
    private  TextView weatherDespText;

    //显式气温1
    private TextView temp1Text;

    //显示气温2
    private TextView temp2Text;

    //显示当前时间
    private TextView currentDateText;

    private Button switchCity;

    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.weather_layout);

        weatherInfoLayout= (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText= (TextView) findViewById(R.id.city_name);
        publisText= (TextView) findViewById(R.id.publish_text);
        weatherDespText= (TextView) findViewById(R.id.weather_desp);
        temp1Text= (TextView) findViewById(R.id.temp1);
        temp2Text= (TextView) findViewById(R.id.temp2);
        currentDateText= (TextView) findViewById(R.id.current_data);
        switchCity= (Button) findViewById(R.id.switch_city);
        refreshWeather= (Button) findViewById(R.id.refresh_weather);

        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);

        String countyCode=getIntent().getStringExtra("county_code");

        if (!TextUtils.isEmpty(countyCode)){

            publisText.setText("同步中.....");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);

        }else{

            showWeather();
        }

    }

    private void queryWeatherInfo(String weatherCode){

        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");

    }



    private void queryWeatherCode(String countyCode) {
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"countyCode");
    }

    private void queryFromServer(final String address, final String type) {

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)){
                    if (!TextUtils.isEmpty(response)){

                        String[] array=response.split("\\|");
                        if (array!=null&&array.length==2){

                            String weatherCode=array[1];
                            queryWeatherInfo(weatherCode);
                        }

                    }
                }else if ("weatherCode".equals(type)){
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publisText.setText("同步失败");
                    }
                });
            }
        });
    }
    private void showWeather() {

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publisText.setText(prefs.getString("publish_time","")+"发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        Intent intent=new Intent(WeatherActivity.this, AutoUpdateService.class);
        startService(intent);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.switch_city:

                Intent intent=new Intent(this,ChooseAreaActivity.class);

                intent.putExtra("from_weather_activity",true);

                startActivity(intent);

                finish();
                break;

            case R.id.refresh_weather:

                publisText.setText("同步中.....");
                SharedPreferences prfs= PreferenceManager.getDefaultSharedPreferences(this);

                String weather= prfs.getString("weather_code","");
                if (!TextUtils.isEmpty(weather)){

                    queryWeatherInfo(weather);
                }

                break;
            default:
                break;

        }
    }
}
