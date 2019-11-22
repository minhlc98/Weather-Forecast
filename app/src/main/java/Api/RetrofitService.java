package Api;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("weather")
    Single<ResponseBody> getCurrWeatherByCityName(@Query("q") String name,
                                                  @Query("units") String units,
                                                  @Query("appid") String appid);

    @GET("weather")
    Single<ResponseBody> getCurrWeatherByLocaltion(@Query("lat") double lat,
                                                   @Query("lon") double longg,
                                                   @Query("units") String units,
                                                   @Query("appid") String appid);

    @GET("forecast")
    Single<ResponseBody> getNextDaysWeather(@Query("q") String name,
                                            @Query("units") String units,
                                            @Query("appid") String appid);
}
