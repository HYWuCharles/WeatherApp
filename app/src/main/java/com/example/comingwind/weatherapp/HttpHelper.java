package com.example.comingwind.weatherapp;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ComingWind on 2017/2/5.
 */
public class HttpHelper {

    private HttpURLConnection connection = null;

    private InputStream inputStream = null;

    private InputStream inputStream1 = null;

    private InputStream inputStream2 = null;

    private URL url = null;

    private Map<String, String> weatherInfo = null;

    private List<WeatherEntity> forecastInfo = null;

    private WeatherEntity weatherEntity = null;

    private List<IndexEntity> indexList = null;

    private IndexEntity indexEntity;

    public void setUrl(URL url) {
        this.url = url;
    }

    public Map<String, String> parseWeatherInfo() throws Exception {
        XmlPullParser xmlPullParser = Xml.newPullParser();
        String tag;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            inputStream = connection.getInputStream();
            xmlPullParser.setInput(inputStream, "utf-8");
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        weatherInfo = new HashMap<String, String>();
                        break;
                    case XmlPullParser.START_TAG:
                        tag = xmlPullParser.getName();
                        if (tag.equalsIgnoreCase("resp")) {
                            //xmlPullParser.nextTag();
                        } else if (tag.equalsIgnoreCase("city")) {
                            weatherInfo.put("city name", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("updatetime")) {
                            weatherInfo.put("update time", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("wendu")) {
                            weatherInfo.put("wendu", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("fengli")) {
                            weatherInfo.put("fengli", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("shidu")) {
                            weatherInfo.put("shidu", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("fengxiang")) {
                            weatherInfo.put("fengxiang", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("sunrise_1")) {
                            weatherInfo.put("sunrise_1", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("sunset_1")) {
                            weatherInfo.put("sunset_1", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("aqi")) {
                            weatherInfo.put("aqi", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("pm25")) {
                            weatherInfo.put("pm25", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("suggest")) {
                            weatherInfo.put("suggest", xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("quality")) {
                            weatherInfo.put("quality", xmlPullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherInfo;
    }

    public List<WeatherEntity> forecast() throws Exception {
        XmlPullParser xmlPullParser = Xml.newPullParser();
        String tag;
        int flcount = 0;
        int fxcount = 0;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            inputStream1 = connection.getInputStream();
            xmlPullParser.setInput(inputStream1, "utf-8");
            int eventType = xmlPullParser.getEventType();
            boolean day = true;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        forecastInfo = new ArrayList<WeatherEntity>();
                        break;
                    case XmlPullParser.START_TAG:
                        tag = xmlPullParser.getName();
                        if (tag.equalsIgnoreCase("weather")) {
                            weatherEntity = new WeatherEntity();
                        } else if (tag.equalsIgnoreCase("date")) {
                            weatherEntity.setDate(xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("high")) {
                            weatherEntity.setHighTemp(xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("low")) {
                            weatherEntity.setLowTemp(xmlPullParser.nextText());
                        } else if (tag.equalsIgnoreCase("type")) {
                            if (day) {
                                weatherEntity.setDay_type(xmlPullParser.nextText());
                                //Log.e("chaxunDaytype", weatherEntity.getDay_type());
                            }
                            if (!day) {
                                weatherEntity.setNight_type(xmlPullParser.nextText());
                                //Log.e("chaxunDaytype", weatherEntity.getNight_type());
                            }
                        } else if (tag.equalsIgnoreCase("fengxiang")) {
                            if (fxcount > 0) {
                                if (day) {
                                    weatherEntity.setDay_fengxiang(xmlPullParser.nextText());
                                    //Log.e("chaxunDaytype", weatherEntity.getDay_type());
                                }
                                if (!day) {
                                    weatherEntity.setNight_fengxiang(xmlPullParser.nextText());
                                }
                            }
                            fxcount++;
                        } else if (tag.equalsIgnoreCase("fengli")) {
                            if (flcount > 0) {
                                if (day) {
                                    weatherEntity.setDay_fengli(xmlPullParser.nextText());
                                }
                                if (!day) {
                                    weatherEntity.setNight_fengli(xmlPullParser.nextText());
                                }
                            }
                            flcount++;
                        }
                        Log.e("chaxunStartTag", tag);
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xmlPullParser.getName();
                        Log.e("chaxunEndTag", tag);
                        if (tag.equalsIgnoreCase("day")) {
                            day = false;
                        } else if (tag.equalsIgnoreCase("night")) {
                            day = true;
                            forecastInfo.add(weatherEntity);
                            weatherEntity = null;
                        }
                        Log.e("chaxunDay", Boolean.toString(day));
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (WeatherEntity weatherentity : forecastInfo) {
            Log.e("chaxunDate", weatherentity.getDate());
            Log.e("chaxunHighTemp", weatherentity.getHighTemp());
            Log.e("chaxunLowTemp", weatherentity.getLowTemp());
            Log.e("chaxunDay_fengli", weatherentity.getDay_fengli());
            Log.e("chaxunDay_fengxiang", weatherentity.getDay_fengxiang());
            Log.e("chaxunNight_fengli", weatherentity.getNight_fengli());
            Log.e("chaxunNight_fengxiang", weatherentity.getNight_fengxiang());
            Log.e("chaxunDay_type", weatherentity.getDay_type());
            Log.e("chaxunNight_type", weatherentity.getNight_type());
        }
        return forecastInfo;
    }

    public List<IndexEntity> indices() throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        String tag;
        String name = "";
        String value = "";
        String detail = "";
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            inputStream2 = connection.getInputStream();
            parser.setInput(inputStream2, "utf-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        indexList = new ArrayList<IndexEntity>();
                        break;
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.equalsIgnoreCase("zhishu")) {
                            indexEntity = new IndexEntity();
                        } else if (tag.equalsIgnoreCase("name")) {
                            indexEntity.setName(parser.nextText());
                        } else if (tag.equalsIgnoreCase("value")) {
                            indexEntity.setValue(parser.nextText());
                        } else if (tag.equalsIgnoreCase("detail")) {
                            indexEntity.setDetail(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if (tag.equalsIgnoreCase("zhishu")) {
                            indexList.add(indexEntity);
                            indexEntity = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return indexList;
    }


}
