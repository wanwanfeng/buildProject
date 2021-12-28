package com.hello.stella;

import com.bsgamesdk.android.BSGameSdk;
import com.bsgamesdk.android.BSGameSdkCenter;
import com.unity3d.player.*;

public class UnityPlayerNativeActivity extends BSGameSdkCenter
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
