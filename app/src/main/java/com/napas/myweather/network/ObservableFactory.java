package com.napas.myweather.network;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ObservableFactory<T> {

    public static <T> Observable<T> getObservable(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(RetrofitFactory.HTTP_TIME_OUT, TimeUnit.SECONDS);
    }
}
