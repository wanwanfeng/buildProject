package com.bilibili.stella;

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

    public static final int StatusCode_Success = 10010;
    public static final int StatusCode_Fail = 10012;

    public static void unity3dSendMessage(String callbackType, int code, Object data) {
        Log.d(TAG, "send message to Unity3D, callbackType =" + callbackType);
        try {
            JSONObject jobj = new JSONObject();
            jobj.put("callbackType", callbackType);
            jobj.put("code", code);
            jobj.put("data", data);
            UnityPlayer.UnitySendMessage("SdkManager", "OnReviceCallback", jobj.toString());
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static class BaseData {
        public String merchant_id;
        public String app_id;
        public String server_id;
        public String app_key;
        public boolean debug;
    }

    public static class User {
        public String userID;
        public String userName;
        public String access_token;
        public String login_type;
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
        user.login_type = arg0.getString("login_type");

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
                SendLoginMessage(10011);
            }
        }
    }

    @Override
    public void onLoginFailure(int code, String message) {
        try {
            unity3dSendMessage("Login", StatusCode_Fail, "");
            userInfo = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.d("###", "登陆失败：" + code + " error code:" + message);
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
    public void onGetUserInfoSuccess(Bundle bundle) {

    }

    @Override
    public void onGetUserInfoFailure(int i, String s) {

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
    public void onGameTermsSuccess(boolean b) {

    }

    @Override
    public void onGameTermsRefuse() {

    }

    @Override
    public void onGameTermsIgnore() {

    }

    @Override
    public void onSwitchAccount() {

    }
}
