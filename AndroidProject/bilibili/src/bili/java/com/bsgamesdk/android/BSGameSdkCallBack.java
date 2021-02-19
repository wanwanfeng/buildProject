package com.bsgamesdk.android;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.bsgamesdk.android.callbacklistener.BSGameSdkError;
import com.bsgamesdk.android.callbacklistener.CallbackListener;
import com.bsgamesdk.android.utils.LogUtils;
import com.unity3d.player.UnityPlayer;

public class BSGameSdkCallBack implements CallbackListener {

    private static final String TAG = "Unity3DCallback";

    // Target GameObject来接收消息
    public static final String Target_Camera = "Management";

    // Target GameObject的监听方法
    public static final String OnBSGameSdkCallback = "OnBSGameSdkCallback";

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

    public static void unity3dSendMessage(String json) {
        Log.d(TAG, "send message to Unity3D, message data =" + json.toString());
        UnityPlayer.UnitySendMessage(Target_Camera, OnBSGameSdkCallback, json);
    }

    public static void callback(String callbackType, int code, Object data) {
        Log.d(TAG, "send message to Unity3D, callbackType =" + callbackType);
        try {
            JSONObject jobj = new JSONObject();
            jobj.put("callbackType", callbackType);
            jobj.put("code", code);
            jobj.put("data", data);
            unity3dSendMessage(jobj.toString());
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void callback(String json) {
        unity3dSendMessage(json);
    }

    public String callbackType;

    public BSGameSdkCallBack(String callbackType) {
        this.callbackType = callbackType;
    }

    @Override
    public void onSuccess(Bundle bundle) {

    }

    @Override
    public void onFailed(BSGameSdkError arg0) {
        callback(callbackType, arg0.getErrorCode(), arg0.getErrorMessage());
        // 此处为操作失败时执行，返回值为BSGameSdkError类型变量，其中包含ErrorCode和ErrorMessage
        LogUtils.d("onFailed\nErrorCode : " + arg0.getErrorCode() + "\nErrorMessage : " + arg0.getErrorMessage());
    }

    @Override
    public void onError(BSGameSdkError arg0) {
        callback(callbackType, arg0.getErrorCode(), arg0.getErrorMessage());
        // 此处为操作异常时执行，返回值为BSGameSdkError类型变量，其中包含ErrorCode和ErrorMessage
        LogUtils.d("onError\nErrorCode : " + arg0.getErrorCode() + "\nErrorMessage : " + arg0.getErrorMessage());
    }
}
