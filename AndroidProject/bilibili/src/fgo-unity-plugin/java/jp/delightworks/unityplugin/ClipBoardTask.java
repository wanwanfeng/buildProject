//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jp.delightworks.unityplugin;

import android.app.Activity;
import android.content.ClipData;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.ClipboardManager;
import android.util.Log;

public class ClipBoardTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "ClipBoardTask";
    protected Activity activity;
    protected String message;

    public ClipBoardTask(Activity activity, String message) {
        Log.d("ClipBoardTask", "create");
        this.activity = activity;
        this.message = message;
    }

    protected Integer doInBackground(String... params) {
        Log.d("ClipBoardTask", "doInBackground: [" + params[0] + "]");

        try {
            while(true) {
                Log.d("ClipBoardTask", "CheckCnacel: [" + this.isCancelled() + "]");
                if (this.isCancelled()) {
                    Log.d("ClipBoardTask", "Cancelled!");
                    break;
                }

                if (this.message == null) {
                    Log.d("ClipBoardTask", "Cancelled2!");
                    break;
                }

                this.publishProgress(new Integer[0]);
                Thread.sleep(1000L);
            }
        } catch (InterruptedException var3) {
            Log.d("ClipBoardTask", "InterruptedException in doInBackground");
        }

        return 0;
    }

    public void onProgressUpdate(Integer... values) {
        Log.d("ClipBoardTask", "onProgressUpdate:");
        if (this.message != null) {
            Log.i("ClipBoardTask", this.message);
            if (VERSION.SDK_INT < 11) {
                ClipboardManager clipboard = (ClipboardManager)this.activity.getSystemService("clipboard");
                clipboard.setText(this.message);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)this.activity.getSystemService("clipboard");
                ClipData clip = ClipData.newPlainText("Copied Text", this.message);
                clipboard.setPrimaryClip(clip);
            }

            this.message = null;
        }

    }

    protected void onPostExecute(Integer result) {
        Log.d("ClipBoardTask", "onPostExecute: [" + result + "]");
    }

    protected void onCancelled() {
        Log.d("ClipBoardTask", "onCancelled:");
    }
}
