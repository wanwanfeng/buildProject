package com.hello.stella;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.gs.android.GameSDK;
import com.gs.android.base.utils.LogUtils;
import com.unity3d.player.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class UnityPlayerNativeActivity extends GameSdkCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void init(String info) {
        status = true;

        String[] array = info.split(",");

        sharedInstance = new BaseData();
        sharedInstance.debug = array[0].equalsIgnoreCase("true");
        sharedInstance.merchant_id = array[1];
        sharedInstance.app_id = array[2];
        sharedInstance.server_id = array[3];
        sharedInstance.app_key = array[4];

        Log.i("init ::::", sharedInstance.toString());

        GameSDK.getInstance().init(UnityPlayer.currentActivity, sharedInstance.merchant_id, sharedInstance.app_id, sharedInstance.server_id, sharedInstance.app_key, this);
    }

    public void notifyZone(String info) {

        String[] array = info.split(",");
        final String role_id = array[0];
        final String role_name = array[1];
        final String server_name = array[2];
        final String serverid = array[3];

        GameSDK.getInstance().notifyZone(server_name, role_id, role_name, serverid, server_name);
    }

    public void createRole(String info) {
        String[] array = info.split(",");
        final String role_id = array[0];
        final String role_name = array[1];
        final String serverid = array[2];
        final String servername = array[3];

        GameSDK.getInstance().createRole(role_id, role_name, serverid, servername);
    }

    public void levelUp(String info) {
        String[] array = info.split(",");
    }

    public void pay(String info) {

        String[] array = info.split(",");
        final String cp_server_id = array[0];
        final String cp_server_name = array[1];
        final String out_trade_no = array[2];
        final String product_id = array[3];
        final String notify_url = array[4];
        final String extension_info = array[5];

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameSDK.getInstance().pay(cp_server_id, cp_server_name, out_trade_no, product_id, notify_url, extension_info);
            }
        });
    }

    public void showSelectDialog() {
        AlertDialog dialog = new AlertDialog.Builder(UnityPlayer.currentActivity)
                .setMessage("确认退出吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        UnityPlayerNativeActivity.this.finish();
                    }
                })
                .setCancelable(false).show();
    }

    public void login() {
        status = false;
        if (!checkIsLogin()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("###", "Login!!!!!");
                    GameSDK.getInstance().login();
                }
            });
        }
    }

    public void logout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameSDK.getInstance().logout();
            }
        });
    }

    public void showGameTerms(String info)  {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameSDK.getInstance().showGameTerms();
            }
        });
    }

    public void showUserAgreement(String info) {
        String[] array = info.split(",");
        final int cpServerArea = Integer.valueOf(array[0]);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameSDK.getInstance().showUserAgreement(cpServerArea);
            }
        });
    }

    public void showPrivacyPolicy(String info) {
        String[] array = info.split(",");
        final int cpServerArea = Integer.valueOf(array[0]);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameSDK.getInstance().showPrivacyPolicy(cpServerArea);
            }
        });
    }

    public void userCenter(String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameSDK.getInstance().userCenter();
            }
        });
    }

    public void switchAccount(String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameSDK.getInstance().switchAccount();
            }
        });
    }

    // Quit Unity
    @Override
    public void onDestroy() {
        super.onDestroy();
        GameSDK.getInstance().appDestroy(UnityPlayer.currentActivity);
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        GameSDK.getInstance().appOffline(UnityPlayer.currentActivity);
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        GameSDK.getInstance().appOnline(UnityPlayer.currentActivity);
    }

    public boolean checkIsLogin() {
        if (userInfo != null) {
            SendLoginMessage(StatusCode_Success);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getUDID() {
        return GameSDK.getInstance().getUDID();
    }

    @Override
    public void appsflyerTrackEvent(String eventKey, HashMap<String, Object> eventValues) {
        Log.i("###", "appsflyerTrackEvent:" + eventKey.toString());
        Log.i("###", "appsflyerTrackEvent:" + eventValues.toString());
        GameSDK.getInstance().appsflyerTrackEvent(UnityPlayer.currentActivity, eventKey, eventValues);
    }

    @Override
    public void firebaseTrackEvent(String eventKey, HashMap<String, Object> eventValues) {
        Log.i("###", "firebaseTrackEvent:" + eventKey.toString());
        Log.i("###", "firebaseTrackEvent:" + eventValues.toString());
        GameSDK.getInstance().firebaseTrackEvent(UnityPlayer.currentActivity, eventKey, eventValues);
    }
}
