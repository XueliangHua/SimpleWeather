package com.simpleweather.app.simpleweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simpleweather.app.simpleweather.R;
import com.simpleweather.app.simpleweather.util.HttpCallbackListener;
import com.simpleweather.app.simpleweather.util.HttpUtil;
import com.simpleweather.app.simpleweather.util.Utility;

public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weartherInfoLayout;
    // 用于显示城市名
    private TextView cityNameText;
    //用于显示发布时间
    private TextView publishText;
    // 用于显示天气描述信息
    private TextView weatherDespText;
    //用于显示气温1
    private TextView temp1Text;
    // 用于显示气温2
    private TextView temp2Text;
    //用于显示当前日期
    private TextView currentDateText;
    //切换城市按钮
    private Button switchCity;
    // 更新天气按钮
    private Button refreshWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        //初始化各控件
        weartherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.btn_switch_city);
        refreshWeather = (Button) findViewById(R.id.btn_refresh_weather);

        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            // 有县级代号时就去查询天气
            publishText.setText("同步中...");
            weartherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(countyCode);
        } else {
            // 没有县级代码时就显示本地天气
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String countyCode = prefs.getString("county_code", "");
                if (!TextUtils.isEmpty(countyCode)) {
                    queryWeatherInfo(countyCode);
                }
                break;
        }
    }

    // 查询天气代号所对应的天气
    public void queryWeatherInfo(String countyCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + countyCode + ".html";
        queryFromServer(address);
    }

    //根据传入的地址去服务器查询天气信息
    private void queryFromServer(final String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {
                // 处理服务器返回的天气信息
                if (Utility.handleWeatherResponse(WeatherActivity.this, response)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            publishText.setText("同步失败");
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    //从SharedPreferences文件中读取存储的天气信息，并显示到界面上
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weartherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
//        Intent intent = new Intent(this, AutoUpdateService.class);
//        startService(intent);
    }
}
