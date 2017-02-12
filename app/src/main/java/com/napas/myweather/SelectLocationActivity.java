package com.napas.myweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.napas.myweather.adapter.LocationListAdapter;
import com.napas.myweather.model.Location;
import com.napas.myweather.model.Locations;
import com.napas.myweather.network.RetrofitFactory;
import com.napas.myweather.util.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class SelectLocationActivity extends AppCompatActivity {

    @Bind(R.id.root)
    RelativeLayout root;
    @Bind(R.id.et_query)
    EditText etQuery;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    public static final int REQUEST_CODE = 123;
    public static final String KEY_LOCATION_NAME = "LOCATION_NAME";
    public static final String KEY_LOCATION = "LOCATION";
    private Subscription mSearchLocationSubscription;
    private LocationListAdapter mLocationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchLocationSubscription != null && !mSearchLocationSubscription.isUnsubscribed()) {
            mSearchLocationSubscription.unsubscribe();
        }
    }

    private void initView() {
        root.setBackgroundColor(ContextCompat.getColor(this, Utils.getBackgroundColor()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setLocationList(List<Location> locations) {
        mLocationListAdapter = new LocationListAdapter(locations);
        recyclerView.setAdapter(mLocationListAdapter);
        Observable<Location> mItemClickObservable = mLocationListAdapter.getPositionClicks();
        mItemClickObservable.subscribe(new Action1<Location>() {
            @Override
            public void call(Location location) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(KEY_LOCATION_NAME, location.getName());
                resultIntent.putExtra(KEY_LOCATION, location.getL().substring(3));
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @OnClick(R.id.btn_close)
    public void onButtonCloseClick() {
        finish();
    }

    @OnTextChanged(value = R.id.et_query, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterLocationInput(Editable query) {
        callSearchLocation(query.toString());
    }

    private void callSearchLocation(@NonNull String query) {
        showProgress(true);

        Observable<Locations> observable = RetrofitFactory.getInstance().getLocationsObservable(query);
        mSearchLocationSubscription = observable
                .map(new Func1<Locations, List<Location>>() {
                    @Override
                    public List<Location> call(Locations locations) {
                        return locations.getRESULTS();
                    }
                })
                .flatMap(new Func1<List<Location>, Observable<Location>>() {
                    @Override
                    public Observable<Location> call(List<Location> locations) {
                        return Observable.from(locations);
                    }
                })
                .filter(new Func1<Location, Boolean>() {
                    @Override
                    public Boolean call(Location location) {
                        return !location.getType().equalsIgnoreCase("country");
                    }
                })
                .toList()
                .subscribe(new Subscriber<List<Location>>() {
                    @Override
                    public void onCompleted() {
                        showProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showProgress(false);
                    }

                    @Override
                    public void onNext(List<Location> locations) {
                        setLocationList(locations);
                    }
                });
//                .subscribe(new Subscriber<Locations>() {
//
//            @Override
//            public void onCompleted() {
//                showProgress(false);
//            }
//
//            @Override
//            public void onError(Throwable e) {
////                DialogFactory.createGenericErrorDialog(getApplicationContext(), e.getMessage()).show();
////                System.out.println(e.getMessage());
//                showProgress(false);
//            }
//
//            @Override
//            public void onNext(Locations locations) {
////                for(Location loc : locations.getRESULTS()) {
////                    System.out.println(loc.getName());
////                }
//                setLocationList(locations.getRESULTS());
//            }
//        });
    }

    private void showProgress(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
