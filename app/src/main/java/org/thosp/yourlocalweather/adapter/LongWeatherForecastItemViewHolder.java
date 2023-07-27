package org.thosp.yourlocalweather.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.thosp.yourlocalweather.R;
import org.thosp.yourlocalweather.model.DetailedWeatherForecast;
import org.thosp.yourlocalweather.model.WeatherCondition;
import org.thosp.yourlocalweather.utils.AppPreference;
import org.thosp.yourlocalweather.utils.TemperatureUtil;
import org.thosp.yourlocalweather.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class LongWeatherForecastItemViewHolder extends RecyclerView.ViewHolder {

    private final String TAG = "LongForecastViewHolder";

    private DetailedWeatherForecast mWeatherForecast;

    private Context mContext;

    private TextView mTime;
    private TextView mDate;
    private TextView mIcon;
    private TextView mTemperature;
    private TextView mApparentTemperature;
    private TextView mWind;
    private TextView windDirection;
    private TextView mHumidity;
    private TextView mPressure;
    private TextView mRainSnow;
    private TextView mDescription;

    public LongWeatherForecastItemViewHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;

        mTime = (TextView) itemView.findViewById(R.id.forecast_time);
        mDate = (TextView) itemView.findViewById(R.id.forecast_date);
        mIcon = (TextView) itemView.findViewById(R.id.forecast_icon);
        mTemperature = (TextView) itemView.findViewById(R.id.forecast_temperature);
        mApparentTemperature = (TextView) itemView.findViewById(R.id.forecast_apparent_temperature);
        mWind = (TextView) itemView.findViewById(R.id.forecast_wind);
        windDirection = (TextView) itemView.findViewById(R.id.forecast_wind_direction);
        mHumidity = (TextView) itemView.findViewById(R.id.forecast_humidity);
        mPressure = (TextView) itemView.findViewById(R.id.forecast_pressure);
        mRainSnow = (TextView) itemView.findViewById(R.id.forecast_rainsnow);
        mDescription = (TextView) itemView.findViewById(R.id.forecast_description);
    }
    public static class WeatherPrefrences{
        Context context;
        double latitude;
        Locale locale;
        DetailedWeatherForecast weather;
        String pressureUnitFromPreferences;
        String rainSnowUnitFromPreferences;
        String windUnitFromPreferences;
        String temperatureUnitFromPreferences;
        Set<Integer> visibleColumns;

        public WeatherPrefrences() {
        }

        public WeatherPrefrences(Context context, double latitude, Locale locale,
                                 DetailedWeatherForecast weather, String pressureUnitFromPreferences,
                                 String rainSnowUnitFromPreferences, String windUnitFromPreferences,
                                 String temperatureUnitFromPreferences, Set<Integer> visibleColumns) {
            this.context = context;
            this.latitude = latitude;
            this.locale = locale;
            this.weather = weather;
            this.pressureUnitFromPreferences = pressureUnitFromPreferences;
            this.rainSnowUnitFromPreferences = rainSnowUnitFromPreferences;
            this.windUnitFromPreferences = windUnitFromPreferences;
            this.temperatureUnitFromPreferences = temperatureUnitFromPreferences;
            this.visibleColumns = visibleColumns;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        public DetailedWeatherForecast getWeather() {
            return weather;
        }

        public void setWeather(DetailedWeatherForecast weather) {
            this.weather = weather;
        }

        public String getPressureUnitFromPreferences() {
            return pressureUnitFromPreferences;
        }

        public void setPressureUnitFromPreferences(String pressureUnitFromPreferences) {
            this.pressureUnitFromPreferences = pressureUnitFromPreferences;
        }

        public String getRainSnowUnitFromPreferences() {
            return rainSnowUnitFromPreferences;
        }

        public void setRainSnowUnitFromPreferences(String rainSnowUnitFromPreferences) {
            this.rainSnowUnitFromPreferences = rainSnowUnitFromPreferences;
        }

        public String getWindUnitFromPreferences() {
            return windUnitFromPreferences;
        }

        public void setWindUnitFromPreferences(String windUnitFromPreferences) {
            this.windUnitFromPreferences = windUnitFromPreferences;
        }

        public String getTemperatureUnitFromPreferences() {
            return temperatureUnitFromPreferences;
        }

        public void setTemperatureUnitFromPreferences(String temperatureUnitFromPreferences) {
            this.temperatureUnitFromPreferences = temperatureUnitFromPreferences;
        }

        public Set<Integer> getVisibleColumns() {
            return visibleColumns;
        }

        public void setVisibleColumns(Set<Integer> visibleColumns) {
            this.visibleColumns = visibleColumns;
        }
    }
    void bindWeather(WeatherPrefrences weatherPrefrences) {
        mWeatherForecast = weatherPrefrences.getWeather();

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/weathericons-regular-webfont.ttf");
        WeatherCondition weatherCondition = weatherPrefrences.getWeather().getFirstWeatherCondition();

        if (weatherPrefrences.getVisibleColumns().contains(1)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d.M", weatherPrefrences.getLocale());
            Date date = new Date(weatherPrefrences.getWeather().getDateTime() * 1000);
            Calendar currentRowDate = Calendar.getInstance();
            currentRowDate.setTime(date);
            mDate.setVisibility(View.VISIBLE);
            mDate.setTypeface(typeface);
            mDate.setText(dateFormat.format(date));
        } else {
            mDate.setVisibility(View.GONE);
        }
        if (weatherPrefrences.getVisibleColumns().contains(2)) {
            mIcon.setVisibility(View.VISIBLE);
            mIcon.setTypeface(typeface);
            if (weatherCondition != null) {
                mIcon.setText(Utils.getStrIcon(mContext, weatherCondition.getIcon()));
            }
        } else {
            mIcon.setVisibility(View.GONE);
        }
        if (weatherPrefrences.getVisibleColumns().contains(3)) {
            mDescription.setVisibility(View.VISIBLE);
            if (weatherCondition != null) {
                mDescription.setText(weatherPrefrences.getWeather().getFirstWeatherCondition().getDescription());
            }
        } else {
            mDescription.setVisibility(View.GONE);
        }
        if (weatherPrefrences.getVisibleColumns().contains(4)) {
            mTemperature.setVisibility(View.VISIBLE);
            String temperature = mContext.getString(R.string.temperature_with_degree,
                    TemperatureUtil.getForecastedTemperatureWithUnit(mContext, weatherPrefrences.getWeather(), weatherPrefrences.getTemperatureUnitFromPreferences(), weatherPrefrences.getLocale()));
            mTemperature.setText(temperature);
        } else {
            mTemperature.setVisibility(View.GONE);
        }
        if (weatherPrefrences.getVisibleColumns().contains(5)) {
            mApparentTemperature.setVisibility(View.VISIBLE);
            String apparentTemperature = mContext.getString(R.string.temperature_with_degree,
                    TemperatureUtil.getForecastedApparentTemperatureWithUnit(mContext, weatherPrefrences.getLatitude(), weatherPrefrences.getWeather(), weatherPrefrences.getTemperatureUnitFromPreferences(), weatherPrefrences.getLocale()));
            mApparentTemperature.setText(apparentTemperature);
        } else {
            mApparentTemperature.setVisibility(View.GONE);
        }
        if (weatherPrefrences.getVisibleColumns().contains(6)) {
            mWind.setVisibility(View.VISIBLE);
            mWind.setText(AppPreference.getWindInString(mContext, weatherPrefrences.getWindUnitFromPreferences(), weatherPrefrences.getWeather().getWindSpeed(), weatherPrefrences.getLocale()));
        } else {
            mWind.setVisibility(View.GONE);
        }
        if (weatherPrefrences.getVisibleColumns().contains(7)) {
            windDirection.setVisibility(View.VISIBLE);
            windDirection.setText(AppPreference.getWindDirection(mContext, weatherPrefrences.getWeather().getWindDegree(), weatherPrefrences.getLocale()));
        } else {
            windDirection.setVisibility(View.GONE);
        }
        if (weatherPrefrences.getVisibleColumns().contains(8)) {
            mRainSnow.setVisibility(View.VISIBLE);
            boolean noRain = weatherPrefrences.getWeather().getRain() < 0.1;
            boolean noSnow = weatherPrefrences.getWeather().getSnow() < 0.1;
            if (noRain && noSnow) {
                mRainSnow.setText("");
            } else {
                String rain = AppPreference.getFormatedRainOrSnow(weatherPrefrences.getRainSnowUnitFromPreferences(), weatherPrefrences.getWeather().getRain(), weatherPrefrences.getLocale());
                String snow = AppPreference.getFormatedRainOrSnow(weatherPrefrences.getRainSnowUnitFromPreferences(), weatherPrefrences.getWeather().getSnow(), weatherPrefrences.getLocale());
                if (!noRain && !noSnow) {
                    mRainSnow.setText(rain + "/" + snow);
                } else if (noSnow) {
                    mRainSnow.setText(rain);
                } else {
                    mRainSnow.setText(snow);
                }
            }
            ViewGroup.LayoutParams params=mRainSnow.getLayoutParams();
            params.width = Utils.spToPx(AppPreference.getRainOrSnowForecastWeadherWidth(weatherPrefrences.getContext()), weatherPrefrences.getContext());
            mRainSnow.setLayoutParams(params);
        } else {
            mRainSnow.setVisibility(View.GONE);
        }
        if (weatherPrefrences.getVisibleColumns().contains(9)) {
            mHumidity.setVisibility(View.VISIBLE);
            mHumidity.setText(String.format(weatherPrefrences.getLocale(), "%d", weatherPrefrences.getWeather().getHumidity()));
        } else {
            mHumidity.setVisibility(View.GONE);
        }
        if (weatherPrefrences.getVisibleColumns().contains(10)) {
            mPressure.setVisibility(View.VISIBLE);
            mPressure.setText(AppPreference.getPressureInString(mContext, weatherPrefrences.getWeather().getPressure(), weatherPrefrences.getPressureUnitFromPreferences(), weatherPrefrences.getLocale()));
        } else {
            mPressure.setVisibility(View.GONE);
        }
    }
}