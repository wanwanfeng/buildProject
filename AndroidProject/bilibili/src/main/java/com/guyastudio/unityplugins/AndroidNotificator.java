//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.guyastudio.unityplugins;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.bilibili.stella.R;
import com.unity3d.player.UnityPlayer;
import java.util.Calendar;

/**
 * 用于生成 / 清除本地通知推送的插件
 * 仅在安卓平台有效
 */
public class AndroidNotificator extends BroadcastReceiver {
    private static int m_nLastID = 0;

    public AndroidNotificator() {
    }
    /**
     * 显示数秒后的通知
     *
     * @param pAppName 应用名
     * @param pTitle 通知标题
     * @param pContent 通知内容
     * @param pDelaySecond 延迟时间
     * @param pIsDailyLoop 是否每日自动推送
     * @throws IllegalArgumentException 错误信息
     */
    public static void ShowNotification(String pAppName, String pTitle, String pContent, int pDelaySecond, boolean pIsDailyLoop) throws IllegalArgumentException {
        Log.d("ShowNotification","ShowNotification,"+ pAppName+","+pTitle+","+pContent+","+pDelaySecond+","+pIsDailyLoop);
        if (pDelaySecond < 0) {
            throw new IllegalArgumentException("The param: pDelaySecond < 0");
        } else {
            Activity curActivity = UnityPlayer.currentActivity;
            Intent intent = new Intent("UNITY_NOTIFICATOR");
            intent.addFlags(0x01000000);
            intent.putExtra("appname", pAppName);
            intent.putExtra("title", pTitle);
            intent.putExtra("content", pContent);
            PendingIntent pi = PendingIntent.getBroadcast(curActivity, 0, intent, 0);
            AlarmManager am = (AlarmManager)curActivity.getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(13, pDelaySecond);
            long alarmTime = calendar.getTimeInMillis();
            if (pIsDailyLoop) {
                am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 86400L, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
            }
        }
    }

    public static void ClearNotification() {
        Activity act = UnityPlayer.currentActivity;
        NotificationManager nManager = (NotificationManager)act.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("ClearNotification","ClearNotification");
        for(int i = m_nLastID; i >= 0; --i) {
            nManager.cancel(i);
        }

        m_nLastID = 0;
    }

    @SuppressWarnings("deprecation")
    public void onReceive(Context pContext, Intent pIntent) {
        Class unityActivity = null;
        Log.d("onReceive","onReceive,");
        try {
            unityActivity = pContext.getClassLoader().loadClass("com.unity3d.player.UnityPlayerActivity");
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        Log.d("onReceive","unityActivity,");
        ApplicationInfo applicationInfo = null;
        PackageManager pm = pContext.getPackageManager();

        try {
            applicationInfo = pm.getApplicationInfo(pContext.getPackageName(), 128);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        Log.d("onReceive","applicationInfo,");
        Bundle bundle = pIntent.getExtras();

        NotificationManager nm = (NotificationManager)pContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(pContext);

        //判断是否是8.0Android.O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan1 = new NotificationChannel("static", "Primary Channel", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(chan1);
            builder = new Notification.Builder(pContext, "static");
        }

        builder.setContentText((String)bundle.get("content"));//消息内容
        builder.setContentTitle((String)bundle.get("title"));//设置标题
//        builder.setSmallIcon(applicationInfo.icon); //设置图标
        builder.setSmallIcon(R.drawable.notification_icon); //设置图标
        builder.setWhen(System.currentTimeMillis());//发送时间
        builder.setAutoCancel(true);//打开程序后图标消失
        builder.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光

        PendingIntent pendingIntent = PendingIntent.getActivity(pContext, m_nLastID, new Intent (pContext,unityActivity), 0);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        nm.notify(m_nLastID, notification); // 通过通知管理器发送通知
        m_nLastID++;
    }
}
