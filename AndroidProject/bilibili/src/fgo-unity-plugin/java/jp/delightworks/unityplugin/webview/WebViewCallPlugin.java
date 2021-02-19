//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jp.delightworks.unityplugin.webview;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import com.unity3d.player.UnityPlayer;

public class WebViewCallPlugin {
    private static final String TAG = "WebViewCallPlugin";
    private static final int LINE_ID = 0;
    private static final int FACEBOOK_ID = 1;
    private static final int TWITTER_ID = 2;
    private static final String[] sharePackages = new String[]{"jp.naver.line.android", "com.facebook.katana", "com.twitter.android"};
    public static String result;

    public WebViewCallPlugin() {
    }

    public static Activity GetActivity() {
        return UnityPlayer.currentActivity;
    }

    public static void LaunchActivity(String type, String address) {
        Activity activity = GetActivity();
        result = null;
        Intent i = new Intent();
        i.setAction("android.intent.action.MAIN");
        i.setFlags(268435456);
        i.setClassName(activity, type);
        i.putExtra("WebViewAddress", address);
        activity.startActivity(i);
    }

    public static void LaunchGooglePlay(String targetPackage) {
        Activity activity = GetActivity();
        String packageName = targetPackage != null ? targetPackage : activity.getPackageName();
        Log.d("WebViewCallPlugin", "LaunchGooglePlay: [" + packageName + "]");
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName));

        try {
            activity.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public static void SendSNSMessage(String kind, String message) {
        Activity activity = GetActivity();
        Intent intent;
        if (kind == "LINE") {
            if (isShareAppInstall(0)) {
                intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse("line://msg/text/" + message));
                activity.startActivity(intent);
            } else {
                ShareAppDl(0);
            }
        } else if (kind == "FACEBOOK") {
            if (isShareAppInstall(1)) {
                intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.setPackage(sharePackages[1]);
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", message);
                activity.startActivity(intent);
            } else {
                ShareAppDl(1);
            }
        } else if (kind == "TWITER") {
            if (isShareAppInstall(2)) {
                intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.setPackage(sharePackages[2]);
                intent.setType("image/png");
                intent.putExtra("android.intent.extra.TEXT", message);
                activity.startActivity(intent);
            } else {
                ShareAppDl(2);
            }
        }

    }

    protected static Boolean isShareAppInstall(int shareId) {
        Activity activity = GetActivity();

        try {
            PackageManager pm = activity.getPackageManager();
            pm.getApplicationInfo(sharePackages[shareId], 128);
            return true;
        } catch (NameNotFoundException var3) {
            return false;
        }
    }

    protected static void ShareAppDl(int shareId) {
        Activity activity = GetActivity();
        Uri uri = Uri.parse("market://details?id=" + sharePackages[shareId]);
        Intent intent = new Intent("android.intent.action.VIEW", uri);

        try {
            activity.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public static void MessageDialog(Activity activity, String title, String message) {
        Log.d("WebViewCallPlugin", "MessageDialog: [" + message + "]");
        (new WebViewDialogTask(activity, title, message)).execute(new String[]{"Test"});
        Log.d("WebViewCallPlugin", "MessageDialog: create dialog task");
    }
}
