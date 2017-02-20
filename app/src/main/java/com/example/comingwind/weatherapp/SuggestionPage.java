package com.example.comingwind.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

/**
 * Created by ComingWind on 2017/2/13.
 */
public class SuggestionPage extends Activity {

    private List<IndexEntity> weatherSuggestions;

    private String cityId, cityName, aqi, quality, fengli, fengxiang, shidu, pm25;

    private TextView  chuanyi2, chuanyi3, chenlian2, chenlian3, xiche2, xiche3;

    private TextView sunRiseTime, sunSetTime;

    private String sunrise, sunset;

    private ImageButton backToMain, More;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.suggestionpage);
        backToMain = (ImageButton) findViewById(R.id.shouye);
        More = (ImageButton) findViewById(R.id.more);
        final Intent intent = getIntent();
        cityId = intent.getStringExtra("cityId");
        cityName = intent.getStringExtra("cityName");
        sunrise = intent.getStringExtra("sunrise");
        sunset = intent.getStringExtra("sunset");
        aqi = intent.getStringExtra("aqi");
        quality = intent.getStringExtra("quality");
        fengli = intent.getStringExtra("fengli");
        fengxiang = intent.getStringExtra("fengxiang");
        shidu = intent.getStringExtra("shidu");
        pm25 = intent.getStringExtra("pm25");
        new Thread(new Runnable() {
            @Override
            public void run() {
                getSuggestion();
            }
        }).start();
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SuggestionPage.this, currentData.class);
                intent1.putExtra("cityName", cityName);
                intent1.putExtra("fengxiang", fengxiang);
                intent1.putExtra("fengli", fengli);
                intent1.putExtra("aqi", aqi);
                intent1.putExtra("quality", quality);
                intent1.putExtra("shidu", shidu);
                intent1.putExtra("pm25", pm25);
                intent1.putExtra("ultrav", weatherSuggestions.get(6).getValue());
                intent1.putExtra("cold", weatherSuggestions.get(3).getDetail());
                startActivityForResult(intent1, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    //do nothing
                }
                if (resultCode == RESULT_CANCELED){
                    finish();
                }
                break;
        }
    }

    //在子线程访问网络
    public void getSuggestion(){
        try {
            HttpHelper httpHelper = new HttpHelper();
            URL url = new URL("http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityId);
            httpHelper.setUrl(url);
            weatherSuggestions = httpHelper.indices();
            Message message = new Message();
            message.what = 1;
            indicesHandler.sendMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //更新UI
    private Handler indicesHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            chuanyi2 = (TextView) findViewById(R.id.chuanyi2);
            chuanyi3 = (TextView) findViewById(R.id.chuanyi3);
            chenlian2 = (TextView) findViewById(R.id.chenlian2);
            chenlian3 = (TextView) findViewById(R.id.chenlian3);
            xiche2 = (TextView) findViewById(R.id.xiche2);
            xiche3 = (TextView) findViewById(R.id.xiche3);
            sunRiseTime = (TextView) findViewById(R.id.richushijian);
            sunSetTime = (TextView) findViewById(R.id.riluoshijian);
            switch (msg.what){
                case 1:
                    chuanyi2.setText(weatherSuggestions.get(2).getValue());
                    chuanyi3.setText(weatherSuggestions.get(2).getDetail());
                    chenlian2.setText(weatherSuggestions.get(0).getValue());
                    chenlian3.setText(weatherSuggestions.get(0).getDetail());
                    xiche2.setText(weatherSuggestions.get(7).getValue());
                    xiche3.setText(weatherSuggestions.get(7).getDetail());
                    sunSetTime.setText(sunset);
                    sunRiseTime.setText(sunrise);
                    break;
                default:
                    //test2.setText("Failed!");
                    break;
            }
        }
    };
}
