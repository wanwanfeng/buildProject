package com.bsgamesdk.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.bsgamesdk.android.callbacklistener.AccountCallBackListener;
import com.bsgamesdk.android.callbacklistener.ExitCallbackListener;
import com.bsgamesdk.android.callbacklistener.BSGameSdkError;
import com.bsgamesdk.android.callbacklistener.InitCallbackListener;
import com.bsgamesdk.android.callbacklistener.OrderCallbackListener;
import com.bsgamesdk.android.utils.LogUtils;
import com.unity3d.player.UnityPlayer;

public class BSGameSdkCenter extends com.unity3d.player.UnityPlayerNativeActivity
{
    public static class BaseData {
        public String merchant_id;
        public String app_id;
        public String server_id;
        public String app_key;
        public boolean debug = true;

        @Override
        public String toString() {
            return "BaseData{" +
                    "merchant_id='" + merchant_id + '\'' +
                    ", app_id='" + app_id + '\'' +
                    ", server_id='" + server_id + '\'' +
                    ", app_key='" + app_key + '\'' +
                    ", debug=" + debug +
                    '}';
        }
    }

    public static final int OK = 1;
    protected static BaseData sharedInstance = null;
    private SharedPreferences preferences;
    private Handler mHandler;
    protected BSGameSdk gameSdk;
    protected boolean status = false;

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
        initHandler();
        gameSdk = BSGameSdk.initialize(
                sharedInstance.debug,
                UnityPlayer.currentActivity,
                sharedInstance.merchant_id,
                sharedInstance.app_id,
                sharedInstance.server_id,
                sharedInstance.app_key,
                new InitCallbackListener() {
                    @Override
                    public void onSuccess() {
                        gameSdk.setAccountListener(new AccountCallBackListener() {
                            @Override
                            public void onAccountInvalid() {
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("message", "account is invalid");
                                    unity3dSendMessage("AccountInvalid", StatusCode_Success,  json.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        try {
                            JSONObject json = new JSONObject();
                            json.put("message", "init gamesdk success");
                            unity3dSendMessage("Init", StatusCode_Success, json.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed() {
                        try {
                            JSONObject json = new JSONObject();
                            json.put("message", "init gamesdk failed");
                            unity3dSendMessage("Init", StatusCode_Fail, json.toString());
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
    public void init(String info) {

        String[] array = info.split(",");
        preferences = UnityPlayer.currentActivity.getSharedPreferences("demouser", Context.MODE_PRIVATE);

        sharedInstance = new BaseData();
        sharedInstance.debug = array[0].equalsIgnoreCase("true");
        sharedInstance.merchant_id = array[1];
        sharedInstance.app_id = array[2];
        sharedInstance.server_id = array[3];
        sharedInstance.app_key = array[4];

        if (Looper.myLooper() != null && Looper.myLooper().equals(Looper.getMainLooper())) {
            LogUtils.d("call gamesdk init in main looper");
            init();
        } else {
            LogUtils.d("call gamesdk init in background looper");
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    init();
                    LogUtils.d("init callback delay 10s");
                }
            });
        }
    }

    /**
     * 平台的用户登录接口
     */
    public void login() {
        gameSdk.login(new BSGameSdkCallBack("Login") {
            @Override
            public void onSuccess(Bundle arg0) {
                String uid = arg0.getString("uid");
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

                    preferences.edit().clear().commit();
                    preferences.edit().putString("username", userName).commit();
                    preferences.edit().putString("uid", uid).commit();
                    preferences.edit().putString("nickname", nickname).commit();
                    super.unity3dSendMessage(json.toString());
                    gameSdk.start(UnityPlayer.currentActivity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void checkLogin() {
        gameSdk.isLogin(new BSGameSdkCallBack("IsLogin") {
            @Override
            public void onSuccess(Bundle arg0) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                LogUtils.d("onSuccess");
                JSONObject json = new JSONObject();
                try {
                    json.put("message",  arg0.getBoolean("logined", false));
                    super.unity3dSendMessage(json.toString());
                    gameSdk.stop(UnityPlayer.currentActivity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void logout() {
        gameSdk.logout(new BSGameSdkCallBack("Logout") {
            @Override
            public void onSuccess(Bundle arg0) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                LogUtils.d("onSuccess");
                JSONObject json = new JSONObject();
                try {
                    json.put("message", arg0.getString("tips"));
                    super.unity3dSendMessage(json.toString());
                    gameSdk.stop(UnityPlayer.currentActivity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getUserInfo() {
        gameSdk.getUserInfo(new BSGameSdkCallBack("GetUserInfo") {
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
                    super.unity3dSendMessage(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void notifyZone(String info) {
        String[] array = info.split(",");
        final String role_id = array[0];
        final String role_name = array[1];
        final String server_id = array[2];
        final String server_name = array[3];

        gameSdk.notifyZone(server_id, server_name, role_id, role_name);
    }

    public void createRole(String info) {
        String[] array = info.split(",");
        final String role_id = array[0];
        final String role_name = array[1];

        gameSdk.createRole(role_name, role_id);
    }

    public static String sign(String data, String secretKey) {
        return MD5.sign(data, secretKey);
    }

    public void pay(String info) {

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

        gameSdk.pay(uid, username, role, serverId, total_fee, game_money, out_trade_no, subject, body, extensioninfo, url, paykey, new BSGameSdkCallBack("Pay") {
            @Override
            public void onSuccess(String out_trade_no, String bs_trade_no) {
                // 此处为操作成功时执行，返回值通过Bundle传回
                LogUtils.d("onSuccess");
                JSONObject json = new JSONObject();
                try {
                    json.put("bs_trade_no", bs_trade_no);
                    json.put("out_trade_no", out_trade_no);
                    super.unity3dSendMessage(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtils.d("pay success " + out_trade_no + bs_trade_no);
            }
        });
    }

    public void showToast(String content) {
        if (!sharedInstance.debug)
            return;
        makeToast(content);
    }

    private void makeToast(String result) {
        Message msg = new Message();
        msg.what = BSGameSdkCenter.OK;
        msg.obj = result;
        mHandler.sendMessage(msg);
    }

    public void getFreeUrl(String info) {

        String[] array = info.split(",");
        String source_url = array[0];
        String app_id = array[1];
        String app_key = array[2];


        BSGameSdk.getFreeUrl(UnityPlayer.currentActivity, source_url, app_id, app_key, new BSGameSdkCallBack("GetFreeUrl") {
            @Override
            public void onSuccess(Bundle bundle) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("result", bundle.getInt("result"));
                    json.put("target_url", bundle.getString("target_url"));
                    super.unity3dSendMessage(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void isRealNameAuth(String info) {
        gameSdk.isRealNameAuth(new BSGameSdkCallBack("IsRealNameAuth") {
            @Override
            public void onSuccess(Bundle arg0) {
                boolean isRealNameAuth = arg0.getBoolean("isRealNameAuth");
                JSONObject json = new JSONObject();
                try {
                    json.put("isRealNameAuth", isRealNameAuth == true);
                    super.unity3dSendMessage(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
