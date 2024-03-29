package com.lodenou.go4lunchv4.ui;

import com.google.android.gms.maps.model.LatLng;
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

    public static LatLng stringToLatLng(String location){
        String[] parts = location.split(",");
        double lat = Double.parseDouble(parts[0]);
        double lng = Double.parseDouble(parts[1]);
        return new LatLng(lat, lng);
    }
}
