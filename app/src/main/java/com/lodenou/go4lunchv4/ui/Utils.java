package com.lodenou.go4lunchv4.ui;

import com.lodenou.go4lunchv4.model.nearbysearch.OpeningHours;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        return dfTime.format(date);
    }

    public static String isOpenOrNot(OpeningHours openingHours){
        if (openingHours != null) {
            if (openingHours.getOpenNow()) {
                return ("Open");
            } else {
                return ("Closed");
            }
        } else {
            return ("No schedule specified");
        }
    }

    public static String formatLocation(Double lat ,Double lng){
        return lat.toString() + "," + lng.toString();
    }
}
