package com.example.comingwind.weatherapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.loc.r;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

public class OpenPage extends Activity {
    private Map<String,String> weather;

    private List<WeatherEntity> weatherEntityList;

    private AMapLocationClient mLocationClient = null;

    public String cityInfo = "";

    public String cityId;

    private TextView city_view;

    private TextView current_temp;

    private TextView current_weather;

    private ProgressDialog locationing;

    private TextView current_high_low_temp;

    private TextView today, tomorrow, day1, day2, day3;

    private ImageView todayIm, tomorrowIm, day1Im, day2Im, day3Im;

    private TextView todayTemp, tomorrowTemp, day1Temp, day2Temp, day3Temp;

    private ImageButton nextPage;

    private List<String> weekdays, types;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Button search_city = (Button) findViewById(R.id.button);
        //test = (TextView) findViewById(R.id.test_pullParser);
        //test1 = (TextView) findViewById(R.id.test_pullParser1);
        //test2 = (TextView) findViewById(R.id.test_pullParser2);
        city_view = (TextView) findViewById(R.id.city);
        current_temp = (TextView) findViewById(R.id.xianzaiwendu);
        current_weather = (TextView) findViewById(R.id.tianqi);
        current_high_low_temp = (TextView) findViewById(R.id.wendu);
        today = (TextView) findViewById(R.id.today);
        tomorrow = (TextView) findViewById(R.id.tomorrow);
        day1 = (TextView) findViewById(R.id.day1);
        day2 = (TextView) findViewById(R.id.day2);
        day3 = (TextView) findViewById(R.id.day3);
        todayIm = (ImageView) findViewById(R.id.todayIm);
        tomorrowIm = (ImageView) findViewById(R.id.tomorrowIm);
        day1Im = (ImageView) findViewById(R.id.day1Im);
        day2Im = (ImageView) findViewById(R.id.day2m);
        day3Im = (ImageView) findViewById(R.id.day3Im);
        nextPage = (ImageButton) findViewById(R.id.nextPage);
        todayTemp = (TextView) findViewById(R.id.todayTemp);
        tomorrowTemp = (TextView) findViewById(R.id.tomorrowTemp);
        day1Temp = (TextView) findViewById(R.id.day1Temp);
        day2Temp = (TextView) findViewById(R.id.day2Temp);
        day3Temp = (TextView) findViewById(R.id.day3Temp);
        //测试:changeIm(todayIm, "晴");
        locationing = new ProgressDialog(OpenPage.this);
        locationing.setMessage("定位中,请稍后...");
        locationing.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //虚拟机测试注释掉initlocation()和while语句,写死为北京
                    //真机测试可用定位,去掉initlocation()注释
                    initLoacation();
                    //异步调用,需要等待返回结果后再进行下一步
                    //此处可优化,判断cityInfo中是否有值
                    //Thread.sleep(500);
                    while (cityInfo.equalsIgnoreCase("")){}
                    //cityInfo = "北京";
                    findCityId(cityInfo);
                    locationing.dismiss();
                    //Log.e("City Info", cityInfo);
                    getWeatherInfo();
                }catch (Exception e){
                    Log.e("City Error",Log.getStackTraceString(e));
                }
            }
        }).start();
        search_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenPage.this, Select_City.class);
                startActivityForResult(intent, 1);
            }
        });

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenPage.this, SuggestionPage.class);
                intent.putExtra("cityId", cityId);
                intent.putExtra("sunrise", weather.get("sunrise_1"));
                intent.putExtra("sunset", weather.get("sunset_1"));
                intent.putExtra("shidu", weather.get("shidu"));
                intent.putExtra("aqi", weather.get("aqi"));
                intent.putExtra("fengli", weather.get("fengli"));
                intent.putExtra("fengxiang", weather.get("fengxiang"));
                intent.putExtra("pm25", weather.get("pm25"));
                intent.putExtra("quality", weather.get("quality"));
                intent.putExtra("cityName", cityInfo);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    Log.e("return data status", "----------->OK!");
                    String city_name_return_from_selection = data.getStringExtra("City_name");
                    cityInfo = city_name_return_from_selection;
                    Log.e("return data", "-------->"+city_name_return_from_selection);
                    try {
                    findCityId(city_name_return_from_selection);
                    new Thread(new Runnable() {
                        @Override
                        public void run(){
                                try{
                                    getWeatherInfo();
                                }catch (Exception e){
                                    Log.e("onActivityResult", Log.getStackTraceString(e));
                                }
                        }
                    }).start();
                    }catch (Exception e){
                        Log.e("onActivityResult1", Log.getStackTraceString(e));
                    }
                break;
        }
    }}

    public void getWeatherInfo() throws Exception{
                try {
                    Log.d("check onCreatMap", "-------->");
                    //此处需要更改citykey
                    URL url = new URL("http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityId);
                    HttpHelper httpHelper = new HttpHelper();
                    httpHelper.setUrl(url);
                    weather = httpHelper.parseWeatherInfo();
                    weatherEntityList = httpHelper.forecast();
                    //Log.e("weather", weather.toString());
                    Message message = new Message();
                    Message message1 = new Message();
                    message1.what = 1;
                    message.what = 1;
                    MapHandler.sendMessage(message);
                    ListHandler.sendMessage(message1);
                    /*
                    httpHelper.setUrl(url);
                    weatherIndicesList = httpHelper.indices();
                    Log.e("weaterIndicesList", weatherIndicesList.toString());
                    Message message2 = new Message();
                    message2.what = 1;
                    indicesHandler.sendMessage(message2);
                    */
                }catch (Exception e){
                    e.printStackTrace();
                    //Toast.makeText(OpenPage.this, "获取天气信息失败!", Toast.LENGTH_SHORT).show();
                    Message message = new Message();
                    message.what = 0;
                    ListHandler.sendMessage(message);
                }
            }

    //以下两个handler为在主线程更新UI
    private Handler MapHandler = new Handler(){
      public void handleMessage(android.os.Message msg){
          //List<String> weekdays = getWeekdays();
          //List<String> types = getType();
          switch (msg.what){
              case 1:
                  //test.setText(weather.get("city name"));
                  city_view.setText(weather.get("city name"));
                  current_temp.setText(weather.get("wendu")+"℃");
                  break;
              default:
                  //test.setText("Failed!");
                  Toast.makeText(OpenPage.this, "获取天气信息失败!", Toast.LENGTH_SHORT).show();
                  break;
          }
      }
    };

    private Handler ListHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            weekdays = getWeekdays();
            types = getType();
            switch (msg.what){
                case 1:
                    //test1.setText(weatherEntityList.get(0).getDate()+weatherEntityList.get(0).getHighTemp());
                    if (isDay()){
                        current_weather.setText(weatherEntityList.get(0).getDay_type());
                    }else {
                        current_weather.setText(weatherEntityList.get(0).getNight_type());
                    }
                    current_high_low_temp.setText(weatherEntityList.get(0).getHighTemp()+"/"+
                    weatherEntityList.get(0).getLowTemp());
                    today.setText(weekdays.get(0));
                    tomorrow.setText(weekdays.get(1));
                    day1.setText(weekdays.get(2));
                    day2.setText(weekdays.get(3));
                    day3.setText(weekdays.get(4));
                    todayTemp.setText(weatherEntityList.get(0).getHighTemp().substring(2,5)+"   "+
                    weatherEntityList.get(0).getLowTemp().substring(weatherEntityList.get(0).getLowTemp().indexOf("低温")+2));
                    tomorrowTemp.setText(weatherEntityList.get(1).getHighTemp().substring(2,5)+"   "+
                            weatherEntityList.get(1).getLowTemp().substring(weatherEntityList.get(1).getLowTemp().indexOf("低温")+2));
                    day1Temp.setText(weatherEntityList.get(2).getHighTemp().substring(2,5)+"   "+
                            weatherEntityList.get(2).getLowTemp().substring(weatherEntityList.get(2).getLowTemp().indexOf("低温")+2));
                    day2Temp.setText(weatherEntityList.get(3).getHighTemp().substring(2,5)+"   "+
                            weatherEntityList.get(3).getLowTemp().substring(weatherEntityList.get(3).getLowTemp().indexOf("低温")+2));
                    day3Temp.setText(weatherEntityList.get(4).getHighTemp().substring(2,5)+"   "+
                            weatherEntityList.get(4).getLowTemp().substring(weatherEntityList.get(4).getLowTemp().indexOf("低温")+2));
                    changeIm(todayIm, types.get(0));
                    changeIm(tomorrowIm, types.get(1));
                    changeIm(day1Im, types.get(2));
                    changeIm(day2Im, types.get(3));
                    changeIm(day3Im, types.get(4));
                    break;

                    //Toast.makeText(OpenPage.this, "Right!", Toast.LENGTH_SHORT).show();
                default:
                    //test1.setText("Failed!");
                    Toast.makeText(OpenPage.this, "Wrong!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //初始化定位及开始定位
    public void initLoacation(){
        //初始化client
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位参数
        mLocationClient.setLocationOption(getDefaultOption());
        //添加监听
        mLocationClient.setLocationListener(mLocationListener);
        //开始定位
        mLocationClient.startLocation();
    }

    //初始化设置
    public AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption temp = new AMapLocationClientOption();
        temp.setGpsFirst(false);
        temp.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        temp.setHttpTimeOut(30000);
        temp.setOnceLocation(true);
        temp.setNeedAddress(true);
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        return temp;
    }

    //设置监听器
    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null){
                if (aMapLocation.getErrorCode() == 0){
                    cityInfo = getLocationInfo(aMapLocation);
                    Log.e("Error Code", Integer.toString(aMapLocation.getErrorCode()));
                    Log.e("Current City", cityInfo);
                }else{
                    Toast.makeText(OpenPage.this, "定位失败!", Toast.LENGTH_SHORT).show();
                    locationing.dismiss();
                    Log.e("Error Code", Integer.toString(aMapLocation.getErrorCode()));
                }
            }else {
                Log.e("Error-->", "--------->");
            }
        }
    };

    //得到城市信息
    public String getLocationInfo(AMapLocation aMapLocation){
        StringBuffer sb = new StringBuffer();
        sb.append(aMapLocation.getCity());
        //Log.e("Current City Name", sb.toString());
        return sb.toString();
    }

    //遍历xml文档寻找城市代码
    public void findCityId(String cityName) throws Exception{
        InputStream xmlInput = getResources().openRawResource(R.raw.city_id_xml);
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try{
            xmlPullParser.setInput(xmlInput,"utf-8");
            int eventType = xmlPullParser.getEventType();
            String _cityName;
            String tag;
            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag = xmlPullParser.getName();
                        if (tag.equalsIgnoreCase("county")){
                            _cityName = xmlPullParser.getAttributeValue(1);
                            //Log.e("Attribute1:-->", xmlPullParser.getAttributeValue(1));
                            if (cityName.contains(_cityName)){
                                cityId = xmlPullParser.getAttributeValue(2);
                                //Log.e("Attribute2:-->", xmlPullParser.getAttributeValue(2));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("CityId", "No such city!");
        }
    }

    //判断是否是白天
    //0-6以及18-24为夜晚
    //6-18为白天
    public boolean isDay(){
        long time = System.currentTimeMillis();
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int hour = mCalendar.get(Calendar.HOUR);
        if ((hour >= 0 && hour <= 6) || (hour >= 18 && hour <=24)){
            return false;
        }
        else
            return true;
    }

    public List<String> getWeekdays(){
        List<String> weekdays = new ArrayList<String>();
        for (WeatherEntity weatherEntity : weatherEntityList){
            String temp = weatherEntity.getDate();
            int startPos = temp.indexOf("星期");
            int endPos = startPos + 3;
            weekdays.add(temp.substring(startPos, endPos));
        }
        return weekdays;
    }

    public List<String> getType(){
        List<String> types = new ArrayList<>();
        boolean isday = isDay();
        for (WeatherEntity weatherEntity : weatherEntityList){
            if (isday){
                String temp = weatherEntity.getDay_type();
                types.add(temp);
            }else if (!isday){
                String temp = weatherEntity.getNight_type();
                types.add(temp);
            }
        }
        for (String type : types){
            Log.e("Type test", "--->"+type);
        }
        return types;
    }

    public void changeIm(ImageView v, String weatherTpye){
        switch (weatherTpye){
            case "晴":
                v.setImageResource(R.drawable.qing);
                break;
            case "多云":
                v.setImageResource(R.drawable.duoyun);
                break;
            case "小雨":
                v.setImageResource(R.drawable.xiayu);
                break;
            case "大雨":
                v.setImageResource(R.drawable.xiayu);
                break;
            case "中雨":
                v.setImageResource(R.drawable.xiayu);
                break;
            case "阵雨":
                v.setImageResource(R.drawable.zhenyu);
                break;
            case "暴雨":
                v.setImageResource(R.drawable.baoyu);
                break;
            case "大暴雨":
                v.setImageResource(R.drawable.dabaoyu);
                break;
            case "小雪":
                v.setImageResource(R.drawable.xiaxue);
                break;
            case "中雪":
                v.setImageResource(R.drawable.xiaxue);
                break;
            case "大雪":
                v.setImageResource(R.drawable.xiaxue);
                break;
            case "暴雪":
                v.setImageResource(R.drawable.xiaxue);
                break;
            case "阴":
                v.setImageResource(R.drawable.yin);
                break;
            case "雾":
                v.setImageResource(R.drawable.wu);
                break;
            case "霾":
                v.setImageResource(R.drawable.mai);
                break;
            case "雨夹雪":
                v.setImageResource(R.drawable.yujiaxue);
                break;
            case "沙尘暴":
                v.setImageResource(R.drawable.shachenbao);
                break;
            case "冻雨":
                v.setImageResource(R.drawable.dongyu);
                break;
            case "扬尘":
                v.setImageResource(R.drawable.yangchen);
                break;
            case "浮尘":
                v.setImageResource(R.drawable.fuchen);
                break;
            default:
                v.setImageResource(R.drawable.qing);
                break;

        }
    }
}
