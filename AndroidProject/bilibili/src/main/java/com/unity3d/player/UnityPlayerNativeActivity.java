package com.unity3d.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class UnityPlayerNativeActivity extends com.sega.sgn.sgnfw.common.unityactivity.SgnfwUnityActivity {
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //Android打包部分机型问题，游戏界面在锁屏界面之上的解决方案 https://blog.csdn.net/chenfujun818/article/details/79165172
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        // 防止屏幕息屏 https://www.jb51.net/article/175373.htm
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//    }

    @SuppressLint("LongLogTag")
    public void ExitGame() {
        Log.d("UnityPlayerNativeActivity", "ExitGame");
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

    public int getSdkType() {
        return 0;
    }

    public int getLanguageType() {
        return 3;
    }
}
