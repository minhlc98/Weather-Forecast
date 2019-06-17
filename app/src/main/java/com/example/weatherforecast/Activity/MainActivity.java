package com.example.weatherforecast.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.weatherforecast.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Api.RetrofitClient;
import Until.Country;
import Until.Language;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView tvCity, tvCountry, tvTemp, tvStatus,tvSweat, tvWindy, tvCloud, tvDay;
    EditText edtSearch;
    Button btnNextDay;
    final String appid="a2f46662e9a34a86b328ddf41e420b64";
    final String units="metric";//convert °K to °C
    ImageView imgWeather;
    public final String defaultCity="Thanh Pho Ho Chi Minh";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Language.setLocale(this,"en");
        Mapping();
        Event();
        getData(defaultCity);
    }
    public void Mapping(){
        tvCity=(TextView) findViewById(R.id.tvCity);
        tvCountry=(TextView) findViewById(R.id.tvCountry);
        tvTemp=(TextView) findViewById(R.id.tvTemp);
        tvStatus=(TextView) findViewById(R.id.tvStatus);
        tvSweat=(TextView) findViewById(R.id.tvSweat);
        tvWindy=(TextView) findViewById(R.id.tvWindy);
        tvCloud=(TextView) findViewById(R.id.tvCloud);
        tvDay=(TextView) findViewById(R.id.tvUpdate);
        edtSearch=(EditText) findViewById(R.id.edtSearch);
        btnNextDay=(Button) findViewById(R.id.btnNextDay);
        imgWeather=(ImageView) findViewById(R.id.imgWeather);

    }
    public void Event(){
        btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ListNextDaysActivity.class);
                intent.putExtra("cityName",tvCity.getText().toString().substring(6));
                startActivity(intent);
            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String name=edtSearch.getText().toString().trim();
                    getData(name);
                    closeKeyboard();
                    return true;
                }
                return false;
            }
        });
    }
    public void getData(String name){
        RetrofitClient.getInstance().getApi().getCurrWeather(name,units,appid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String s=response.body().string();
                    JSONObject object=new JSONObject(s);
                    String day=object.getString("dt");
                    String name=object.getString("name");
                    tvCity.setText("City: "+name);
                    long Day=Long.valueOf(day);
                    Date date=new Date(Day*1000);
                    SimpleDateFormat format=new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
                    tvDay.setText(format.format(date));
                    JSONArray jsonArray=object.getJSONArray("weather");
                    JSONObject objectWeather=jsonArray.getJSONObject(0);
                    tvStatus.setText(objectWeather.getString("main"));
                    String url="http://openweathermap.org/img/w/"+objectWeather.getString("icon")+".png";
                    Glide.with(MainActivity.this).load(url).into(imgWeather);
                    JSONObject objectSys=object.getJSONObject("sys");
                    tvCountry.setText("Country: "+ Country.getInstance().getCountryName(objectSys.getString("country")));
                    JSONObject objectMain=object.getJSONObject("main");
                    tvSweat.setText(String.valueOf(objectMain.getInt("humidity"))+"%");
                    double tempo=objectMain.getDouble("temp");
                    tvTemp.setText(Math.round(tempo)+"°C");
                    JSONObject objectClouds=object.getJSONObject("clouds");
                    tvCloud.setText(objectClouds.getString("all")+"%");
                    JSONObject objectWind=object.getJSONObject("wind");
                    tvWindy.setText(objectWind.getString("speed")+"m/s");

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception ex){
                    Toast.makeText(MainActivity.this, "Cannot find this city name", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void closeKeyboard(){
        View v=this.getCurrentFocus();
        if (v!=null){
            InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        }
    }
}

