package com.example.weatherforecast.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.weatherforecast.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Api.RetrofitClient;
import Until.Country;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends BaseActivity {
    private GoogleApiClient gac;
    TextView tvCity, tvCountry, tvTemp, tvStatus, tvSweat, tvWindy, tvCloud, tvDay;
    EditText edtSearch;
    Button btnNextDay;
    final String units = "metric";//convert °K to °C
    ImageView imgWeather;
    Dialog requestGpsDialog;
    int PERMISSION_ID = 1;
    boolean isReceivedData = false;
    ProgressDialog progress;
    FusedLocationProviderClient mFusedLocationClient;
    // use this flag to prevent load data many time
    // When user navigates to activity again, the method onStart will be invoked
    // The method onStart will connect google api client by (gac.connect()).
    // When device connects to gac, it will invoke method onConnected which invoke method get data by location
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mapping();
        Event();
        checkPlayServices();
    }

    public void showRequestGpsDialog() {
        if (requestGpsDialog == null) {
            requestGpsDialog = new Dialog(this);
            requestGpsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            requestGpsDialog.setContentView(R.layout.layout_location_no_found);
            requestGpsDialog.setCanceledOnTouchOutside(false);
            TextView tvGotIt = requestGpsDialog.findViewById(R.id.tvGotIt);
            tvGotIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            requestGpsDialog.show();
            Window window = requestGpsDialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

        }
        else{
            requestGpsDialog.show();
        }
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        MobileAds.initialize(this, "ca-app-pub-4022182242256653~4953343004");
//        AdView mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
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
                    getDataByCityName(name);
                    closeKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    public void getDataByLocation(double longg, double lat) {
        RetrofitClient
                .getInstance()
                .getApi()
                .getCurrWeatherByLocaltion(lat, longg, units, getString(R.string.ApiKey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseBody res) {
                        try {
                            String s = res.string();
                            updateUI(s);
                            isReceivedData = true;
                            progress.cancel();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.cancel();
                        Toast.makeText(MainActivity.this, getString(R.string.fail_to_load_data), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void getDataByCityName(String name) {
        progressDialog();
        RetrofitClient
                .getInstance()
                .getApi()
                .getCurrWeatherByCityName(name, units, getString(R.string.ApiKey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseBody res) {
                        try {
                            String s = res.string();
                            updateUI(s);
                            progress.cancel();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.cancel();
                        Toast.makeText(MainActivity.this, getString(R.string.city_not_found), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void updateUI(String s) throws JSONException {
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
        double wind = objectWind.getDouble("speed") * 3.6;
        tvWindy.setText((int) Math.ceil(wind) + " km/h");
    }

    private void checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, 1000).show();
            } else {
                Toast.makeText(this, "Can't get location on your device", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void getLocation() {
        if (isEnableGPS()){
            if (requestGpsDialog!=null) requestGpsDialog.cancel();
            if (hasPermissions(this,PERMISSIONS)){
                progressDialog();
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    getDataByLocation(location.getLongitude(), location.getLatitude());
                                } else {
                                    requestNewLocationData();
                                }
                            }
                        });
            }
            else{
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ID);
            }
        }
        else{
            showRequestGpsDialog();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            getDataByLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude());

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void closeKeyboard() {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    @Override
    protected void onStart() {
        if (!isReceivedData) {
            getLocation();
        }
        super.onStart();
    }

    private boolean isEnableGPS() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void progressDialog() {
        progress = new ProgressDialog(this);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Loading");
        progress.show();
    }

}

