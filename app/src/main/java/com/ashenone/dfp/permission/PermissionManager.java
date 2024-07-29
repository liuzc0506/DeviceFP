package com.ashenone.dfp.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

public class PermissionManager {

    public static boolean hasPermission(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
                || PermissionChecker.checkSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }


    public static void checkPermission(Activity activity , String[] permission){
        String[] noPermission = new String[permission.length];
        int j = 0 ;
        for (int i = 0; i < permission.length; i++) {
            if (activity.checkSelfPermission(permission[i]) == PackageManager.PERMISSION_DENIED){
                if (permission[i] != null){
                    requestPermisson(new String[]{permission[i]} ,  activity );
                }
            }
        }
    }
    private static void requestPermisson(String[] s , Activity activity) {
        ActivityCompat.requestPermissions(activity , s  , 100);
    }

}
