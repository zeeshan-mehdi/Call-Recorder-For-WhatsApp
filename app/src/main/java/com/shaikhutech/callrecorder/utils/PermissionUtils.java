package com.shaikhutech.callrecorder.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/**
 * Created by Ashiq on 6/1/16.
 */
public class PermissionUtils {

    public static final int REQUEST_WRITE_STORAGE_DOWNLOAD = 112;
    public static final int REQUEST_WRITE_STORAGE_UPLOAD = 113;

    // permission to write sd card
    public static String[] SD_WRITE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int REQUEST_CALL = 114;

    // permission to make a phone call
    public static String[] CALL_PERMISSIONS = {
            Manifest.permission.CALL_PHONE
    };

    public static boolean isPermissionGranted(Activity activity, String[] permissions, int requestCode) {
        boolean requirePermission = false;
        if(permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                if ((ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)) {
                    requirePermission = true;
                    break;
                }
            }
        }

        if (requirePermission) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPermissionResultGranted(int[] grantResults) {
        boolean allGranted = true;
        if(grantResults != null && grantResults.length > 0) {
            for (int i : grantResults) {
                if(i != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
        }
        return allGranted;
    }

}
