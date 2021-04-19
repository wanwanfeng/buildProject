package com.hello.stella;

import android.os.Bundle;
import android.util.Log;

import com.gs.android.GSCallback;
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

    public static class OrderInfo {
        public long moneyAmount;
        public String productName;
        public String order_sign;
        public String notify_url;
        public int productCount;
        public String tradeNo;
        public String subject;
        public String extInfo;

        @Override
        public String toString() {
            return "OrderInfo{" +
                    "moneyAmount=" + moneyAmount +
                    ", productName='" + productName + '\'' +
                    ", order_sign='" + order_sign + '\'' +
                    ", notify_url='" + notify_url + '\'' +
                    ", productCount=" + productCount +
                    ", tradeNo='" + tradeNo + '\'' +
                    ", subject='" + subject + '\'' +
                    ", extInfo='" + extInfo + '\'' +
                    '}';
        }
    }

    protected static BaseData sharedInstance = null;

    protected OrderInfo orderInfo = null;
    protected User userInfo = null;
    protected boolean status = false;

    public void SendLoginMessage(int code) {
        LogUtils.d("###", "public SendLoginMessage");
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
        LogUtils.d("###", "登陆失败：" + code + " error code:" + message);
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
        LogUtils.d("###", "public void onLogout() :");
        try {
            LogUtils.d("###", "public void onLogout(),uid:" + this.userInfo.userID);
            unity3dSendMessage("Logout", StatusCode_Success, "");
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
            dat.put("bs_trade_no", order_no);
            unity3dSendMessage("Pay", StatusCode_Success, dat.toString());
            LogUtils.d("###", "Pay success ");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPayFailure(int code, String message) {
        LogUtils.d("###", "Pay fail");
        try {
            JSONObject dat = new JSONObject();
            dat.put("out_trade_no", "");
            dat.put("message", "[" + code + "]" + message);
            unity3dSendMessage("Pay", StatusCode_Fail, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetUserInfoSuccess(Bundle arg0) {
        UnityPlayerNativeActivity.User userInfo = new UnityPlayerNativeActivity.User();
        userInfo.userID = arg0.getString("uid");
        userInfo.userName = arg0.getString("username");
        userInfo.access_token = arg0.getString("access_token");
        userInfo.login_type = arg0.getInt("login_type");

        try {
            JSONObject dat = new JSONObject();
            dat.put("username", userInfo.userName);
            dat.put("nickname", userInfo.userName);
            dat.put("uid", userInfo.userID);
            dat.put("access_token", userInfo.access_token);
            dat.put("login_type", userInfo.login_type);
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
    public void onUserUpgrade(Bundle bundle) {

    }

    @Override
    public void onAccountInvalid() {

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
        LogUtils.d("###", "public void onGameTermsSuccess() :");
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", 0);
            dat.put("enable_nighttime_push", enable_nighttime_push);
            unity3dSendMessage("ShowGameTerms", StatusCode_Fail, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGameTermsRefuse() {
        LogUtils.d("###", "public void onGameTermsRefuse() :");
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", 1);
            dat.put("messagge", "onGameTermsRefuse");
            unity3dSendMessage("ShowGameTerms", StatusCode_Fail, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGameTermsIgnore() {
        LogUtils.d("###", "public void onGameTermsIgnore() :");
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", 2);
            dat.put("message", "onGameTermsIgnore");
            unity3dSendMessage("ShowGameTerms", StatusCode_Fail, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSwitchAccount() {

    }
}
