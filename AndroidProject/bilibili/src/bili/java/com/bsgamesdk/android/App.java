package com.bsgamesdk.android;

import android.app.Application;

import com.gsc.pub.GSCPubCommon;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GSCPubCommon.applicationAttach(this);
    }
}
