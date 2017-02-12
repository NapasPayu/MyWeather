package com.napas.myweather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.napas.myweather.R;
import com.napas.myweather.model.Date;
import com.napas.myweather.model.Forecastday_;
import com.napas.myweather.model.High;
import com.napas.myweather.model.Low;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.ViewHolder> {

    private List<Forecastday_> mForecasts;

    public ForecastListAdapter(List<Forecastday_> forecasts) {
        mForecasts = forecasts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Forecastday_ forecast = mForecasts.get(position);
        if (forecast == null) return;

        Date date = forecast.getDate();
        High high = forecast.getHigh();
        Low low = forecast.getLow();
        holder.tv_date.setText(date.getWeekdayShort() + " " + date.getMonth() + "/" + date.getDay());
        holder.tv_condition.setText(forecast.getConditions());
        holder.tv_temp_high.setText(high.getCelsius() + "°C / " + high.getFahrenheit() + "°F");
        holder.tv_temp_low.setText(low.getCelsius() + "°C / " + low.getFahrenheit() + "°F");
    }

    @Override
    public int getItemCount() {
        return mForecasts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_date)
        TextView tv_date;
        @Bind(R.id.tv_condition)
        TextView tv_condition;
        @Bind(R.id.tv_temp_high)
        TextView tv_temp_high;
        @Bind(R.id.tv_temp_low)
        TextView tv_temp_low;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
