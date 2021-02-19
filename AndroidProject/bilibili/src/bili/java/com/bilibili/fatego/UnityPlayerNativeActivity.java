package com.bilibili.fatego;

import com.bsgamesdk.android.BSGameSdk;
import com.unity3d.player.*;

import jp.delightworks.Fgo.player.AndroidPlugin;

public class UnityPlayerNativeActivity extends AndroidPlugin
{
    @Override
    public void onResume() {
        super.onResume();
        BSGameSdk.appOnline(UnityPlayer.currentActivity);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BSGameSdk.appOffline(UnityPlayer.currentActivity);
    }

    @Override
    public void onDestroy() {
        BSGameSdk.appDestroy(UnityPlayer.currentActivity);
        super.onDestroy();
    }
}
