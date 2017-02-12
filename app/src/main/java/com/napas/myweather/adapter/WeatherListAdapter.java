package com.napas.myweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.napas.myweather.R;
import com.napas.myweather.model.DbObjWeather;
import com.napas.myweather.util.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.ViewHolder> {

    private List<DbObjWeather> mWeathers;
    private final PublishSubject<Integer> onClickSubject = PublishSubject.create();

    public WeatherListAdapter(List<DbObjWeather> weathers) {
        mWeathers = weathers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final DbObjWeather weather = mWeathers.get(position);
        if (weather == null) return;

        holder.iv_condition.setImageResource(Utils.getForecastIcon(weather.getConditionCode()));
        holder.tv_detail.setText(weather.getLocationName() + "\n" + weather.getConditionName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubject.onNext(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWeathers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_condition)
        ImageView iv_condition;
        @Bind(R.id.tv_detail)
        TextView tv_detail;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public Observable<Integer> getPositionClicks() {
        return onClickSubject.asObservable();
    }
}
