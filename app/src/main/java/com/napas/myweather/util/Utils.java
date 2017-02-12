package com.napas.myweather.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.napas.myweather.R;

import java.util.Arrays;
import java.util.Calendar;

public class Utils {

    private static String[] SUN_FCT_CODE = {"1", "7", "8"};
    private static String[] SNOW_FCT_CODE = {"9", "18", "19", "20", "21", "22", "23", "24"};
    private static String[] CLOUDY_FCT_CODE = {"2", "3", "4"};
    private static String[] FOG_FCT_CODE = {"5", "6"};
    private static String[] RAIN_FCT_CODE = {"10", "11", "12", "13", "16", "17"};
    private static String[] THUNDERSTORM_FCT_CODE = {"14", "15"};

    public static int getForecastIcon(String fctcode) {
        int imgRes;

        if (Arrays.asList(SUN_FCT_CODE).contains(fctcode)) {
            imgRes = R.drawable.sun;
        } else if (Arrays.asList(SNOW_FCT_CODE).contains(fctcode)) {
            imgRes = R.drawable.snow;
        } else if (Arrays.asList(CLOUDY_FCT_CODE).contains(fctcode)) {
            imgRes = R.drawable.cloudy;
        } else if (Arrays.asList(FOG_FCT_CODE).contains(fctcode)) {
            imgRes = R.drawable.fog;
        } else if (Arrays.asList(RAIN_FCT_CODE).contains(fctcode)) {
            imgRes = R.drawable.rain;
        } else if (Arrays.asList(THUNDERSTORM_FCT_CODE).contains(fctcode)) {
            imgRes = R.drawable.thunderstorm;
        } else {
            imgRes = R.drawable.sun;
        }
        return imgRes;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static int getBackgroundColor() {
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentHour >= 7 && currentHour <= 19) { // 7am to 7pm
            return R.color.colorPrimary;
        } else {
            return R.color.dark_blue;
        }
    }
}
