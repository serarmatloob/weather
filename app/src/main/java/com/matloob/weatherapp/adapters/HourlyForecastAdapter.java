package com.matloob.weatherapp.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.models.CurrentWeatherModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Serar Matloob on 9/28/2019.
 * <p>
 * This class is a custom adapter to manage the hourly forecast list
 */
public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ForecastViewHolder> {
    // CurrentWeatherModel list instance
    private List<CurrentWeatherModel> weatherList;

    /**
     * Constructor for adapter
     *
     * @param weatherList a list contains CurrentWeatherModel objects
     */
    HourlyForecastAdapter(List<CurrentWeatherModel> weatherList) {
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        // Get object at current position
        CurrentWeatherModel currentWeatherModel = weatherList.get(position);
        // Display temperature
        holder.temp.setText(Application.getInstance().getString(R.string.temperature, (int) currentWeatherModel.getMain().getTemp()));
        // Display time
        holder.time.setText(getTime(currentWeatherModel.getDt()));
        // Load the icon using Glide
        loadIconWithGlide(holder.icon, currentWeatherModel.getWeather()[0].getIcon());
    }

    /**
     * Helper function to get readable time
     *
     * @param time {@link Long} unix time
     * @return a {@link String} time in hh:mm format
     */
    private String getTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        return DateFormat.format("hh:mma", cal).toString();
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    /**
     * Our view holder class
     */
    class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView temp;
        ImageView icon;
        TextView time;

        ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            temp = itemView.findViewById(R.id.temp);
            icon = itemView.findViewById(R.id.icon_item);
            time = itemView.findViewById(R.id.time_textView);
        }
    }

    /**
     * Helper function to load icon into image view
     *
     * @param imageView {@link ImageView}
     * @param icon      {@link String} code
     */
    private void loadIconWithGlide(ImageView imageView, String icon) {
        Glide.with(Application.getInstance()).load(Application.getInstance().getString(R.string.icon_endpoint, icon)).into(imageView);
    }
}
