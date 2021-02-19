//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jp.delightworks.unityplugin.webview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;

public class WebViewDialogTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "WebViewDialogTask";
    protected Activity activity;
    protected String title;
    protected String message;
    protected AlertDialog dialog;

    public WebViewDialogTask(Activity activity, String title, String message) {
        this.activity = activity;
        this.title = title;
        this.message = message;
    }

    protected Integer doInBackground(String... params) {
        Log.d("WebViewDialogTask", "doInBackground: [" + params[0] + "]");

        try {
            while(true) {
                Log.d("WebViewDialogTask", "CheckCnacel: [" + this.isCancelled() + "]");
                if (this.isCancelled()) {
                    Log.d("WebViewDialogTask", "Cancelled!");
                    break;
                }

                if (this.dialog != null && !this.dialog.isShowing()) {
                    Log.d("WebViewDialogTask", "Cancelled2!");
                    break;
                }

                this.publishProgress(new Integer[0]);
                Thread.sleep(1000L);
            }
        } catch (InterruptedException var3) {
            Log.d("WebViewDialogTask", "InterruptedException in doInBackground");
        }

        return 0;
    }

    public void onProgressUpdate(Integer... values) {
        Log.d("WebViewDialogTask", "onProgressUpdate:");
        if (this.dialog == null) {
            Builder builder = new Builder(this.activity);
            if (this.title != null) {
                builder.setTitle(this.title);
            }

            builder.setMessage(this.message);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("WebViewDialogTask", "Positive which :" + which);
                    dialog.cancel();
                }
            });
            this.dialog = builder.create();
            this.dialog.show();
        }

    }

    protected void onPostExecute(Integer result) {
        Log.d("WebViewDialogTask", "onPostExecute: [" + result + "]");
        if (this.dialog != null) {
            this.dialog.dismiss();
        }

    }

    protected void onCancelled() {
        Log.d("WebViewDialogTask", "onCancelled:");
        if (this.dialog != null) {
            this.dialog.dismiss();
        }

    }
}
