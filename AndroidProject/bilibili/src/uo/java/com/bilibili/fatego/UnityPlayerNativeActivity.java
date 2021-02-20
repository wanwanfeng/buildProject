package com.bilibili.stella;

import org.json.JSONException;
import org.json.JSONObject;

import com.bsgamesdk.android.uo.BSGameSdkError;
import com.bsgamesdk.android.uo.callback.CallbackListener;
import com.bsgamesdk.android.uo.callback.ExiterListener;
import com.bsgamesdk.android.uo.callback.UserListener;
import com.bsgamesdk.android.uo.imp.GameSdkProxy;
import com.bsgamesdk.android.uo.model.OrderInfo;
import com.bsgamesdk.android.uo.model.User;
import com.bsgamesdk.android.uo.model.UserExtData;
import com.bsgamesdk.android.uo.utils.LogUtils;
import com.bsgamesdk.android.uo.utils.ToastUtil;
import com.unity3d.player.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

public class UnityPlayerNativeActivity extends com.unity3d.player.UnityPlayerNativeActivity implements UserListener {
    private GameSdkProxy gameSdkProxy = null;
    private OrderInfo orderInfo = null;
    private User userInfo = null;
    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.gameSdkProxy = GameSdkProxy.sharedInstance();
        this.gameSdkProxy.setUserListener(this);
        UoInit();
        this.gameSdkProxy.onCreate(UnityPlayer.currentActivity, savedInstanceState);
    }

    public static void unity3dSendMessage(String json) {
        UnityPlayer.UnitySendMessage("SdkManager", "OnReviceCallback", json);
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
                    jobj.put("callbackType", "Init");
                    jobj.put("code", 10010);
                    jobj.put("data", dat.toString());
                    UnityPlayerNativeActivity.unity3dSendMessage(jobj.toString());
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
                            jobj.put("callbackType", "Pay");
                            jobj.put("code", 10010);
                            jobj.put("data", dat.toString());
                            UnityPlayerNativeActivity.unity3dSendMessage(jobj.toString());
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
                            JSONObject jobj = new JSONObject();
                            jobj.put("callbackType", "Pay");
                            jobj.put("code", 200);
                            jobj.put("data", dat.toString());
                            UnityPlayerNativeActivity.unity3dSendMessage(jobj.toString());
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

    public boolean CheckIsLogin() {
        if (userInfo != null) {
            SendLoginMessage(10010);
            return true;
        } else {
            return false;
        }
    }

    public void SendLoginMessage(int code) {
        LogUtils.d("###", "public SendLoginMessage getChannelId :" + getChannelId());
        try {
            JSONObject dat = new JSONObject();
            if (userInfo.channelUName == null || userInfo.channelUName == "") {
                dat.put("username", userInfo.userID);
                dat.put("nickname", userInfo.userID);
            } else {
                dat.put("username", userInfo.channelUName);
                dat.put("nickname", userInfo.channelUName);
            }
            dat.put("uid", userInfo.userID);
            dat.put("access_token", userInfo.channelToken);
            dat.put("session_id", userInfo.sessionid);
            JSONObject jobj = new JSONObject();
            jobj.put("callbackType", "Login");
            jobj.put("code", code);
            jobj.put("data", dat.toString());
            unity3dSendMessage(jobj.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoginSuccess(User user) {
        if (status) {
            userInfo = user;
            return;
        }
        if (userInfo == null) {
            userInfo = user;
            SendLoginMessage(10010);
        } else {
            if (user.userID.equalsIgnoreCase(userInfo.userID)) {
                return;
            } else {
                userInfo = user;
                SendLoginMessage(10011);
            }
        }
    }

    @Override
    public void onLoginFailed(BSGameSdkError error) {
        try {
            JSONObject jobj = new JSONObject();
            jobj.put("callbackType", "Login");
            jobj.put("code", 10012);
            jobj.put("data", "");
            userInfo = null;
            UnityPlayerNativeActivity.unity3dSendMessage(jobj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("###", " \"public void onLoginFailed(BSGameSdkError error)");
        LogUtils.d("###", "登陆失败：" + error.getErrorMessage() + " error code:" + error.getErrorCode());
        ToastUtil.showToast(UnityPlayer.currentActivity, "登陆失败，请重试。" + "error code：" + error.getErrorCode());
    }

    @Override
    public void onLogout() {
        LogUtils.d("###", "public void onLogout() getChannelId :" + getChannelId());
        try {
            LogUtils.d("###", "public void onLogout(),uid:" + this.userInfo.userID);
            JSONObject jobj = new JSONObject();
            jobj.put("callbackType", "Logout");
            jobj.put("code", 10010);
            jobj.put("data", "");
            UnityPlayerNativeActivity.unity3dSendMessage(jobj.toString());
            userInfo = null;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public String getChannelId() {
        return Integer.toString(gameSdkProxy.channelid());
    }
}
