package com.bilibili.stella;

import android.content.Intent;
import android.graphics.Color;

import com.bsgamesdk.android.uo.GameSdkProxyCommonImp;
import com.bsgamesdk.android.uo.imp.GameSdkProxy;
import com.bsgamesdk.android.uo.splash.BaseSplashActivity;

import android.support.v4.app.ActivityCompat;

public class SplashActivity extends BaseSplashActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public int getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    protected void onSplashStop() {
        StartMainActivity();
    }

    private void StartMainActivity() {
        Intent intent = new Intent(this, PermissionActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected GameSdkProxyCommonImp loadGameSdkProxy() {
        return GameSdkProxy.sharedInstance();
    }
}