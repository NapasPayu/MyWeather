package com.napas.myweather;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.napas.myweather.adapter.WeatherListAdapter;
import com.napas.myweather.model.DbObjWeather;
import com.napas.myweather.model.HourlyForecast;
import com.napas.myweather.model.Temp;
import com.napas.myweather.model.Weather;
import com.napas.myweather.network.RetrofitFactory;
import com.napas.myweather.util.DialogFactory;
import com.napas.myweather.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

import static com.napas.myweather.SelectLocationActivity.KEY_LOCATION;
import static com.napas.myweather.SelectLocationActivity.KEY_LOCATION_NAME;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.iv_condition)
    ImageView ivCondition;
    @Bind(R.id.tv_detail)
    TextView tvDetail;
    @Bind(R.id.root)
    RelativeLayout root;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.ll_main_weather)
    LinearLayout llMainWeather;

    private static long PERIODIC_TASK_INTERVAL = 3000000; // millisecond
    private WeatherListAdapter mWeatherListAdapter;
    private Subscription mGetWeatherAllLocations;
    private List<DbObjWeather> mWeathers;
    private ProgressDialog mProgress;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        mHandler.post(periodicTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGetWeatherAllLocations != null && !mGetWeatherAllLocations.isUnsubscribed()) {
            mGetWeatherAllLocations.unsubscribe();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (SelectLocationActivity.REQUEST_CODE): {
                if (resultCode == RESULT_OK) {
                    String locationName = data.getStringExtra(KEY_LOCATION_NAME);
                    String location = data.getStringExtra(KEY_LOCATION);
                    callGetWeatherOfLocation(getWeatherObservable(locationName, location));
//                    callGetWeatherOfLocation(locationName, location);
                }
                break;
            }
        }
    }

    private void initView() {
        root.setBackgroundColor(ContextCompat.getColor(this, Utils.getBackgroundColor()));
        mProgress = DialogFactory.createProgressDialog(this, getString(R.string.loading));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        try {
            mWeathers = DbObjWeather.listAll(DbObjWeather.class);
            if (mWeathers.size() == 0) {
                startSelectLocationActivity();
            } else {
                updateView();
            }
        } catch (Exception e) {  // table not found
            startSelectLocationActivity();
        }
    }

    private void updateView() {
        int size = mWeathers.size();
        if (size == 0) {
            llMainWeather.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else if (size == 1) {
            setMainLocation();
            recyclerView.setVisibility(View.GONE);
        } else {
            setMainLocation();
            setLocationList();
        }
    }

    private Runnable periodicTask = new Runnable() {
        @Override
        public void run() {
            if (Utils.isNetworkConnected(getApplicationContext())) {
                callGetWeatherAllLocations();
            }
            mHandler.postDelayed(periodicTask, PERIODIC_TASK_INTERVAL);
        }
    };

    private void setMainLocation() {
        if (mWeathers == null) return;
        DbObjWeather location = mWeathers.get(0);
        ivCondition.setImageResource(Utils.getForecastIcon(location.getConditionCode()));
        String locationName = location.getLocationName();
        String conditionName = location.getConditionName();
        String temp = location.getTempCelsius() + "°C" + " / " + location.getTempFahrenheit() + "°F";
        tvDetail.setText(locationName + "\n" + conditionName + "\n" + temp);
        llMainWeather.setVisibility(View.VISIBLE);
    }

    private void setLocationList() {
        mWeatherListAdapter = new WeatherListAdapter(mWeathers.subList(1, mWeathers.size()));
        recyclerView.setAdapter(mWeatherListAdapter);
        recyclerView.setVisibility(View.VISIBLE);

        Observable<Integer> mItemClickObservable = mWeatherListAdapter.getPositionClicks();
        mItemClickObservable.subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer position) {
                if (mWeathers == null) return;
                Collections.swap(mWeathers, 0, position + 1);
                updateView();
            }
        });
    }

    @OnClick(R.id.btn_add)
    public void onButtonAddClick() {
        startSelectLocationActivity();
    }

    @OnClick(R.id.btn_forecast)
    public void onButtonForecastClick() {
        if (mWeathers == null) return;
        DbObjWeather weather = mWeathers.get(0);
        Intent intent = new Intent(this, ForecastActivity.class);
        intent.putExtra(ForecastActivity.KEY_LOCATION_NAME, weather.getLocationName());
        intent.putExtra(ForecastActivity.KEY_LOCATION, weather.getLocation());
        startActivity(intent);
    }

    private void startSelectLocationActivity() {
        startActivityForResult(new Intent(this, SelectLocationActivity.class), SelectLocationActivity.REQUEST_CODE);
    }

    @OnLongClick(R.id.ll_main_weather)
    public boolean onImageLongClick() {
        if (mWeathers != null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                    .setTitle(getString(R.string.dialog_title_remove))
                    .setMessage(mWeathers.get(0).getLocationName())
                    .setNegativeButton(getString(R.string.dialog_action_cancel), null)
                    .setPositiveButton(getString(R.string.dialog_action_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeItem();
                            updateView();
                        }
                    });
            alertDialog.show();
        }
        return true;
    }

    private void addItem(DbObjWeather item) {
        item.save();
        if (mWeathers == null) {
            mWeathers = new ArrayList<>();
        }
        mWeathers.add(item);
    }

    private void removeItem() {
        if (mWeathers == null) return;
        DbObjWeather item = DbObjWeather.findById(DbObjWeather.class, mWeathers.get(0).getId());
        item.delete();
        mWeathers.remove(0);
    }

    private void callGetWeatherAllLocations() {

        if (mWeathers == null) return;

        showProgress(true);

        List<Observable<DbObjWeather>> weatherObservables = new ArrayList<>();
        for (DbObjWeather weather : mWeathers) {
            weatherObservables.add(getWeatherObservable(weather.getLocationName(), weather.getLocation()));
        }

        mWeathers = new ArrayList<>();
        DbObjWeather.deleteAll(DbObjWeather.class);
        updateView();

        mGetWeatherAllLocations = Observable
                .merge(weatherObservables)
                .subscribe(new Subscriber<DbObjWeather>() {

                    @Override
                    public void onCompleted() {
                        showProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showProgress(false);
                    }

                    @Override
                    public void onNext(DbObjWeather dbObjWeather) {
                        updateView();
                        showProgress(false);
                    }
                });
    }

    private Observable<DbObjWeather> getWeatherObservable(@NonNull final String locationName, @NonNull final String location) {

        Observable<DbObjWeather> observable = RetrofitFactory.getInstance().getHourlyWeather(location)
                .map(new Func1<Weather, DbObjWeather>() {
                    @Override
                    public DbObjWeather call(Weather weather) {
                        HourlyForecast hourlyForecast = weather.getHourlyForecast().get(0);

                        DbObjWeather dbObjWeather = new DbObjWeather();
                        dbObjWeather.setLocationName(locationName);
                        dbObjWeather.setLocation(location);
                        dbObjWeather.setConditionCode(hourlyForecast.getFctcode());
                        dbObjWeather.setConditionName(hourlyForecast.getWx());
                        Temp temp = hourlyForecast.getTemp();
                        dbObjWeather.setTempCelsius(temp.getMetric());
                        dbObjWeather.setTempFahrenheit(temp.getEnglish());

                        addItem(dbObjWeather);
                        return dbObjWeather;
                    }
                });
        return observable;
    }

    private void callGetWeatherOfLocation(Observable<DbObjWeather> observable) {
        showProgress(true);

        observable.subscribe(new Subscriber<DbObjWeather>() {
            @Override
            public void onCompleted() {
                showProgress(false);
            }

            @Override
            public void onError(Throwable e) {
                showProgress(false);
            }

            @Override
            public void onNext(DbObjWeather dbObjWeather) {
                updateView();
            }
        });
    }

    private void showProgress(boolean show) {
        if (mProgress == null) return;
        if (show) {
            mProgress.show();
        } else {
            mProgress.dismiss();
        }
    }
}
