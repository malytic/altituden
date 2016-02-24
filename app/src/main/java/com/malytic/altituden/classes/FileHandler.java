package com.malytic.altituden.classes;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by William on 2016-02-19.
 */
public class FileHandler {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void savePathElevation(List<ElevationPoint> elevation, Context context) {
/*
        File file = new File(new File("/sdcard/com.malytic.altituden"),"testcache");
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput("testcache", Context.MODE_PRIVATE);
            outputStream.write(elevation.toString().getBytes());
            outputStream.write("\n".getBytes());
            outputStream.close();
        } catch (Exception e) {
            System.out.println("Outputstream fail.");
        }
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }*/
    }
}
