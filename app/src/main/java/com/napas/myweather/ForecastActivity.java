package com.napas.myweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.napas.myweather.adapter.ForecastListAdapter;
import com.napas.myweather.model.Forecast;
import com.napas.myweather.model.Forecastday_;
import com.napas.myweather.network.RetrofitFactory;
import com.napas.myweather.util.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

public class ForecastActivity extends AppCompatActivity {

    @Bind(R.id.root)
    RelativeLayout root;
    @Bind(R.id.tv_location)
    TextView tvLocation;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    public static String KEY_LOCATION = "LOCATION";
    public static String KEY_LOCATION_NAME = "LOCATION_NAME";
    private ForecastListAdapter mForecastListAdapter;
    private Subscription mForecastSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forcast);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(KEY_LOCATION) || !intent.hasExtra(KEY_LOCATION_NAME)) {
            return;
        }
        initView(intent.getStringExtra(KEY_LOCATION_NAME));
        callGetForecast(intent.getStringExtra(KEY_LOCATION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mForecastSubscription != null && !mForecastSubscription.isUnsubscribed()) {
            mForecastSubscription.unsubscribe();
        }
    }

    @OnClick(R.id.btn_close)
    public void onButtonCloseClick() {
        finish();
    }

    private void initView(@NonNull String locationName) {
        root.setBackgroundColor(ContextCompat.getColor(this, Utils.getBackgroundColor()));
        tvLocation.setText(getString(R.string.ten_day_forecast) + "\n" + locationName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setForecastList(List<Forecastday_> forecasts) {
        mForecastListAdapter = new ForecastListAdapter(forecasts);
        recyclerView.setAdapter(mForecastListAdapter);
    }

    private void callGetForecast(@NonNull String location) {
        showProgress(true);

        Observable<Forecast> observable = RetrofitFactory.getInstance().getForecast(location);
        mForecastSubscription = observable
                .map(new Func1<Forecast, List<Forecastday_>>() {
                    @Override
                    public List<Forecastday_> call(Forecast forecast) {
                        return forecast.getForecast().getSimpleforecast().getForecastday();
                    }
                }).subscribe(new Subscriber<List<Forecastday_>>() {
                    @Override
                    public void onCompleted() {
                        showProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showProgress(false);
                    }

                    @Override
                    public void onNext(List<Forecastday_> forecasts) {
                        setForecastList(forecasts);
                    }
                });
    }

    private void showProgress(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
