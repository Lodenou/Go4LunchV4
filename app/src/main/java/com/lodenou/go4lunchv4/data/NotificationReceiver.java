package com.lodenou.go4lunchv4.data;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.lodenou.go4lunchv4.R;


import java.util.ArrayList;

import java.util.stream.Collectors;


public class NotificationReceiver extends BroadcastReceiver {

    private final String CHANNEL_ID = "122";


    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences("1234", Context.MODE_PRIVATE);
        boolean aBoolean = sharedPref.getBoolean("123", true);
        if (aBoolean) {

            Log.d("123", "onReceive: ");
            String restaurantName = intent.getExtras().getString("restaurantName");
            String restaurantAddress = intent.getExtras().getString("restaurantAddress");
            ArrayList<String> colleagues = intent.getExtras().getStringArrayList("colleagues");

            int notificationId = 55;
            if (colleagues.size() >= 1) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.bol_logo)
                        .setContentTitle("Go4Lunch")
                        .setContentText("You are going to eat at " + restaurantName + " " + restaurantAddress + " with "
                                + colleagues.stream().map(i -> i.toString()).collect(Collectors.joining(", ")))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("You are going to eat at " + restaurantName + " " + restaurantAddress + " with "
                                        + colleagues.stream().map(i -> i.toString()).collect(Collectors.joining(", "))))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(notificationId, builder.build());
            } else {

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.bol_logo)
                        .setContentTitle("Go4Lunch")
                        .setContentText("You are going to eat at " + restaurantName + " " + restaurantAddress + " alone")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("You are going to eat at " + restaurantName + " " + restaurantAddress + " alone"))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(notificationId, builder.build());
            }
            // When we already post notification, delete it to avoid repetitions
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
    }
}
