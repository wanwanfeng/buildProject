package com.bsgamesdk.android;

import org.json.JSONObject;

import android.os.Bundle;

import com.bsgamesdk.android.callbacklistener.BSGameSdkError;
import com.bsgamesdk.android.callbacklistener.CallbackListener;
import com.bsgamesdk.android.utils.LogUtils;
import com.unity3d.player.UnityPlayerNativeActivity;

public class BSGameSdkCallBack implements CallbackListener {

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


    public static final int StatusCode_Success = UnityPlayerNativeActivity.StatusCode_Success;
    public static final int StatusCode_Fail = UnityPlayerNativeActivity.StatusCode_Fail;

    public static void callback(String callbackType, int code, Object data) {
        UnityPlayerNativeActivity.unity3dSendMessage(callbackType, code, data);
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
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", arg0.getErrorCode());
            dat.put("message", arg0.getErrorMessage());

            // 此处为操作失败时执行，返回值为BSGameSdkError类型变量，其中包含ErrorCode和ErrorMessage
            LogUtils.d("onFailed\nErrorCode : " + arg0.getErrorCode() + "\nErrorMessage : " + arg0.getErrorMessage());
            callback(callbackType, StatusCode_Fail, dat.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(BSGameSdkError arg0) {
        try {
            JSONObject dat = new JSONObject();
            dat.put("code", arg0.getErrorCode());
            dat.put("message", arg0.getErrorMessage());
            callback(callbackType, StatusCode_Fail, dat.toString());
            // 此处为操作异常时执行，返回值为BSGameSdkError类型变量，其中包含ErrorCode和ErrorMessage
            LogUtils.d("onError\nErrorCode : " + arg0.getErrorCode() + "\nErrorMessage : " + arg0.getErrorMessage());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
