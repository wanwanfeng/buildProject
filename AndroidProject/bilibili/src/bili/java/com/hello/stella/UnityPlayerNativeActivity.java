package com.hello.stella;

import android.os.Bundle;

import com.bsgamesdk.android.BSGameSdkCallBack;
import com.unity3d.player.*;

import org.json.JSONException;
import org.json.JSONObject;

public class UnityPlayerNativeActivity extends com.bsgamesdk.android.BSGameSdkCenter
{
    @Override
    public void onResume() {
        super.onResume();
        gameSdk.appOnline(UnityPlayer.currentActivity);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameSdk.appOffline(UnityPlayer.currentActivity);
    }

    @Override
    public void onDestroy() {
        gameSdk.appDestroy(UnityPlayer.currentActivity);
        super.onDestroy();
    }

    public void isRealNameAuth(String info) {
        gameSdk.isRealNameAuth(new BSGameSdkCallBack("IsRealNameAuth") {
            @Override
            public void onSuccess(Bundle arg0) {
                boolean isRealNameAuth = arg0.getBoolean("isRealNameAuth");
                JSONObject json = new JSONObject();
                try {
                    json.put("isRealNameAuth", isRealNameAuth==true);
                    super.unity3dSendMessage(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showGameTerms(String info)  {
        String[] array = info.split(",");
        gameSdk.showAgreementWithLicence(new BSGameSdkCallBack("ShowGameTerms") {
            @Override
            public void onSuccess(Bundle arg0) {
                gameSdk.showAgreementWithPrivacy(new BSGameSdkCallBack("ShowGameTerms") {
                    @Override
                    public void onSuccess(Bundle arg0) {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("code", "100");
                            super.unity3dSendMessage(json.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void showUserAgreement(String info) {
        String[] array = info.split(",");
        gameSdk.showAgreementWithLicence(new BSGameSdkCallBack("ShowUserAgreement") {
            @Override
            public void onSuccess(Bundle arg0) {
                JSONObject json = new JSONObject();
                try {
                    json.put("code", "100");
                    super.unity3dSendMessage(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showPrivacyPolicy(String info) {
        String[] array = info.split(",");
        gameSdk.showAgreementWithPrivacy(new BSGameSdkCallBack("ShowPrivacyPolicy") {
            @Override
            public void onSuccess(Bundle arg0) {
                JSONObject json = new JSONObject();
                try {
                    json.put("code", "100");
                    super.unity3dSendMessage(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void userCenter(String info) {
        JSONObject json = new JSONObject();
        try {
            json.put("code", "100");
            unity3dSendMessage("UserCenter", StatusCode_Success, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showGeetestView(String info) {

        gameSdk.showGeetestView(new BSGameSdkCallBack("ShowGeetestView") {
            @Override
            public void onSuccess(Bundle arg0) {
                String captcha_type = arg0.getString("captcha_type");
                String image_token = arg0.getString("image_token");
                String captcha_code = arg0.getString("captcha_code");
                String challenge = arg0.getString("challenge");
                String validate = arg0.getString("validate");
                String seccode = arg0.getString("seccode");
                String gt_user_id = arg0.getString("gt_user_id String");
                JSONObject json = new JSONObject();
                try {
                    json.put("captcha_type", captcha_type);
                    json.put("image_token", image_token);
                    json.put("captcha_code", captcha_code);
                    json.put("challenge", challenge);
                    json.put("validate", validate);
                    json.put("seccode", seccode);
                    json.put("gt_user_id String", gt_user_id);
                    super.unity3dSendMessage(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void switchAccount(String info) {
        JSONObject json = new JSONObject();
        try {
            json.put("code", "100");
            unity3dSendMessage("SwitchAccount", StatusCode_Success, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
