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
                                BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_AccountInvalid, BSGameSdkCallBack.StatusCode_Success, "account is invalid");
                            }
                        });
                        BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Init, BSGameSdkCallBack.StatusCode_Success, "init gamesdk success");
                    }

                    @Override
                    public void onFailed() {
                        BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Init, 10012, "init gamesdk failed");
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
    public static void init(final boolean debug, String merchant_id, String app_id, String server_id, String app_key) {
        sharedInstance = new BSGameSdkCenter();
        sharedInstance.preferences = UnityPlayer.currentActivity.getSharedPreferences("demouser", Context.MODE_PRIVATE);
        sharedInstance.merchant_id = merchant_id;
        sharedInstance.app_id = app_id;
        sharedInstance.server_id = server_id;
        sharedInstance.app_key = app_key;
        sharedInstance.debug = debug;
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
        if (sharedInstance == null || sharedInstance.gameSdk == null) {
            BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Login, 300, "init fail or not completed!");
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
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    sharedInstance.preferences.edit().clear().commit();
                    sharedInstance.preferences.edit().putString("username", userName).commit();
                    sharedInstance.preferences.edit().putString("uid", uid).commit();
                    sharedInstance.preferences.edit().putString("nickname", nickname).commit();
                    /*
                     * center.makeToast("uid: " + uid + " userName: " + userName +
                     * " access_token: " + access_token + " expire_times: " +
                     * expire_times + " refresh_token: " + refresh_token);
                     */
                    sharedInstance.gameSdk.start(UnityPlayer.currentActivity);
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Login, BSGameSdkCallBack.StatusCode_Success, json.toString());
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
                boolean logined = arg0.getBoolean("logined", false);
                BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_IsLogin, BSGameSdkCallBack.StatusCode_Success, logined);
            }
        });
    }

    public static void logout() {
        sharedInstance.gameSdk.logout(new BSGameSdkCallBack(BSGameSdkCallBack.CALLBACKTYPE_Logout) {
            @Override
            public void onSuccess(Bundle arg0) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                LogUtils.d("onSuccess");
                String tips = arg0.getString("tips");
                sharedInstance.preferences.edit().clear().commit();
                BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Logout, BSGameSdkCallBack.StatusCode_Success, tips);
                sharedInstance.gameSdk.stop(UnityPlayer.currentActivity);
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
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_GetUserInfo, BSGameSdkCallBack.StatusCode_Success, json.toString());
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
                String result = arg0.getString("result");
                BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Register, BSGameSdkCallBack.StatusCode_Success, result);
            }
        });
    }

    public static void notifyZone(String serverid, String servername, String roleid, String rolename) {
        sharedInstance.gameSdk.notifyZone(serverid, servername, roleid, rolename);
    }

    public static void createRole(String rolename, String roleid) {
        sharedInstance.gameSdk.createRole(rolename, roleid);
    }

    public static String sign(String data, String secretKey) {
        return MD5.sign(data, secretKey);
    }

    /**
     * @param uid           用户的唯一标识(整型)
     * @param username      用户名或者email（唯一）
     * @param role          充值的角色信息
     * @param serverId      区服号
     * @param total_fee     充值金额
     * @param game_money    游戏币，需要用充值金额*充值比率
     * @param out_trade_no  充值订单号
     * @param subject       充值主题
     * @param body          充值描述
     * @param extensioninfo 附加信息，会在服务器异步回调中原样传回
     */
    public static void pay(long uid, String username, String role, String serverId, int total_fee, int game_money, String out_trade_no, String subject, String body, String extensioninfo, String url, String paykey) {
        // 支付操作
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
                    json.put("message", error.getErrorMessage());
                    json.put("out_trade_no", out_trade_no);
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Pay, error.getErrorCode(), json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtils.d("onFailed\nErrorCode : " + error.getErrorCode() + "\nErrorMessage : " + error.getErrorMessage());
            }

            @Override
            public void onError(String out_trade_no, BSGameSdkError error) {
                JSONObject json = new JSONObject();
                try {
                    json.put("message", error.getErrorMessage());
                    json.put("out_trade_no", out_trade_no);
                    BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_Pay, error.getErrorCode(), json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtils.d("onError\nErrorCode : " + error.getErrorCode() + "\nErrorMessage : " + error.getErrorMessage());
            }
        });
    }

    public static void testPay() {
        if ("".equals(sharedInstance.preferences.getString("username", ""))) {
            sharedInstance.makeToast("您还未登录！");
            return;
        }
        String username = sharedInstance.preferences.getString("username", "test");
        String payUid = sharedInstance.preferences.getString("uid", "88");
        String role = sharedInstance.preferences.getString("nickname", "默认昵称");
        int uid = Integer.valueOf(payUid);
        pay(uid, username, role, sharedInstance.server_id, 1, 100, String.valueOf(System.currentTimeMillis()), "test-subject", "test-body", "test for new parameters", "111", "111");
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

    /**
     * c：游戏Activity实例
     * sourceUrl: 需要免流的host,也支持完整的url,建议游戏在请求相同域名的多个资源时
     * game_id：平台分配给游戏的唯一标识
     * app_key：商户应用的客户端密钥，请勿使用服务器端密钥
     * listener：CallbackListener的实例
     */
    public static void getFreeUrl(String source_url, String app_id, String app_key) {
        BSGameSdk.getFreeUrl(UnityPlayer.currentActivity, source_url, app_id, app_key, new BSGameSdkCallBack(BSGameSdkCallBack.CALLBACKTYPE_GetFreeUrl) {
            @Override
            public void onSuccess(Bundle bundle) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                LogUtils.d("onSuccess");
                int result = bundle.getInt("result");
                String target_url = bundle.getString("target_url");
                BSGameSdkCallBack.callback(BSGameSdkCallBack.CALLBACKTYPE_GetFreeUrl, result, target_url);
            }
        });
    }
}
