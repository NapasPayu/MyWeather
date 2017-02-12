package com.napas.myweather.network;

import com.napas.myweather.model.Forecast;
import com.napas.myweather.model.Locations;
import com.napas.myweather.model.Weather;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface IWebServices {

    @GET("http://autocomplete.wunderground.com/aq")
    Observable<Locations> searchLocation(@Query("query") String query);

    @GET("hourly/q/{location}.json")
    Observable<Weather> getHourlyWeather(@Path("location") String location);

    @GET("forecast10day/q/{location}.json")
    Observable<Forecast> getForecast(@Path("location") String location);
}
