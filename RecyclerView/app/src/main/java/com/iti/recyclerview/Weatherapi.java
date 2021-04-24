package com.iti.recyclerview;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface Weatherapi {
    @GET("weather")
    Call<InitializtionMain> getWeather(@Query("lon") double longitude, @Query("lat") double latitude, @Query("appid") String apikey);
}
