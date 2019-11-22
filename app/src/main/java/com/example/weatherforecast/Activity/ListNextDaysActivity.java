package com.example.weatherforecast.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ListNextDaysActivity extends BaseActivity {
    ArrayList<Weather> listWeather;
    ListView list;
    WeatherAdapter adapter;
    ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_next_days);
        Mapping();
        Intent intent = getIntent();
        String cityName = intent.getStringExtra("cityName");
        getData(cityName);
    }

    public void Mapping() {
        progressDialog();
        listWeather = new ArrayList<>();
        list = findViewById(R.id.list);
        adapter = new WeatherAdapter(listWeather, this);
        list.setAdapter(adapter);
    }

    public void getData(String cityName) {
        RetrofitClient
                .getInstance()
                .getApi()
                .getNextDaysWeather(cityName, "metric", getString(R.string.ApiKey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseBody response) {
                        try {
                            // Toast.makeText(ListNextDaysActivity.this, response.body().string(), Toast.LENGTH_SHORT).show();
                            JSONObject object = new JSONObject(response.string());
                            JSONArray list = object.getJSONArray("list");
                            for (int i = 0; i < list.length(); ++i) {
                                String date, status, temp, image;
                                long Date;
                                JSONObject weather = list.getJSONObject(i);
                                JSONObject objectMain = weather.getJSONObject("main");
                                JSONArray arrayWeather = weather.getJSONArray("weather");
                                JSONObject objectWeather = arrayWeather.getJSONObject(0);
                                Date = Long.valueOf(weather.getString("dt"));
                                Date d = new Date(Date * 1000);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
                                date = dateFormat.format(d);
                                status = objectWeather.getString("main");
                                temp = (Math.round(objectMain.getDouble("temp"))) + "°C";
                                image = "http://openweathermap.org/img/w/" + objectWeather.getString("icon") + ".png";
                                listWeather.add(new Weather(date, status, image, temp));
                            }

                            adapter.notifyDataSetChanged();
                            progress.cancel();
                        } catch (IOException | JSONException e) {
                            progress.cancel();
                            Toast.makeText(ListNextDaysActivity.this, getString(R.string.fail_to_load_data), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }
    public void progressDialog(){
        progress = new ProgressDialog(this);
        progress.setMessage("Loading");
        progress.show();
    }
}
