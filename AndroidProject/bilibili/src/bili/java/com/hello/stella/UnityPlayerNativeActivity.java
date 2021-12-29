package com.hello.stella;

import com.unity3d.player.*;

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
}
