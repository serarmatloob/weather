package com.matloob.weatherapp.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.models.ForecastWeatherModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Serar Matloob on 9/28/2019.
 *
 * This class is a custom adapter to manage the daily forecast list
 */
public class DaysForecastAdapter extends RecyclerView.Adapter<DaysForecastAdapter.ViewHolder> {
    // Forecast weather model list instance
    private List<ForecastWeatherModel> weatherList;

    /**
     * Constructor for adapter
     *
     * @param weatherList a list contains ForecastWeatherModel objects
     */
    public DaysForecastAdapter(List<ForecastWeatherModel> weatherList) {
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get object at current position
        ForecastWeatherModel forecastWeatherModel = weatherList.get(position);
        // Set the date for the 3rd hourly reading
        holder.date.setText(getDate(forecastWeatherModel.getList().get(position + 2).getDt()));
        // Linear manager for our recycler
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Application.getInstance(), LinearLayoutManager.HORIZONTAL, false);
        // Set layout manager to recycler
        holder.forecastListRecycler.setLayoutManager(linearLayoutManager);
        // Our custom adapter fill with the hourly list (8 items)
        HourlyForecastAdapter adapter = new HourlyForecastAdapter(forecastWeatherModel.getList());
        // Set adapter to recycler
        holder.forecastListRecycler.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    /**
     * Our view holder class
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        RecyclerView forecastListRecycler;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_textView);
            forecastListRecycler = itemView.findViewById(R.id.forecast_recycler);
        }
    }

    /**
     * Helper function to get readable date
     *
     * @param time in unix format
     * @return {@link String} of date
     */
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        return DateFormat.format("MM-dd-yyyy", cal).toString();
    }
}
