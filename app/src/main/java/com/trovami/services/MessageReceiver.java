package com.trovami.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trovami.R;
import com.trovami.activities.DashboardActivity;
import com.trovami.activities.MainActivity;

/**
 * Created by samrat on 26/04/18.
 */

public class MessageReceiver extends FirebaseMessagingService {
    private static final String TAG = "MessageReceiver";
    private static final int REQUEST_CODE = 1;
    private static final int NOTIFICATION_ID = 6578;

    public MessageReceiver() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        final String title = remoteMessage.getData().get("title");
        final String message = remoteMessage.getData().get("body");
        final String uid = remoteMessage.getData().get("uid");
        showNotifications(title, message, uid);
    }

    private void showNotifications(String title, String msg, String uid) {
        Intent i = new Intent(this, DashboardActivity.class);
        i.putExtra("uid", uid);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher_round);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentText(msg)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_track)
                .setLargeIcon(icon)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }
}
