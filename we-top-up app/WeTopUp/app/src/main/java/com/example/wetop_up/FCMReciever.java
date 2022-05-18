package com.example.wetop_up;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.wetop_up.home_activities.HomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMReciever extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().isEmpty())
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        else
            showNotification(remoteMessage.getData());
    }

    private void showNotification(Map<String, String> data) {

        Intent resultIntent = new Intent(this, SplashScreen.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = data.get("title");
        String body  = data.get("body");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String NOTIF_CHANNEL_ID = "com.example.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notifChannel = new NotificationChannel(NOTIF_CHANNEL_ID,"Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notifChannel.setDescription("New Channel");
            notifChannel.enableLights(true);
            notifChannel.setLightColor(Color.BLUE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notifChannel);
            }
        }

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID);

        notifBuilder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.spider_web)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        notifBuilder.setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(0, notifBuilder.build());
        }
    }

    private void showNotification(String title, String body) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String NOTIF_CHANNEL_ID = "com.example.test";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notifChannel = new NotificationChannel(NOTIF_CHANNEL_ID,"Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notifChannel.setDescription("New Channel");
            notifChannel.enableLights(true);
            notifChannel.setLightColor(Color.BLUE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notifChannel);
            }
        }

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID);

        notifBuilder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.spider_web)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);



        if (notificationManager != null) {
            notificationManager.notify(0, notifBuilder.build());
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
