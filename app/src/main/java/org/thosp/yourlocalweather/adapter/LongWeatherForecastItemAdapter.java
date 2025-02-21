package org.thosp.yourlocalweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.thosp.yourlocalweather.R;
import org.thosp.yourlocalweather.model.DetailedWeatherForecast;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LongWeatherForecastItemAdapter extends RecyclerView.Adapter<LongWeatherForecastItemViewHolder> {

    private Context mContext;
    private Set<Integer> visibleColumns;
    private List<DetailedWeatherForecast> mWeatherList;
    private double latitude;
    private Locale locale;
    private String pressureUnitFromPreferences;
    private String rainSnowUnitFromPreferences;
    private String windUnitFromPreferences;
    private String temperatureUnitFromPreferences;

    public LongWeatherForecastItemAdapter(Context context,
                                          List<DetailedWeatherForecast> weather,
                                          double latitude,
                                          Locale locale,
                                          String pressureUnitFromPreferences,
                                          String rainSnowUnitFromPreferences,
                                          String windUnitFromPreferences,
                                          String temperatureUnitFromPreferences,
                                          Set<Integer> visibleColumns) {
        mContext = context;
        mWeatherList = weather;
        this.visibleColumns = visibleColumns;
        this.latitude = latitude;
        this.locale = locale;
        this.pressureUnitFromPreferences = pressureUnitFromPreferences;
        this.rainSnowUnitFromPreferences = rainSnowUnitFromPreferences;
        this.windUnitFromPreferences = windUnitFromPreferences;
        this.temperatureUnitFromPreferences = temperatureUnitFromPreferences;
    }

    @Override
    public LongWeatherForecastItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.long_forecast_item_detail, parent, false);
        return new LongWeatherForecastItemViewHolder(v, mContext);
    }

    @Override
    public void onBindViewHolder(LongWeatherForecastItemViewHolder holder, int position) {
        DetailedWeatherForecast weather = mWeatherList.get(position);
        LongWeatherForecastItemViewHolder.WeatherPreferences weatherPreferences = new LongWeatherForecastItemViewHolder.WeatherPreferences();
        weatherPreferences.setWeather(weather);
        weatherPreferences.setContext(mContext);
        weatherPreferences.setLocale(locale);
        weatherPreferences.setVisibleColumns(visibleColumns);
        weatherPreferences.setPressureUnitFromPreferences(pressureUnitFromPreferences);
        weatherPreferences.setRainSnowUnitFromPreferences(rainSnowUnitFromPreferences);
        weatherPreferences.setWindUnitFromPreferences(windUnitFromPreferences);
        weatherPreferences.setLatitude(latitude);
        weatherPreferences.setTemperatureUnitFromPreferences(temperatureUnitFromPreferences);
        holder.bindWeather(weatherPreferences);
    }

    @Override
    public int getItemCount() {
        return (mWeatherList != null ? mWeatherList.size() : 0);
    }
}
