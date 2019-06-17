package Api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("weather")
    Call<ResponseBody> getCurrWeather(@Query("q") String name,
                                      @Query("units") String units,
                                      @Query("appid") String appid);

    @GET("forecast")
    Call<ResponseBody> getNextDaysWeather(@Query("q") String name,
                                      @Query("units") String units,
                                      @Query("appid") String appid) ;
}
