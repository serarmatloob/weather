package com.matloob.weatherapp.tasks;

import android.os.AsyncTask;

import com.matloob.weatherapp.models.CurrentWeatherModel;
import com.matloob.weatherapp.models.ForecastWeatherModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This AsyncTask populate the forecast array and divide it into 5 arrays each 8 of (3 hour forecasts)
 * Then save it to new array to be used with the recycler
 */
public class CreateForecastArrays extends AsyncTask<String, Void, List<ForecastWeatherModel>> {
    private ForecastWeatherModel forecastWeatherModel;
    private ArrayCallback callback;

    public CreateForecastArrays(final ForecastWeatherModel forecastWeatherModel, ArrayCallback callback) {
        this.forecastWeatherModel = forecastWeatherModel;
        this.callback = callback;
    }

    @Override
    protected List<ForecastWeatherModel> doInBackground(String... params) {
        List<CurrentWeatherModel> day1 = new ArrayList<>();
        List<CurrentWeatherModel> day2 = new ArrayList<>();
        List<CurrentWeatherModel> day3 = new ArrayList<>();
        List<CurrentWeatherModel> day4 = new ArrayList<>();
        List<CurrentWeatherModel> day5 = new ArrayList<>();

        List<ForecastWeatherModel> daysForecastList = new ArrayList<>();

        try {
            int counter = 0;
            for (int i = 0; i < forecastWeatherModel.getList().size(); i++) {
                if (counter < 8) {
                    day1.add(forecastWeatherModel.getList().get(i));
                    counter++;
                    continue;
                }
                if (counter < 16) {
                    day2.add(forecastWeatherModel.getList().get(i));
                    counter++;
                    continue;
                }
                if (counter < 24) {
                    day3.add(forecastWeatherModel.getList().get(i));
                    counter++;
                    continue;
                }
                if (counter < 32) {
                    day4.add(forecastWeatherModel.getList().get(i));
                    counter++;
                    continue;
                }
                if (counter < 40) {
                    day5.add(forecastWeatherModel.getList().get(i));
                    counter++;
                }
            }
            ForecastWeatherModel forecastWeatherModel1 = new ForecastWeatherModel();
            forecastWeatherModel1.setList(day1);
            daysForecastList.add(forecastWeatherModel1);
            ForecastWeatherModel forecastWeatherModel2 = new ForecastWeatherModel();
            forecastWeatherModel2.setList(day2);
            daysForecastList.add(forecastWeatherModel2);
            ForecastWeatherModel forecastWeatherModel3 = new ForecastWeatherModel();
            forecastWeatherModel3.setList(day3);
            daysForecastList.add(forecastWeatherModel3);
            ForecastWeatherModel forecastWeatherModel4 = new ForecastWeatherModel();
            forecastWeatherModel4.setList(day4);
            daysForecastList.add(forecastWeatherModel4);
            ForecastWeatherModel forecastWeatherModel5 = new ForecastWeatherModel();
            forecastWeatherModel5.setList(day5);
            daysForecastList.add(forecastWeatherModel5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return daysForecastList;
    }

    @Override
    protected void onPostExecute(List<ForecastWeatherModel> result) {
        callback.onArrayReady(result);
    }

    /**
     * Interface to return task results
     */
    public interface ArrayCallback {
        /**
         * Called when resulted array is ready
         * @param result Array of 5 forecast arrays.
         */
        void onArrayReady(List<ForecastWeatherModel> result);
    }

}