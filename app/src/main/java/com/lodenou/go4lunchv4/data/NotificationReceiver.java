package com.lodenou.go4lunchv4.data;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.lodenou.go4lunchv4.R;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * BroadcastReceiver for handling notifications.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private final String CHANNEL_ID = "122";


    /**
     * This method is called when the BroadcastReceiver receives a notification intent.
     *
     * @param context The application context in which the receiver is running.
     * @param intent  The Intent containing information about the notification.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences("1234", Context.MODE_PRIVATE);
        boolean aBoolean = sharedPref.getBoolean("123", true);
        if (aBoolean) {

            String restaurantName = intent.getExtras().getString("restaurantName");
            String restaurantAddress = intent.getExtras().getString("restaurantAddress");
            ArrayList<String> colleagues = intent.getExtras().getStringArrayList("colleagues");

            int notificationId = 55;
            if (colleagues.size() >= 1) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.bol_logo)
                        .setContentTitle("Go4Lunch")
                        .setContentText(context.getString(R.string.You_are_going_to_eat)+ restaurantName + " " + restaurantAddress +
                                context.getString(R.string.with)
                                + colleagues.stream().map(i -> i.toString()).collect(Collectors.joining(", ")))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(context.getString(R.string.You_are_going_to_eat)+ restaurantName + " " + restaurantAddress +
                                        context.getString(R.string.with)
                                        + colleagues.stream().map(i -> i.toString()).collect(Collectors.joining(", "))))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(notificationId, builder.build());
            } else {

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.bol_logo)
                        .setContentTitle("Go4Lunch")
                        .setContentText(context.getString(R.string.You_are_going_to_eat) + restaurantName + " " + restaurantAddress +
                                context.getString(R.string.alone))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(context.getString(R.string.You_are_going_to_eat) + restaurantName + " " + restaurantAddress +
                                        context.getString(R.string.alone)))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(notificationId, builder.build());
            }
            // When we already post notification, delete it to avoid repetitions
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        }
    }
}
