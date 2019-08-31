package com.example.weatherforecast.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.weatherforecast.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Api.RetrofitClient;
import Until.Country;
import Until.Language;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    TextView tvCity, tvCountry, tvTemp, tvStatus, tvSweat, tvWindy, tvCloud, tvDay;
    EditText edtSearch;
    Button btnNextDay;
    final String appid = "a2f46662e9a34a86b328ddf41e420b64";
    final String units = "metric";//convert °K to °C
    ImageView imgWeather;
    public final String defaultCity = "Thanh Pho Ho Chi Minh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Language.setLocale(this, "en");
        Mapping();
        Event();
        getData(defaultCity);
    }

    public void Mapping() {
        tvCity = findViewById(R.id.tvCity);
        tvCountry = findViewById(R.id.tvCountry);
        tvTemp = findViewById(R.id.tvTemp);
        tvStatus = findViewById(R.id.tvStatus);
        tvSweat = findViewById(R.id.tvSweat);
        tvWindy = findViewById(R.id.tvWindy);
        tvCloud = findViewById(R.id.tvCloud);
        tvDay = findViewById(R.id.tvUpdate);
        edtSearch = findViewById(R.id.edtSearch);
        btnNextDay = findViewById(R.id.btnNextDay);
        imgWeather = findViewById(R.id.imgWeather);
        MobileAds.initialize(this, "ca-app-pub-4022182242256653~4953343004");
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void Event() {
        btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListNextDaysActivity.class);
                intent.putExtra("cityName", tvCity.getText().toString().substring(6));
                startActivity(intent);
            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String name = edtSearch.getText().toString().trim();
                    getData(name);
                    closeKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    public void getData(String name) {
        RetrofitClient
                .getInstance()
                .getApi()
                .getCurrWeather(name, units, appid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseBody res) {
                        try {
                            String s  = res.string();
                            JSONObject object = new JSONObject(s);
                            String day = object.getString("dt");
                            String name = object.getString("name");
                            tvCity.setText("City: " + name);
                            long Day = Long.valueOf(day);
                            Date date = new Date(Day * 1000);
                            SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
                            tvDay.setText(format.format(date));
                            JSONArray jsonArray = object.getJSONArray("weather");
                            JSONObject objectWeather = jsonArray.getJSONObject(0);
                            tvStatus.setText(objectWeather.getString("main"));
                            String url = "http://openweathermap.org/img/w/" + objectWeather.getString("icon") + ".png";
                            Glide.with(MainActivity.this).load(url).into(imgWeather);
                            JSONObject objectSys = object.getJSONObject("sys");
                            tvCountry.setText("Country: " + Country.getInstance().getCountryName(objectSys.getString("country")));
                            JSONObject objectMain = object.getJSONObject("main");
                            tvSweat.setText((objectMain.getInt("humidity")) + "%");
                            double tempo = objectMain.getDouble("temp");
                            tvTemp.setText(Math.round(tempo) + "°C");
                            JSONObject objectClouds = object.getJSONObject("clouds");
                            tvCloud.setText(objectClouds.getString("all") + "%");
                            JSONObject objectWind = object.getJSONObject("wind");
                            tvWindy.setText(objectWind.getString("speed") + "m/s");
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Main10",e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void closeKeyboard() {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}

