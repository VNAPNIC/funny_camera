package top.icecream.testme.main.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * AUTHOR: 86417
 * DATE: 5/9/2017
 */

public class PermissionHelper {

    private final static int PERMISSIONCODE = 100;
    public static boolean checkPermissions(Context context){
        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    public static void requestPermissions(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONCODE);
    }

    public static boolean checkRequest(int requestCode,int[] grantResults){
        boolean result = false;
        result = requestCode == PERMISSIONCODE
                && grantResults.length == 3
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED;
        return result;
    }

}
