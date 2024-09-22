package com.devm22.happyguide.helper;

import android.content.Context;
import android.util.Log;

import com.devm22.happyguide.R;
import com.devm22.happyguide.utils.Utils;
import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

public class NotificationService implements OneSignal.OSRemoteNotificationReceivedHandler {
    private static final String TAG = "MyOSRemoteNotificationR";


    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent notificationReceivedEvent) {
        OSNotification notification = notificationReceivedEvent.getNotification();
        DatabaseHelperNotifications databaseHelperNotifications = new DatabaseHelperNotifications(context);

        Log.e(TAG, "all: " + notification.getAndroidNotificationId() + "  " + notification.getTitle() + "  " + notification.getBody() + "  " + notification.getLargeIcon() + "  " +
                notification.getLargeIcon() + "  " + notification.getBigPicture() + "  " + notification.getLaunchURL());

        if (!databaseHelperNotifications.CheckIsDataAlreadyInDBorNot(notification.getAndroidNotificationId())){
            databaseHelperNotifications.addNotifications(notification.getAndroidNotificationId(), notification.getTitle(), notification.getBody(), notification.getSmallIcon(),
                    notification.getLargeIcon(), notification.getBigPicture(), notification.getLaunchURL(), Utils.dateToString(Utils.getDate(0)), 0);
        }




        // Example of modifying the notification's accent color
        OSMutableNotification mutableNotification = notification.mutableCopy();
        mutableNotification.setExtender(builder -> builder.setColor(context.getResources().getColor(R.color.colorPrimary)));

        // If complete isn't call within a time period of 25 seconds, OneSignal internal logic will show the original notification
        // To omit displaying a notifiation, pass `null` to complete()
        notificationReceivedEvent.complete(mutableNotification);
    }

}
