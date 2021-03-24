package com.bilibili.stella;

public class UnityPlayerNativeActivity extends com.unity3d.player.UnityPlayerNativeActivity {
    @Override
    public int getSdkType() {
        return 0;
    }

    @Override
    public int getLanguageType() {
        return 3;
    }
}
