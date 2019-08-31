package Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient mInstance;
    private static Retrofit retrofit;

    private RetrofitClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().
                connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        String host = "http://api.openweathermap.org/data/2.5/";
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public RetrofitService getApi() {
        return retrofit.create(RetrofitService.class);
    }
}
