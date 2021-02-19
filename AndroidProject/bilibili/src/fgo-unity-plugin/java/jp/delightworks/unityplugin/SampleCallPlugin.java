//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jp.delightworks.unityplugin;

import android.util.Log;
import com.unity3d.player.UnityPlayer;

public class SampleCallPlugin {
    public SampleCallPlugin() {
    }

    public static int FuncSA(int v) {
        Log.d("Unity Native FuncSA ", "" + v);
        return 1;
    }

    public static String FuncSB(String str) {
        Log.d("Unity Native FuncSB ", str);
        return "Back " + str;
    }

    public void FuncA(String str) {
        Log.d("Unity Native FuncA ", str);
    }

    public String FuncB(String str) {
        Log.d("Unity Native FuncB ", str);
        return "Back " + str;
    }

    public void FuncC(String gameObjName, String str) {
        Log.d("Unity Native FuncC ", str);
        UnityPlayer.UnitySendMessage(gameObjName, "onCallBack", str);
    }
}
