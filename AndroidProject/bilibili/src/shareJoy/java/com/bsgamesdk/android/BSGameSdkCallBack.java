package com.bsgamesdk.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.bsgamesdk.android.callbacklistener.BSGameSdkError;
import com.bsgamesdk.android.callbacklistener.CallbackListener;
import com.bsgamesdk.android.callbacklistener.OrderCallbackListener;
import com.bsgamesdk.android.utils.LogUtils;
import com.unity3d.player.UnityPlayerNativeActivity;

public class BSGameSdkCallBack implements CallbackListener, OrderCallbackListener {

    public String callbackType;

    public BSGameSdkCallBack(String callbackType) {
        this.callbackType = callbackType;
    }

    public void unity3dSendMessage(Object data) {
        UnityPlayerNativeActivity.unity3dSendMessage(callbackType, UnityPlayerNativeActivity.StatusCode_Success, data);
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
            UnityPlayerNativeActivity.unity3dSendMessage(callbackType, UnityPlayerNativeActivity.StatusCode_Fail, dat.toString());
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
            UnityPlayerNativeActivity.unity3dSendMessage(callbackType, UnityPlayerNativeActivity.StatusCode_Fail, dat.toString());
            // 此处为操作异常时执行，返回值为BSGameSdkError类型变量，其中包含ErrorCode和ErrorMessage
            LogUtils.d("onError\nErrorCode : " + arg0.getErrorCode() + "\nErrorMessage : " + arg0.getErrorMessage());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /***
     * pay
     * @param out_trade_no
     * @param bs_trade_no
     */

    @Override
    public void onSuccess(String out_trade_no, String bs_trade_no) {

    }

    /***
     * pay
     * @param out_trade_no
     * @param error
     */
    @Override
    public void onFailed(String out_trade_no, BSGameSdkError error) {
        JSONObject json = new JSONObject();
        try {
            json.put("code", error.getErrorCode());
            json.put("message", error.getErrorMessage());
            json.put("out_trade_no", out_trade_no);
            UnityPlayerNativeActivity.unity3dSendMessage(callbackType, UnityPlayerNativeActivity.StatusCode_Fail, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("onFailed\nErrorCode : " + error.getErrorCode() + "\nErrorMessage : " + error.getErrorMessage());
    }

    /***
     * pay
     * @param out_trade_no
     * @param error
     */
    @Override
    public void onError(String out_trade_no, BSGameSdkError error) {
        JSONObject json = new JSONObject();
        try {
            json.put("code", error.getErrorCode());
            json.put("message", error.getErrorMessage());
            json.put("out_trade_no", out_trade_no);
            UnityPlayerNativeActivity.unity3dSendMessage(callbackType, UnityPlayerNativeActivity.StatusCode_Fail, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("onError\nErrorCode : " + error.getErrorCode() + "\nErrorMessage : " + error.getErrorMessage());
    }
}
