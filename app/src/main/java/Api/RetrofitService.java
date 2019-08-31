package Api;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("weather")
    Single<ResponseBody> getCurrWeather(@Query("q") String name,
                                        @Query("units") String units,
                                        @Query("appid") String appid);

    @GET("forecast")
    Single<ResponseBody> getNextDaysWeather(@Query("q") String name,
                                            @Query("units") String units,
                                            @Query("appid") String appid);
}
