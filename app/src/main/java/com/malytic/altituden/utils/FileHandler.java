package com.malytic.altituden.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.malytic.altituden.models.pojo.ElevationPoint;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class FileHandler {

    private static final String TAG = FileHandler.class.getSimpleName();

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void savePathElevation(List<ElevationPoint> elevation, Context context) {

        File file = new File(new File("/sdcard/com.malytic.altituden"),"testcache");
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput("testcache", context.MODE_PRIVATE);
            outputStream.write(elevation.toString().getBytes());
            outputStream.write("\n".getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Output stream fail");
        }
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Checks if external storage is available to at least read
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
