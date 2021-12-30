package com.unity3d.player;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.hello.stella.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class UnityPlayerNativeActivity extends com.sega.sgn.sgnfw.common.unityactivity.SgnfwUnityActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //Android打包部分机型问题，游戏界面在锁屏界面之上的解决方案 https://blog.csdn.net/chenfujun818/article/details/79165172
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        // 防止屏幕息屏 https://www.jb51.net/article/175373.htm
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void ExitGame() {
        Log.d(TAG, "ExitGame");
        this.finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static String GetInternalStoragePath(Activity pAvtivity) {
        String path = "";
        try {
            path = UnityPlayer.currentActivity.getApplicationContext().getFilesDir().getAbsolutePath();
        } catch (Error error) {
            Log.i("GetLocalPath ::::", " Error");
        }
        return path;
    }

    public int getSdkType() {
        return getResources().getInteger(R.integer.sdk_type);
    }

    public int getLanguageType() {
        return getResources().getInteger(R.integer.language_type);
    }

    public int getMultiLanguageType() {
        return getResources().getInteger(R.integer.mullanguage_type);
    }

    public String getUDID() {
        return "";
    }

    public String getMacAddress() {
        return "";
    }

    public String getNetWorkInfo() {
        return "";
    }

    public static final int StatusCode_Success = 10010;
    public static final int StatusCode_Fail = 10012;
    public static final int StatusCode_AccountChange = 10011;

    public static void unity3dSendMessage(String callbackType, int code, Object data) {
        Log.d(TAG, "send message to Unity3D, callbackType = " + callbackType);
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

    public int getNightPushEnabled(String info) {
        return 1;
    }

    public void setNightPushEnabled(String info) {

    }

    public void appsflyerTrackEvent(String eventKey, HashMap<String, Object> eventValues) {
    }

    public void firebaseTrackEvent(String eventKey, HashMap<String, Object> eventValues) {
    }

    public long getFirstInstallTime() {
        try {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            //应用装时间
            long firstInstallTime = packageInfo.firstInstallTime;
            //应用最后一次更新时间
            long lastUpdateTime = packageInfo.lastUpdateTime;
            Log.i(TAG, "first install time : " + firstInstallTime + " last update time :" + lastUpdateTime);
            return firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getLastUpdateTime() {
        try {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            //应用装时间
            long firstInstallTime = packageInfo.firstInstallTime;
            //应用最后一次更新时间
            long lastUpdateTime = packageInfo.lastUpdateTime;
            Log.i(TAG, "first install time : " + firstInstallTime + " last update time :" + lastUpdateTime);
            return firstInstallTime;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getProperty(String propName) {
        Class<?> classType = null;
        String buildVersion = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            buildVersion = (String) getMethod.invoke(classType, new Object[]{propName});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion;
    }

    private void ReturnSuccessJson(String type) {
        JSONObject json = new JSONObject();
        try {
            json.put("code", "100");
            unity3dSendMessage(type, StatusCode_Success, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void init(String info) {
        ReturnSuccessJson("Init");
    }

    public void login() {
        ReturnSuccessJson("Login");
    }

    public void checkLogin() {
        ReturnSuccessJson("IsLogin");
    }

    public void getUserInfo() {
        ReturnSuccessJson("GetUserInfo");
    }

    public void logout() {
        ReturnSuccessJson("Logout");
    }

    public void notifyZone(String info) {
        ReturnSuccessJson("NotifyZone");
    }

    public void createRole(String info) {
        ReturnSuccessJson("CreateRole");
    }

    public void pay(String info) {
        ReturnSuccessJson("Pay");
    }

    public void getFreeUrl(String info) {
        ReturnSuccessJson("GetFreeUrl");
    }

    public void isRealNameAuth(String info) {
        ReturnSuccessJson("IsRealNameAuth");
    }

    public void showGameTerms(String info) {
        ReturnSuccessJson("ShowGameTerms");
    }

    public void showUserAgreement(String info) {
        ReturnSuccessJson("ShowUserAgreement");
    }

    public void showPrivacyPolicy(String info) {
        ReturnSuccessJson("ShowPrivacyPolicy");
    }

    public void userCenter(String info) {
        ReturnSuccessJson("UserCenter");
    }

    public void showGeetestView(String info) {
        ReturnSuccessJson("ShowGeetestView");
    }

    public void switchAccount(String info) {
        ReturnSuccessJson("SwitchAccount");
    }
}
