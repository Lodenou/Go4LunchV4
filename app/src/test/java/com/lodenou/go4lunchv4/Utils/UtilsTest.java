package com.lodenou.go4lunchv4.Utils;
import org.junit.Test;
import static org.junit.Assert.*;

import com.google.android.gms.maps.model.LatLng;
import com.lodenou.go4lunchv4.model.nearbysearch.OpeningHours;
import com.lodenou.go4lunchv4.ui.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsTest {
    @Test
    public void testConvertDateToHour() {
        String expectedFormat = "HH:mm";
        Date date = new Date();
        String result = Utils.convertDateToHour(date);
        SimpleDateFormat df = new SimpleDateFormat(expectedFormat);
        String expected = df.format(date);
        assertEquals(expected, result);
    }

    @Test
    public void testIsOpenOrNot_Open() {
        // Arrange
        OpeningHours openingHours = new OpeningHours();

        openingHours.setOpenNow(true);
        // Act
        String result = Utils.isOpenOrNot(openingHours);

        // Assert
        assertEquals("Open", result);
    }

    @Test
    public void testIsOpenOrNot_Closed() {
        // Arrange
        OpeningHours openingHours = new OpeningHours();

        openingHours.setOpenNow(false);

        // Act
        String result = Utils.isOpenOrNot(openingHours);

        // Assert
        assertEquals("Closed", result);
    }

    @Test
    public void testIsOpenOrNot_Null() {
        // Act
        String result = Utils.isOpenOrNot(null);

        // Assert
        assertEquals("No schedule specified", result);
    }

    @Test
    public void testFormatLocation() {
        // Arrange
        Double lat = 40.712776;
        Double lng = -74.005974;
        String expected = "40.712776,-74.005974";

        // Act
        String result = Utils.formatLocation(lat, lng);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void testStringToLatLng() {
        // Arrange
        String location = "40.712776,-74.005974";
        LatLng expected = new LatLng(40.712776, -74.005974);

        // Act
        LatLng result = Utils.stringToLatLng(location);

        // Assert
        assertEquals(expected.latitude, result.latitude, 0.000001);
        assertEquals(expected.longitude, result.longitude, 0.000001);
    }
}
