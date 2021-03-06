package com.example.covar.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.covar.R;

public class ReminderBroadcast extends BroadcastReceiver {

    /**
     * Method to trigger the notification for the 2nd dose. It receives the date for 2nd dose
     * from VaccineDetailsActivity on filling up of form for 1st dose.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String vaccineDate2 = intent.getStringExtra("vaccineDate2");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyAnkit")
                .setSmallIcon(R.drawable.vaccine_splash_logo)
                .setContentTitle("Vaccine 2nd dose reminder")
                .setContentText("Your 2nd dose is due on " + vaccineDate2)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(200, builder.build());
    }
}
