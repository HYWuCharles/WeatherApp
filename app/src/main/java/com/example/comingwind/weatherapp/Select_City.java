package com.example.comingwind.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ComingWind on 2017/1/31.
 */
public class Select_City extends Activity {

    private String[] capitals_list = {"北京","天津","上海","重庆","石家庄","太原","西安","济南","郑州","沈阳",
            "长春","哈尔滨","南京","杭州","合肥","南昌","福州","武汉","长沙","成都","贵阳","昆明","广州","海口",
            "兰州","西宁","台北","呼和浩特","乌鲁木齐","拉萨","南宁","银川","香港","澳门"
    };

    private ArrayList<String> Capital_City_list = new ArrayList<>();
    private EditText enter_city_name_text;
    private ListView listView;
    private ListView search_city_result;
    private List<City> city_list;
    private Map<String, String> cities_map;

    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_city_layout);

        //构建界面
        enter_city_name_text = (EditText)findViewById(R.id.enter_city_name);
        enter_city_name_text.clearFocus();
        initCapitalCities();
        ArrayAdapter<String> capitals_adapter = new ArrayAdapter<String>(Select_City.this,
                android.R.layout.simple_list_item_1, Capital_City_list);
        listView = (ListView) findViewById(R.id.init_city_names);
        search_city_result = (ListView) findViewById(R.id.search_city_result);
        search_city_result.setVisibility(View.GONE);
        listView.setAdapter(capitals_adapter);

        //解析XML文档
        try {
            parseXMLWithPull(city_list);
        }catch (Exception e){}

        enter_city_name_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<City> containing_cities = searchCity(s.toString());
                Log.d("check changing: -->","OK");
                if (containing_cities.size() != 2565 && containing_cities.size() != 0){             //2565是中国城市数目
                    listView.setVisibility(View.GONE);
                    final ArrayList<String> city_result = new ArrayList<String>();
                    for (City city : containing_cities){
                        city_result.add(city.getCityname());
                    }
                    ArrayAdapter<String> cities_adapter = new ArrayAdapter<String>(Select_City.this,
                            android.R.layout.simple_list_item_1, city_result);
                    search_city_result.setAdapter(cities_adapter);
                    search_city_result.setVisibility(View.VISIBLE);
                    search_city_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            City temp = new City();
                            temp.setCityname(city_result.get(position));
                            enter_city_name_text.setText(temp.getCityname());
                            enter_city_name_text.setSelection(temp.getCityname().length());
                            Intent return_city_intent = new Intent();
                            return_city_intent.putExtra("City_name", temp.getCityname());
                            setResult(RESULT_OK, return_city_intent);
                            finish();
                        }
                    });
                }
                if(containing_cities.size() == 2565){
                    listView.setVisibility(View.VISIBLE);
                    search_city_result.setVisibility(View.GONE);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City temp = new City();
                temp.setCityname(Capital_City_list.get(position));
                enter_city_name_text.setText(temp.getCityname());
                enter_city_name_text.setSelection(temp.getCityname().length());
                Intent return_city_intent = new Intent();
                return_city_intent.putExtra("City_name", temp.getCityname());
                setResult(RESULT_OK, return_city_intent);
                finish();
            }
        });
    }

    /*
     *初始化省会列表
     */
    public void initCapitalCities(){

        int i = 0;

        while(i < capitals_list.length){
            Capital_City_list.add(capitals_list[i]);
            i++;
        }


    }

    private void parseXMLWithPull(List<City> _city_list) throws Exception{
        Log.d("Parse Starts:","---------------->");
        InputStream inputStream = getResources().openRawResource(R.raw.city_id_xml);
        XmlPullParser xmlPullParser = Xml.newPullParser();
        City city;
        city_list = new ArrayList<City>();
        //set input stream to parser
        xmlPullParser.setInput(inputStream, "utf-8");
        //get event type
        int eventType = xmlPullParser.getEventType();
        //loop, until the end of file
        String tag;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    tag = xmlPullParser.getName();
                    if (tag.equalsIgnoreCase("province")){
                        xmlPullParser.nextTag();
                    }
                    if (tag.equals("county")) {
                        city = new City();
                        city.setCityname(xmlPullParser.getAttributeValue(null, "name"));
                        //Log.d("cityName:-->",city.getCityname());
                        city.setCityID(xmlPullParser.getAttributeValue(null, "weatherCode"));
                        //Log.d("citycode:-->",city.getCityID());
                        city_list.add(city);
                        city = null;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
                }
                eventType = xmlPullParser.next();
            }
        Log.d("Parse Ends:", "-------->");
        Log.d("City Number: ", Integer.toString(city_list.size()));
        for (City city1 : city_list){
            cities_map.put(city1.getCityname(), city1.getCityID());
            Log.d("map info: ", Integer.toString(cities_map.size()));
        }
    }

    /*
     *测试是否含有包含用户输入的内容
     */
    private List<City> searchCity(String cityName){
        Iterator<City> iterator = city_list.iterator();
        List<City> temp_containing = new ArrayList<City>();
        while (iterator.hasNext()){
            City temp = iterator.next();
            if(temp.getCityname().contains(cityName)){
                //Log.d("check containing: -->", "OK");
                temp_containing.add(temp);
                Log.d("Contains: ", temp.getCityname());
            }
        }
        return temp_containing;
    }
}
