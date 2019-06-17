package com.example.weatherforecast.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weatherforecast.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Adapter.WeatherAdapter;
import Api.RetrofitClient;
import Model.Weather;
import Until.Language;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListNextDaysActivity extends AppCompatActivity {
    ArrayList<Weather> listWeather;
    ListView list;
    WeatherAdapter adapter;
    final String api="a2f46662e9a34a86b328ddf41e420b64";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list_next_days);
        Language.setLocale(this,"en");
        Mapping();
        Intent intent=getIntent();
        String cityName=intent.getStringExtra("cityName");
        getData(cityName);
    }
    public void Mapping(){
        listWeather=new ArrayList<>();
        list=(ListView) findViewById(R.id.list);
        adapter=new WeatherAdapter(listWeather,getApplicationContext());
        list.setAdapter(adapter);
    }
    public void getData(String cityName){
        RetrofitClient.getInstance().getApi().getNextDaysWeather(cityName,"metric",api).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    // Toast.makeText(ListNextDaysActivity.this, response.body().string(), Toast.LENGTH_SHORT).show();
                    JSONObject object = new JSONObject(response.body().string());
                    JSONArray list = object.getJSONArray("list");
                    for (int i = 0; i < list.length(); ++i) {
                        String date, status, temp, image;
                        long Date;
                        JSONObject weather = list.getJSONObject(i);
                        JSONObject objectMain = weather.getJSONObject("main");
                        JSONArray arrayWeather =weather.getJSONArray("weather");
                        JSONObject objectWeather=arrayWeather.getJSONObject(0);
                        Date=Long.valueOf(weather.getString("dt"));
                        Date d=new Date(Date*1000);
                        SimpleDateFormat dateFormat=new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
                        date=dateFormat.format(d);
                        status = objectWeather.getString("main");
                        temp = String.valueOf(Math.round(objectMain.getDouble("temp")))+"°C";
                        image = "http://openweathermap.org/img/w/" + objectWeather.getString("icon") + ".png";
                        listWeather.add(new Weather(date, status, image,temp));
                    }

                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    Toast.makeText(ListNextDaysActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(ListNextDaysActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }


                @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ListNextDaysActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
