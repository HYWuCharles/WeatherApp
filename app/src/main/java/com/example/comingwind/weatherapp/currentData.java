package com.example.comingwind.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import java.net.URL;
import java.sql.Time;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by ComingWind on 2017/2/13.
 */
public class currentData extends Activity {

    private String cityName, aqi, quality, fengli, fengxiang, shidu, pm25, ultrav, cold;

    private TextView fengxiang1, fengxiang2, aqi2, aqi4, aqi6, shidu2, ziwaixian2, jianyi1, didian, shijian;

    private int h, m;

    private ImageButton fanhui, shouye;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.currentdata);
        fanhui = (ImageButton) findViewById(R.id.fanhui);
        shouye = (ImageButton) findViewById(R.id.shouye);
        Intent intent = getIntent();
        cityName = intent.getStringExtra("cityName");
        aqi = intent.getStringExtra("aqi");
        quality = intent.getStringExtra("quality");
        fengli = intent.getStringExtra("fengli");
        fengxiang = intent.getStringExtra("fengxiang");
        shidu = intent.getStringExtra("shidu");
        pm25 = intent.getStringExtra("pm25");
        ultrav = intent.getStringExtra("ultrav");
        cold = intent.getStringExtra("cold");

        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                setResult(RESULT_OK, intent1);
                finish();
            }
        });

        shouye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                setResult(RESULT_CANCELED, intent1);
                finish();
            }
        });

        getTime();
        updateUI();


    }

    public void updateUI(){
        //初始化
        fengxiang1 = (TextView) findViewById(R.id.fengxiang1);
        fengxiang2 = (TextView) findViewById(R.id.fengxiang2);
        aqi2 = (TextView) findViewById(R.id.aqi2);
        aqi4 = (TextView) findViewById(R.id.aqi4);
        aqi6 = (TextView) findViewById(R.id.aqi6);
        shidu2 = (TextView) findViewById(R.id.shidu2);
        ziwaixian2 = (TextView) findViewById(R.id.ziwaixian2);
        jianyi1 = (TextView) findViewById(R.id.jianyi1);
        didian = (TextView) findViewById(R.id.didian);
        shijian = (TextView) findViewById(R.id.shijian);
        fengxiang1.setText(fengxiang);
        fengxiang2.setText(fengli);
        aqi2.setText(aqi);
        aqi4.setText(quality);
        aqi6.setText(pm25);
        shidu2.setText(shidu);
        ziwaixian2.setText(ultrav);
        jianyi1.setText(cold);
        didian.setText(cityName);
        String hs = Integer.toString(h);
        String ms = Integer.toString(m);
        String time = hs+":"+ms;
        shijian.setText(time);
    }

    public void getTime(){
        long time = System.currentTimeMillis();
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        h = mCalendar.get(Calendar.HOUR);
        m = mCalendar.get(Calendar.MINUTE);
    }
}
