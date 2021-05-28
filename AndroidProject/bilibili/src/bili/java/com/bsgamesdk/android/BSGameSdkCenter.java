package com.bsgamesdk.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.telephony.*;

import com.bsgamesdk.android.callbacklistener.AccountCallBackListener;
import com.bsgamesdk.android.callbacklistener.ExitCallbackListener;
import com.bsgamesdk.android.callbacklistener.BSGameSdkError;
import com.bsgamesdk.android.callbacklistener.InitCallbackListener;
import com.bsgamesdk.android.callbacklistener.OrderCallbackListener;
import com.bsgamesdk.android.utils.LogUtils;
import com.unity3d.player.UnityPlayer;

import java.io.File;
//剪切板所用
import android.content.ClipboardManager;
import android.content.ClipData;

import static android.content.ContentValues.TAG;

public class BSGameSdkCenter {
    public static String temp = "temp_sdfsdfsdf";
    public static final int OK = 1;
    private static BSGameSdkCenter sharedInstance = null;
    private BSGameSdk gameSdk;
    private String merchant_id;
    private String app_id;
    private String server_id;
    private String app_key;
    private String user_id;
    private boolean debug = true;
    // 用于存储用户信息
    private SharedPreferences preferences;
    private Handler mHandler;

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case BSGameSdkCenter.OK:
                        Toast.makeText(UnityPlayer.currentActivity, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    private void init() {
        sharedInstance.initHandler();
        sharedInstance.gameSdk = BSGameSdk.initialize(
                debug,
                UnityPlayer.currentActivity,
                sharedInstance.merchant_id,
                sharedInstance.app_id,
                sharedInstance.server_id,
                sharedInstance.app_key,
                new InitCallbackListener() {
                    @Override
                    public void onSuccess() {
                        sharedInstance.gameSdk.setAccountListener(new AccountCallBackListener() {
                            @Override
                            public void onAccountInvalid() {
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("message", "account is invalid");
                                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_AccountInvalid, BSGameSdkCallBack.StatusCode_Success,  json.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        try {
                            JSONObject json = new JSONObject();
                            json.put("message", "init gamesdk success");
                            BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Init, BSGameSdkCallBack.StatusCode_Success, json.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed() {
                        try {
                            JSONObject json = new JSONObject();
                            json.put("message", "init gamesdk failed");
                            BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Init, BSGameSdkCallBack.StatusCode_Fail, json.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new ExitCallbackListener() {
                    @Override
                    public void onExit() {
                        //finish();
                        System.exit(0);
                    }
                });
    }

    /**
     * 平台的初始化接口
     * <p>
     * 必须在主线线程初始化，或者在Activity中的OnCreate方法中调用
     */
    public static void init(String info) {

        String[] array = info.split(",");
        sharedInstance = new BSGameSdkCenter();
        sharedInstance.preferences = UnityPlayer.currentActivity.getSharedPreferences("demouser", Context.MODE_PRIVATE);
        sharedInstance.debug = array[0].equalsIgnoreCase("true");
        sharedInstance.merchant_id = array[1];
        sharedInstance.app_id = array[2];
        sharedInstance.server_id = array[3];
        sharedInstance.app_key = array[4];

        if (Looper.myLooper() != null && Looper.myLooper().equals(Looper.getMainLooper())) {
            LogUtils.d("call gamesdk init in main looper");
            sharedInstance.init();
        } else {
            LogUtils.d("call gamesdk init in background looper");
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    sharedInstance.init();
                    LogUtils.d("init callback delay 10s");
                }
            });
        }
    }

    public static void InitDC() {
        sharedInstance.gameSdk.start(UnityPlayer.currentActivity);
    }

    /**
     * 平台的用户登录接口
     */
    public static void login() {
        LogUtils.d("BSGameSdkCenter: login");
        JSONObject json = new JSONObject();

        if (sharedInstance == null || sharedInstance.gameSdk == null) {
            try {
                json.put("message", "init fail or not completed!");
                BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Login, BSGameSdkCallBack.StatusCode_Fail, json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            sharedInstance.gameSdk.login(new BSGameSdkCallBack(BSGameSdkCallBack.CALLBACKTYPE_Login) {
                @Override
                public void onSuccess(Bundle arg0) {
                    // 此处为操作成功时执行，返回值通过Bundle传回
                    //LogUtils.d("onSuccess");
                    String uid = arg0.getString("uid");
                    sharedInstance.user_id = arg0.getString("uid");
                    String userName = arg0.getString("username");
                    String access_token = arg0.getString("access_token");
                    String expire_times = arg0.getString("expire_times");
                    String refresh_token = arg0.getString("refresh_token");
                    String nickname = arg0.getString("nickname");
                    nickname = nickname != null ? nickname : "";
                    JSONObject json = new JSONObject();
                    try {
                        json.put("uid", uid);
                        json.put("username", userName);
                        json.put("access_token", access_token);
                        json.put("expire_times", expire_times);
                        json.put("refresh_token", refresh_token);
                        json.put("nickname", nickname);

                        sharedInstance.preferences.edit().clear().commit();
                        sharedInstance.preferences.edit().putString("username", userName).commit();
                        sharedInstance.preferences.edit().putString("uid", uid).commit();
                        sharedInstance.preferences.edit().putString("nickname", nickname).commit();
                        BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Login, BSGameSdkCallBack.StatusCode_Success, json.toString());
                        sharedInstance.gameSdk.start(UnityPlayer.currentActivity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void checkLogin() {
        sharedInstance.gameSdk.isLogin(new BSGameSdkCallBack(BSGameSdkCallBack.CALLBACKTYPE_IsLogin) {
            @Override
            public void onSuccess(Bundle arg0) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                LogUtils.d("onSuccess");
                JSONObject json = new JSONObject();
                try {
                    json.put("message",  arg0.getBoolean("logined", false));
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_IsLogin, BSGameSdkCallBack.StatusCode_Success, json.toString());
                    sharedInstance.gameSdk.stop(UnityPlayer.currentActivity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void logout() {
        sharedInstance.gameSdk.logout(new BSGameSdkCallBack(BSGameSdkCallBack.CALLBACKTYPE_Logout) {
            @Override
            public void onSuccess(Bundle arg0) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                LogUtils.d("onSuccess");
                JSONObject json = new JSONObject();
                try {
                    json.put("message", arg0.getString("tips"));
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Logout, BSGameSdkCallBack.StatusCode_Success, json.toString());
                    sharedInstance.gameSdk.stop(UnityPlayer.currentActivity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getUserInfo() {
        sharedInstance.gameSdk.getUserInfo(new BSGameSdkCallBack(BSGameSdkCallBack.CALLBACKTYPE_GetUserInfo) {
            @Override
            public void onSuccess(Bundle arg0) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                String uid = arg0.getString("uid");
                String username = arg0.getString("username");
                String access_token = arg0.getString("access_token");
                String expire_times = arg0.getString("expire_times");
                String refresh_token = arg0.getString("refresh_token");
                String lastLoginTime = arg0.getString("last_login_time");
                String avatar = arg0.getString("avatar");
                String s_avatar = arg0.getString("s_avatar");
                LogUtils.d("onSuccess\nuid: " + uid + " username: " + username + " access_token: " + access_token + " expire_times: " + expire_times + " refresh_token: " + refresh_token + " lastLoginTime: " + lastLoginTime);
                JSONObject json = new JSONObject();
                try {
                    json.put("uid", uid);
                    json.put("username", username);
                    json.put("access_token", access_token);
                    json.put("expire_times", expire_times);
                    json.put("refresh_token", refresh_token);
                    json.put("lastLoginTime", lastLoginTime);
                    json.put("avatar", avatar);
                    json.put("s_avatar", s_avatar);
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_GetUserInfo, BSGameSdkCallBack.StatusCode_Success, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void register() {
        sharedInstance.gameSdk.register(new BSGameSdkCallBack(BSGameSdkCallBack.CALLBACKTYPE_Register) {
            @Override
            public void onSuccess(Bundle arg0) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                LogUtils.d("onSuccess");
                // 注册成功后已退出登录，清除保存的信息
                sharedInstance.preferences.edit().clear().commit();
                JSONObject json = new JSONObject();
                try {
                    json.put("message", arg0.getString("result"));
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Register, BSGameSdkCallBack.StatusCode_Success, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void notifyZone(String info) {
        String[] array = info.split(",");
        final String role_id = array[0];
        final String role_name = array[1];
        final String server_id = array[2];
        final String server_name = array[3];

        sharedInstance.gameSdk.notifyZone(server_id, server_name, role_id, role_name);
    }

    public static void createRole(String info) {
        String[] array = info.split(",");
        final String role_id = array[0];
        final String role_name = array[1];

        sharedInstance.gameSdk.createRole(role_name, role_id);
    }

    public static String sign(String data, String secretKey) {
        return MD5.sign(data, secretKey);
    }

    public static void pay(String info) {
        // 支付操作

        String[] array = info.split(",");
        long uid = Long.valueOf(array[0]);
        String username = array[1];
        String role = array[2];
        String serverId = array[3];
        int total_fee = Integer.valueOf(array[4]);
        int game_money = Integer.valueOf(array[5]);
        String out_trade_no = array[6];
        String subject = array[7];
        String body = array[8];
        String extensioninfo = array[9];
        String url = array[10];
        String paykey = array[11];

        sharedInstance.gameSdk.pay(uid, username, role, serverId, total_fee, game_money, out_trade_no, subject, body, extensioninfo, url, paykey, new OrderCallbackListener() {
            @Override
            public void onSuccess(String out_trade_no, String bs_trade_no) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                LogUtils.d("onSuccess");
                JSONObject json = new JSONObject();
                try {
                    json.put("bs_trade_no", bs_trade_no);
                    json.put("out_trade_no", out_trade_no);
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Pay, BSGameSdkCallBack.StatusCode_Success, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtils.d("pay success " + out_trade_no + bs_trade_no);
            }

            @Override
            public void onFailed(String out_trade_no, BSGameSdkError error) {
                JSONObject json = new JSONObject();
                try {
                    json.put("code", error.getErrorCode());
                    json.put("message", error.getErrorMessage());
                    json.put("out_trade_no", out_trade_no);
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Pay, BSGameSdkCallBack.StatusCode_Fail, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtils.d("onFailed\nErrorCode : " + error.getErrorCode() + "\nErrorMessage : " + error.getErrorMessage());
            }

            @Override
            public void onError(String out_trade_no, BSGameSdkError error) {
                JSONObject json = new JSONObject();
                try {
                    json.put("code", error.getErrorCode());
                    json.put("message", error.getErrorMessage());
                    json.put("out_trade_no", out_trade_no);
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Pay, BSGameSdkCallBack.StatusCode_Fail, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtils.d("onError\nErrorCode : " + error.getErrorCode() + "\nErrorMessage : " + error.getErrorMessage());
            }
        });
    }

    public static void showToast(String content) {
        if (!sharedInstance.debug)
            return;
        sharedInstance.makeToast(content);
    }

    private void makeToast(String result) {
        Message msg = new Message();
        msg.what = BSGameSdkCenter.OK;
        msg.obj = result;
        mHandler.sendMessage(msg);
    }

    public static void getFreeUrl(String info) {

        String[] array = info.split(",");
        String source_url = array[0];
        String app_id = array[1];
        String app_key = array[2];


        BSGameSdk.getFreeUrl(UnityPlayer.currentActivity, source_url, app_id, app_key, new BSGameSdkCallBack(BSGameSdkCallBack.CALLBACKTYPE_GetFreeUrl) {
            @Override
            public void onSuccess(Bundle bundle) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                try {
                    JSONObject json = new JSONObject();
                    json.put("result", bundle.getInt("result"));
                    json.put("target_url", bundle.getString("target_url"));
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_GetFreeUrl, BSGameSdkCallBack.StatusCode_Success, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
