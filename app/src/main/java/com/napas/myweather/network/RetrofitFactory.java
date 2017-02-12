package com.napas.myweather.network;

import com.napas.myweather.model.Forecast;
import com.napas.myweather.model.Locations;
import com.napas.myweather.model.Weather;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class RetrofitFactory {

    public static final int HTTP_TIME_OUT = 20;
    private static RetrofitFactory instance;
    private IWebServices mWebServices;

    /**
     * Returns the instance of this singleton.
     */
    public static RetrofitFactory getInstance() {
        if (instance == null) {
            instance = new RetrofitFactory();
        }
        return instance;
    }

    /**
     * Private singleton constructor.
     */
    private RetrofitFactory() {
        Retrofit retrofit = buildRestAdapter();
        this.mWebServices = retrofit.create(IWebServices.class);
    }

    private static OkHttpClient getHttpClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS);
        return okHttpClient.build();
    }

    /**
     * Creates the RestAdapter by setting custom HttpClient.
     */
    private Retrofit buildRestAdapter() {
        return new Retrofit.Builder()
                .baseUrl("http://api.wunderground.com/api/6dfc48884f6168e4/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }

    public Observable<Locations> getLocationsObservable(String query) {
        return ObservableFactory.getObservable(mWebServices.searchLocation(query));
    }

    public Observable<Weather> getHourlyWeather(String location) {
        return ObservableFactory.getObservable(mWebServices.getHourlyWeather(location));
    }

    public Observable<Forecast> getForecast(String location) {
        return ObservableFactory.getObservable(mWebServices.getForecast(location));
    }

}
