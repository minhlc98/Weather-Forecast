package Api;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class RetrofitClient {
    public final String host="http://api.openweathermap.org/data/2.5/";
    public static RetrofitClient mInstance;
    public static Retrofit retrofit;
    public RetrofitClient(){
        OkHttpClient okHttpClient=new OkHttpClient.Builder().
                connectTimeout(60,TimeUnit.SECONDS).
                readTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(60,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        retrofit=new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(host)
                .build();
    }
    public static synchronized RetrofitClient getInstance(){
        if (mInstance==null){
            mInstance=new RetrofitClient();
        }
        return mInstance;
    }
    public RetrofitService getApi(){
        return retrofit.create(RetrofitService.class);
    }
}
