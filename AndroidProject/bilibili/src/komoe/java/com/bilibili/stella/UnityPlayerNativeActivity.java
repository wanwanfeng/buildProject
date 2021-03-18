package com.bilibili.stella;

import com.gs.android.GameSDK;
import com.gs.android.base.utils.LogUtils;
import com.unity3d.player.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class UnityPlayerNativeActivity extends GameSdkCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void init(final boolean debug, String merchant_id, String app_id, String server_id, String app_key) {
        status = true;
        sharedInstance = new BaseData();
        sharedInstance.merchant_id = merchant_id;
        sharedInstance.app_id = app_id;
        sharedInstance.server_id = server_id;
        sharedInstance.app_key = app_key;
        sharedInstance.debug = debug;
        GameSDK.getInstance().init(UnityPlayer.currentActivity, merchant_id, app_id, server_id, app_key, this);
    }

    public void notifyZone(String role_id, String role_name, int lv, String serverid, String server_name, String createTime) {
        GameSDK.getInstance().notifyZone(server_name, role_id, role_name, serverid, server_name);
    }

    public void createRole(String role_id, String role_name, int lv, String serverid, String servername, String createTime) {
        GameSDK.getInstance().createRole(role_id, role_name, serverid, servername);
    }

    public void levelUp(String id, String name, int lv, String serverid, String servername, String createTime, String levelUpTime) {

    }

    public void pay(int money, String prodectName, int productCount, String tradeNo, String subject, String extInfo, String notify_url, String order_sign) {
        orderInfo = new OrderInfo();
        orderInfo.moneyAmount = money;
        orderInfo.productName = prodectName;
        orderInfo.productCount = productCount;
        orderInfo.tradeNo = tradeNo;
        orderInfo.subject = subject;
        orderInfo.extInfo = extInfo;
        orderInfo.notify_url = notify_url;
        orderInfo.order_sign = order_sign;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameSDK.getInstance().pay("cp_server_id", "cp_server_name", orderInfo.tradeNo, orderInfo.productName, orderInfo.notify_url, orderInfo.extInfo);
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
                    LogUtils.d("###", "UoLogin!!!!!");
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

    public void showGameTerms() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameSDK.getInstance().showGameTerms();
            }
        });
    }

    public String getUDID() {
        return GameSDK.getInstance().getUDID();
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
            SendLoginMessage(10010);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getSdkType() {
        return 4;
    }
}
