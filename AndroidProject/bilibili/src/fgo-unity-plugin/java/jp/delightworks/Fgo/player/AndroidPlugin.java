//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jp.delightworks.Fgo.player;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.ValueCallback;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerNativeActivity;

public class AndroidPlugin extends UnityPlayerNativeActivity {
    public static ValueCallback<Uri> _uploadMessages;
    public static ValueCallback<Uri[]> _uploadCallback;
    protected static boolean isBoot = false;
    protected static Activity bootActivity = null;
    public static final int FLAG_LOCKED = 0x00080000;

    public AndroidPlugin() {
    }

    public static Activity getActivity() {
        return UnityPlayer.currentActivity;
    }

    public static void setUploadMessage(ValueCallback<Uri> message) {
        _uploadMessages = message;
    }

    public static void setUploadCallback(ValueCallback<Uri[]> message) {
        _uploadCallback = message;
    }

    public static boolean getIsBoot() {
        return isBoot;
    }

    public static Activity getBootActivity() {
        return bootActivity;
    }

    protected void onCreate(Bundle savedInstanceState) {
        bootActivity = this;
        Log.i("AndroidPlugin", "onCreate Fgo Main Activirty");
        super.onCreate(savedInstanceState);

        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~FLAG_LOCKED;
    }

    public void onDestroy() {
        Log.i("AndroidPlugin", "onDestroy");
        super.onDestroy();
    }

    protected void onPause() {
        Log.i("AndroidPlugin", "onPause");
        super.onPause();
    }

    protected void onResume() {
        Log.i("AndroidPlugin", "onResume");
        super.onResume();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("UniWebView", "Rotation: " + newConfig.orientation);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }
}
