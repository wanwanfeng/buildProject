package com.unity3d.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;

import static android.content.ContentValues.TAG;

public class UnityPlayerNativeActivity extends UnityPlayerActivity
{
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //Android打包部分机型问题，游戏界面在锁屏界面之上的解决方案 https://blog.csdn.net/chenfujun818/article/details/79165172
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        // 防止屏幕息屏 https://www.jb51.net/article/175373.htm
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//    }

    @SuppressLint("LongLogTag")
    public void ExitGame() {
        Log.d("UnityPlayerNativeActivity","ExitGame");
        this.finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static String GetInternalStoragePath(Activity pAvtivity) {
        String path = "";
        try {
            path = UnityPlayer.currentActivity.getApplicationContext().getFilesDir().getAbsolutePath();
        } catch (Error error) {
            Log.i("GetLocalPath ::::", " Error");
        }
        return path;
    }


    public static boolean checkDeviceRoot() {
        boolean bool = false;
        try {
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
                //Log.d("Root", "is not root~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                bool = false;
            } else {
                //Log.d("Root", "is root~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                bool = true;
            }
            Log.d(TAG, "bool = " + bool);
        } catch (Exception e) {
        }
        return bool;
    }

    public static boolean checkSimulator() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = UnityPlayer.currentActivity.registerReceiver(null, intentFilter);
        int voltage = batteryStatusIntent.getIntExtra("voltage", 99999);
        int temperature = batteryStatusIntent.getIntExtra("temperature", 99999);
        if (((voltage == 0) && (temperature == 0)) || ((voltage == 10000) && (temperature == 0))) {
            //这是通过电池的伏数和温度来判断是真机还是模拟器
            //Log.d("Root", "is simulator~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            return true;
        } else {
            //Log.d("Root", "is not simulator~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            return false;
        }
    }

    //unity调用手机剪切板
    public static void copyTextToClipboard(Activity activity, String str) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        try {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Activity.CLIPBOARD_SERVICE);
            ClipData textCd = ClipData.newPlainText("data", str);
            clipboard.setPrimaryClip(textCd);
        } catch (Error error) {
            com.activeandroid.util.Log.i("activity.getSystemService ::::", " Error");
        }
    }

    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) UnityPlayer.currentActivity.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        return deviceId;
    }
}
