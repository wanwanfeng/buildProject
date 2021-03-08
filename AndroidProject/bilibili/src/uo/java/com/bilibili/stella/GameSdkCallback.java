package com.bilibili.stella;

import android.util.Log;

import com.bsgamesdk.android.uo.BSGameSdkError;

import org.json.JSONObject;

import com.bsgamesdk.android.uo.callback.UserListener;
import com.bsgamesdk.android.uo.imp.GameSdkProxy;
import com.bsgamesdk.android.uo.model.User;
import com.bsgamesdk.android.uo.utils.LogUtils;
import com.bsgamesdk.android.uo.utils.ToastUtil;
import com.unity3d.player.UnityPlayer;


public class GameSdkCallback extends com.unity3d.player.UnityPlayerNativeActivity implements UserListener {

    private static final String TAG = "Unity3DCallback";
    public static final int StatusCode_Success = 10010;
    public static final int StatusCode_Fail = 10012;

    protected GameSdkProxy gameSdkProxy = null;
    protected User userInfo = null;
    protected boolean status = false;

    public String getChannelId() {
        return Integer.toString(gameSdkProxy.channelid());
    }

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
            unity3dSendMessage("Login", code, dat.toString());
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
    public void onLoginFailed(BSGameSdkError error) {
        try {
            unity3dSendMessage("Login", StatusCode_Fail, "");
            userInfo = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.d("###", "登陆失败：" + error.getErrorMessage() + " error code:" + error.getErrorCode());
        ToastUtil.showToast(UnityPlayer.currentActivity, "登陆失败，请重试。" + "error code：" + error.getErrorCode());
    }

    @Override
    public void onLogout() {
        LogUtils.d("###", "public void onLogout() getChannelId :" + getChannelId());
        try {
            LogUtils.d("###", "public void onLogout(),uid:" + this.userInfo.userID);
            unity3dSendMessage("Logout", StatusCode_Success, "");
            userInfo = null;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
