package com.bilibili.stella;

import org.json.JSONObject;

import com.bsgamesdk.android.uo.BSGameSdkError;
import com.bsgamesdk.android.uo.callback.CallbackListener;
import com.bsgamesdk.android.uo.callback.ExiterListener;
import com.bsgamesdk.android.uo.imp.GameSdkProxy;
import com.bsgamesdk.android.uo.model.OrderInfo;
import com.bsgamesdk.android.uo.model.UserExtData;
import com.bsgamesdk.android.uo.utils.LogUtils;
import com.unity3d.player.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

public class UnityPlayerNativeActivity extends GameSdkCallback {

    private OrderInfo orderInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.gameSdkProxy = GameSdkProxy.sharedInstance();
        this.gameSdkProxy.setUserListener(this);
        UoInit();
        this.gameSdkProxy.onCreate(UnityPlayer.currentActivity, savedInstanceState);
    }

    public void UoInit() {
        status = true;
        this.gameSdkProxy.init(UnityPlayer.currentActivity, new CallbackListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                try {
                    JSONObject dat = new JSONObject();
                    String cid = Integer.toString(UnityPlayerNativeActivity.this.gameSdkProxy.channelid());
                    dat.put("channelId", cid);
                    JSONObject jobj = new JSONObject();
                    unity3dSendMessage("Init", StatusCode_Success, dat.toString());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(BSGameSdkError error) {
                UoInit();
            }
        });
    }

    private UserExtData.Role getRole(String id, String name, int lv, String Createtime) {
        UserExtData.Role role = null;
        if (role == null) {
            role = new UserExtData.Role(Long.parseLong(id), name, lv);
            role.roleCreateTime = Createtime;
        }
        return role;
    }

    private UserExtData.Server getServer(String serverid, String Servername) {
        UserExtData.Server server = null;
        if (server == null) {
            server = new UserExtData.Server(serverid, Servername, "1", "UO一区");
        }
        return server;
    }

    public void UoChooseServer(String id, String name, int lv, String serverid, String servername, String createTime) {
        String cid = Integer.toString(gameSdkProxy.channelid());
        if (cid.equalsIgnoreCase("1"))
            createTime = createTime + "000";
        UserExtData data = new UserExtData(UserExtData.Type.EnterServer, getRole(id, name, lv, createTime), getServer(serverid, servername), "vip0", 100);
        this.gameSdkProxy.setUserExtData(UnityPlayer.currentActivity, data);
    }

    public void UoCreateRole(String id, String name, int lv, String serverid, String servername, String createTime) {
        String cid = Integer.toString(gameSdkProxy.channelid());
        if (cid.equalsIgnoreCase("1"))
            createTime = createTime + "000";
        UserExtData data = new UserExtData(UserExtData.Type.CreateRole, getRole(id, name, 1, createTime), getServer(serverid, servername), "vip0", 100);
        this.gameSdkProxy.setUserExtData(UnityPlayer.currentActivity, data);
    }

    public void UoLevelUp(String id, String name, int lv, String serverid, String servername, String createTime, String levelUpTime) {
        String cid = Integer.toString(gameSdkProxy.channelid());
        if (cid.equalsIgnoreCase("1"))
            createTime = createTime + "000";
        UserExtData.Role role = null;
        role = getRole(id, name, lv, createTime);
        role.roleLevelupTime = levelUpTime;
        UserExtData data = new UserExtData(UserExtData.Type.LevelUp, role, getServer(serverid, servername), "vip0", 100);
        this.gameSdkProxy.setUserExtData(this, data);
    }

    public void UoPay(int money, String prodectName, int productCount, String tradeNo, String subject, String extInfo, String notify_url, String order_sign) {
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
                gameSdkProxy.pay(UnityPlayer.currentActivity, orderInfo, new CallbackListener() {
                    public void onSuccess(Bundle arg0) {
                        String payOutTradeNo = arg0.getString("out_trade_no");
                        String payBsTradeNo = arg0.getString("bs_out_trade_no");
                        try {
                            JSONObject dat = new JSONObject();
                            dat.put("out_trade_no", payOutTradeNo);
                            dat.put("bs_trade_no", payBsTradeNo);
                            JSONObject jobj = new JSONObject();
                            unity3dSendMessage("Pay", StatusCode_Success, dat.toString());
                            LogUtils.d("###", "UoPay success ");
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailed(BSGameSdkError arg0) {
                        LogUtils.d("###", "UoPay fail");
                        try {
                            JSONObject dat = new JSONObject();
                            dat.put("out_trade_no", "");
                            dat.put("message", arg0.getErrorCode() + arg0.getErrorMessage());
                            unity3dSendMessage("Pay", StatusCode_Fail, dat.toString());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
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
                        UnityPlayerNativeActivity.this.gameSdkProxy.destroy(UnityPlayerNativeActivity.this);
                        UnityPlayerNativeActivity.this.finish();
                    }
                })
                .setCancelable(false).show();
    }

    public void UoExit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameSdkProxy.exit(UnityPlayer.currentActivity, new ExiterListener() {
                    public void onNo3rdExiterProvide() {//第三方sdk不提供退出界面，需要研发弹出确认框，不能直接退出
                        showSelectDialog();
                    }

                    public void onExit() {//直接退出操作，用户点确认退出时的回调
                        UnityPlayerNativeActivity.this.gameSdkProxy.destroy(UnityPlayerNativeActivity.this);
                        UnityPlayerNativeActivity.this.finish();
                    }
                });
            }
        });
    }

    public boolean CheckIsLogin() {
        if (userInfo != null) {
            SendLoginMessage(10010);
            return true;
        } else {
            return false;
        }
    }

    public void YYBlogin(final int type) {
        status = false;
        if (!CheckIsLogin()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("###", "YYBlogin!!!!!");
                    gameSdkProxy.login(UnityPlayer.currentActivity, type);
                }
            });
        }
    }

    public void UoLogin() {
        status = false;
        if (!CheckIsLogin()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("###", "UoLogin!!!!!");
                    gameSdkProxy.login(UnityPlayer.currentActivity);
                }
            });
        }
    }

    public void UoLogout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameSdkProxy.logout(UnityPlayer.currentActivity);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.gameSdkProxy.onRestart(UnityPlayer.currentActivity);
    }

    // Quit Unity
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.gameSdkProxy.onDestroy(UnityPlayer.currentActivity);
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        this.gameSdkProxy.onPause(UnityPlayer.currentActivity);
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        this.gameSdkProxy.onResume(UnityPlayer.currentActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.gameSdkProxy.onStart(UnityPlayer.currentActivity);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.gameSdkProxy.onStop(UnityPlayer.currentActivity);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.gameSdkProxy.onNewIntent(UnityPlayer.currentActivity, intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.gameSdkProxy.onSaveInstanceState(UnityPlayer.currentActivity, outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.gameSdkProxy.onActivityResult(UnityPlayer.currentActivity, requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.gameSdkProxy.onConfigurationChanged(UnityPlayer.currentActivity, newConfig);
    }

    @Override
    public int getSdkType() {
        return 3;
    }
}
