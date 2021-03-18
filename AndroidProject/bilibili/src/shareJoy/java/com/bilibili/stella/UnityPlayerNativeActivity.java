package com.bilibili.stella;

import com.bsgamesdk.android.BSGameSdk;
import com.unity3d.player.*;

public class UnityPlayerNativeActivity extends com.unity3d.player.UnityPlayerNativeActivity
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

    @Override
    public int getSdkType() {
        return 2;
    }
}
