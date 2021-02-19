//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jp.delightworks.unityplugin;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;

public class AlertDialogTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "AlertDialogTask";
    protected Activity activity;
    protected String message;
    protected int errCode;
    protected int requestCode;
    protected boolean isDialogCreate;
    protected Dialog dialog;

    public AlertDialogTask(Activity activity, String message) {
        Log.d("AlertDialogTask", "create A");
        this.activity = activity;
        this.message = message;
        this.isDialogCreate = false;
    }

    public AlertDialogTask(Activity activity, int errCode, int requestCode) {
        Log.d("AlertDialogTask", "create B");
        this.activity = activity;
        this.errCode = errCode;
        this.requestCode = requestCode;
        this.message = null;
        this.isDialogCreate = false;
    }

    protected Integer doInBackground(String... params) {
        Log.d("AlertDialogTask", "doInBackground: [" + params[0] + "]");

        try {
            while(true) {
                Log.d("AlertDialogTask", "CheckCnacel: [" + this.isCancelled() + "]");
                if (this.isCancelled()) {
                    Log.d("AlertDialogTask", "Cancelled!");
                    break;
                }

                if (this.dialog != null && !this.dialog.isShowing()) {
                    Log.d("AlertDialogTask", "Cancelled2!");
                    break;
                }

                this.publishProgress(new Integer[0]);
                Thread.sleep(1000L);
            }
        } catch (InterruptedException var3) {
            Log.d("AlertDialogTask", "InterruptedException in doInBackground");
        }

        return 0;
    }

    public void onProgressUpdate(Integer... values) {
        Log.d("AlertDialogTask", "onProgressUpdate:");
        if (!this.isDialogCreate) {
            this.isDialogCreate = true;
            if (this.message == null) {
                if (this.errCode == 1) {
                    this.message = "Google Play service is not installed.";
                } else {
                    this.message = "You can not use successfully Google Play Service.　[" + this.errCode + "]";
                }

                try {
                    Log.d("AlertDialogTask", "create gps error dialog " + this.dialog);
                } catch (Exception var3) {
                    Log.d("AlertDialogTask", var3.getMessage());
                }
            }

            Log.i("AlertDialogTask", this.message);
            if (this.dialog == null) {
                Log.d("AlertDialogTask", "create common alert dialog");
                Builder builder = new Builder(this.activity);
                builder.setMessage(this.message);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("AlertDialogTask", "Positive which :" + which);
                        dialog.cancel();
                    }
                });
                this.dialog = builder.create();
            }

            if (this.dialog != null) {
                this.dialog.show();
            }
        }

    }

    protected void onPostExecute(Integer result) {
        Log.d("AlertDialogTask", "onPostExecute: [" + result + "]");
        if (this.dialog != null) {
            this.dialog.dismiss();
        }

    }

    protected void onCancelled() {
        Log.d("AlertDialogTask", "onCancelled:");
        if (this.dialog != null) {
            this.dialog.dismiss();
        }

    }
}
