//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jp.delightworks.unityplugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.StatFs;
import android.util.Log;
import com.unity3d.player.UnityPlayer;

@SuppressLint({"NewApi"})
public class CommonServicePlugin {
    private static final String TAG = "CommonServicePlugin";

    public CommonServicePlugin() {
    }

    public static Activity GetActivity() {
        return UnityPlayer.currentActivity;
    }

    public static void OnDestory() {
        Log.d("CommonServicePlugin", "Destroying helper.");
    }

    public static boolean OnActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    public static boolean AlertDialog(final String message) {
        Log.d("CommonServicePlugin", "ConfirmDialog: " + message);

        try {
            GetActivity().runOnUiThread(new Runnable() {
                public void run() {
                    (new AlertDialogTask(CommonServicePlugin.GetActivity(), message)).execute(new String[]{"Test"});
                    Log.i("CommonServicePlugin", "create task");
                }
            });
            return true;
        } catch (Exception var2) {
            Log.i("CommonServicePlugin", var2.getMessage());
            return false;
        }
    }

    public static boolean ConfirmDialog(final String message) {
        Log.d("CommonServicePlugin", "ConfirmDialog: " + message);

        try {
            GetActivity().runOnUiThread(new Runnable() {
                public void run() {
                    (new AlertDialogTask(CommonServicePlugin.GetActivity(), message)).execute(new String[]{"Test"});
                    Log.i("CommonServicePlugin", "create task");
                }
            });
            return true;
        } catch (Exception var2) {
            Log.i("CommonServicePlugin", var2.getMessage());
            return false;
        }
    }

    public static boolean SetClipBoardText(final String message) {
        Log.d("CommonServicePlugin", "SetClipBoardText: " + message);

        try {
            GetActivity().runOnUiThread(new Runnable() {
                public void run() {
                    (new ClipBoardTask(CommonServicePlugin.GetActivity(), message)).execute(new String[]{"Test"});
                    Log.i("CommonServicePlugin", "create task");
                }
            });
            return true;
        } catch (Exception var2) {
            Log.i("CommonServicePlugin", var2.getMessage());
            return false;
        }
    }

    public static long GetFreeSize(String path) {
        if (path != null) {
            StatFs statFs = new StatFs(path);
            if (statFs != null) {
                long availableBlocks = (long)statFs.getAvailableBlocks();
                long blockSize = (long)statFs.getBlockSize();
                long freeBytes = availableBlocks * blockSize;
                return freeBytes;
            }
        }

        return 0L;
    }
}
