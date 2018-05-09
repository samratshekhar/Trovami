package com.trovami.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by samrat on 27/01/18.
 */

public class Utils {

    public static boolean isServiceRunning(Activity activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String formatDateTime(String date) {
        SimpleDateFormat reader=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dateStr = reader.parse(date);
            SimpleDateFormat writer = new SimpleDateFormat("E dd, h:m:s a");
            return writer.format(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void safeToast(Context context, String toastMsg) {
        if (context != null) {
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean checkIfLocationPermissionGranted(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkIfCallPermissionGranted(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}
