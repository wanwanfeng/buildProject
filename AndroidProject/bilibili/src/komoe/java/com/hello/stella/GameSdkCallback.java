package com.hello.stella;

import android.os.Bundle;
import android.util.Log;

import com.gs.android.base.interfaces.GSCallback;
import com.gs.android.base.utils.LogUtils;
import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import java.util.List;

public class GameSdkCallback extends com.unity3d.player.UnityPlayerNativeActivity implements GSCallback {

    private static final String TAG = "Unity3DCallback";

    public static final String CALLBACKTYPE_IsLogin = "IsLogin";
    public static final String CALLBACKTYPE_Login = "Login";
    public static final String CALLBACKTYPE_Logout = "Logout";
    public static final String CALLBACKTYPE_Register = "Register";
    public static final String CALLBACKTYPE_UserCenter = "UserCenter";
    public static final String CALLBACKTYPE_GetUserInfo = "GetUserInfo";
    public static final String CALLBACKTYPE_Pay = "Pay";
    public static final String CALLBACKTYPE_Init = "Init";
    public static final String CALLBACKTYPE_AccountInvalid = "AccountInvalid";
    public static final String CALLBACKTYPE_GetFreeUrl = "GetFreeUrl";

    public static class BaseData {
        public String merchant_id;
        public String app_id;
        public String server_id;
        public String app_key;
        public boolean debug;

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

    public static class User {
        public String userID;
        public String userName;
        public String access_token;
        public int login_type;

        @Override
        public String toString() {
            return "User{" +
                    "userID='" + userID + '\'' +
                    ", userName='" + userName + '\'' +
                    ", access_token='" + access_token + '\'' +
                    ", login_type=" + login_type +
                    '}';
        }
    }

    protected static BaseData sharedInstance = null;

    protected User userInfo = null;
    protected boolean status = false;

    public void SendLoginMessage(int code) {
        Log.i("###", "public SendLoginMessage");
        try {
            JSONObject dat = new JSONObject();
            dat.put("username", userInfo.userName);
            dat.put("nickname", userInfo.userName);
            dat.put("uid", userInfo.userID);
            dat.put("access_token", userInfo.access_token);
            dat.put("login_type", userInfo.login_type);
            unity3dSendMessage("Login", code, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoginSuccess(Bundle arg0) {
        UnityPlayerNativeActivity.User user = new UnityPlayerNativeActivity.User();
        user.userID = arg0.getString("uid");
        user.userName = arg0.getString("username");
        user.access_token = arg0.getString("access_token");
        user.login_type = arg0.getInt("login_type");

        if (status) {
            userInfo = user;
            return;
        }
        if (userInfo == null) {
            userInfo = user;
            SendLoginMessage(StatusCode_Success);
        } else {
            if (user.userID.equalsIgnoreCase(userInfo.userID)) {
                return;
            } else {
                userInfo = user;
                SendLoginMessage(StatusCode_AccountChange);
            }
        }
    }

    @Override
    public void onLoginFailure(int code, String message) {
        Log.i("###", "???????????????" + code + " error code:" + message);
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", code);
            dat.put("message", message);
            unity3dSendMessage("Login", StatusCode_Fail, dat.toString());
            userInfo = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLogout() {
        Log.i("###", "public void onLogout() :");
        try {
            Log.i("###", "public void onLogout(),uid:" + this.userInfo.userID);
            JSONObject dat = new JSONObject();
            dat.put("code", 0);
            unity3dSendMessage("Logout", StatusCode_Success, dat.toString());
            userInfo = null;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaySuccess(String out_trade_no, String order_no) {
        try {
            JSONObject dat = new JSONObject();
            dat.put("out_trade_no", out_trade_no);
            dat.put("order_no", order_no);
            unity3dSendMessage("Pay", StatusCode_Success, dat.toString());
            Log.i("###", "Pay success ");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPayFailure(int code, String message) {
        Log.i("###", "Pay fail");
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", code);
            dat.put("message", message);
            unity3dSendMessage("Pay", StatusCode_Fail, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetUserInfoSuccess(Bundle arg0) {
        UnityPlayerNativeActivity.User user = new UnityPlayerNativeActivity.User();
        user.userID = arg0.getString("uid");
        user.userName = arg0.getString("username");
        user.access_token = arg0.getString("access_token");
        user.login_type = arg0.getInt("login_type");

        userInfo = user;

        try {
            JSONObject dat = new JSONObject();
            dat.put("username", user.userName);
            dat.put("nickname", user.userName);
            dat.put("uid", user.userID);
            dat.put("access_token", user.access_token);
            dat.put("login_type", user.login_type);
            unity3dSendMessage("GetUserInfo", StatusCode_Success, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetUserInfoFailure(int code, String message) {
        try {
            userInfo = null;
            JSONObject dat = new JSONObject();
            dat.put("code", code);
            dat.put("message", message);
            unity3dSendMessage("GetUserInfo", StatusCode_Fail, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onQueryHistoryOrdersSuccess(String s, String s1) {

    }

    @Override
    public void onQueryHistoryOrdersFailure(int i, String s) {

    }

    @Override
    public void onUserUpgrade(Bundle arg0) {
        UnityPlayerNativeActivity.User user = new UnityPlayerNativeActivity.User();
        user.userID = arg0.getString("uid");
        user.userName = arg0.getString("username");
        user.access_token = arg0.getString("access_token");
        user.login_type = arg0.getInt("login_type");

        userInfo = user;

        try {
            JSONObject dat = new JSONObject();
            dat.put("username", user.userName);
            dat.put("nickname", user.userName);
            dat.put("uid", user.userID);
            dat.put("access_token", user.access_token);
            dat.put("login_type", user.login_type);
            unity3dSendMessage("UserUpgradeWithUserInfo", StatusCode_Success, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccountInvalid() {
        try {
            userInfo = null;
            JSONObject dat = new JSONObject();
            dat.put("code", 0);
            unity3dSendMessage("AccountInvalid", StatusCode_Success, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShareSuccess(int i, String s) {

    }

    @Override
    public void onShareFailure(int i, int i1, String s) {

    }

    @Override
    public void onQueryExchangeProductsSuccess(List<String> list) {

    }

    @Override
    public void onQueryExchangeProductsFailure(int i, String s) {

    }

    @Override
    public void onReceiveExchangeProductSuccess(String s, String s1, String s2) {

    }

    @Override
    public void onReceiveExchangeProductFailure(int i, String s) {

    }

    @Override
    public void onGameTermsSuccess(boolean enable_nighttime_push) {
        Log.i("###", "public void onGameTermsSuccess() :");
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", 0);
            dat.put("enable_nighttime_push", enable_nighttime_push ? 1 : 0);
            unity3dSendMessage("ShowGameTerms", StatusCode_Success, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGameTermsIgnore() {
        Log.i("###", "public void onGameTermsIgnore() :");
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", 1);
            dat.put("message", "onGameTermsIgnore");
            unity3dSendMessage("ShowGameTerms", StatusCode_Success, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGameTermsRefuse(int code, String message) {
        Log.i("###", "public void onGameTermsRefuse() :");
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", code);
            dat.put("message", message);
            unity3dSendMessage("ShowGameTerms", StatusCode_Fail, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSwitchAccount() {
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", 0);
            dat.put("message", "message");
            unity3dSendMessage("SwitchAccount", StatusCode_Success, dat.toString());
            userInfo = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
