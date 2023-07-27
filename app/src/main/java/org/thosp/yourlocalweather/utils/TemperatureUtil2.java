package org.thosp.yourlocalweather.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import org.thosp.yourlocalweather.model.DetailedWeatherForecast;
import org.thosp.yourlocalweather.model.Weather;

public class TemperatureUtil2 {
    static TemperatureUtil temperatureUtil = new TemperatureUtil();
    public TemperatureUtil2() {
    }

    public static float getApparentTemperature(double dryBulbTemperature,
                                               int humidity,
                                               double windSpeed,
                                               int cloudiness,
                                               double latitude,
                                               long timestamp) {
        return TemperatureUtil.getApparentTemperatureWithSolarIrradiation(dryBulbTemperature, humidity, windSpeed, cloudiness, latitude, timestamp);
    }

    public static float getApparentTemperatureWithoutSolarIrradiation(double dryBulbTemperature, int humidity, double windSpeed) {
        double e = (humidity / 100f) * 6.105 * Math.exp((17.27*dryBulbTemperature) / (237.7 + dryBulbTemperature));
        double apparentTemperature = dryBulbTemperature + (0.33*e)-(0.70*windSpeed)-4.00;
        return (float)apparentTemperature;
    }
    public static double getTemperature(Context context, String unitsFromPreferences, DetailedWeatherForecast weather) {
        if (weather == null) {
            return 0;
        }
        return TemperatureUtil.getTemperatureInPreferredUnit(context, unitsFromPreferences, TemperatureUtil.getTemperatureInCelsius(context, weather));
    }

    public static double getTemperature(Context context, String temperatureUnitFromPreferences, Weather weather) {
        if (weather == null) {
            return 0;
        }
        return TemperatureUtil.getTemperatureInPreferredUnit(temperatureUnitFromPreferences, TemperatureUtil.getTemperatureInCelsius(context, weather));
    }

}