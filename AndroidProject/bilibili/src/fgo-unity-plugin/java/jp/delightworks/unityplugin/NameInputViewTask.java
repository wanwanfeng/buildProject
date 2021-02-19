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

public class NameInputViewTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "NameInputViewTask";
    protected Activity activity;
    protected String editText;
    protected boolean isDialogCreate;
    protected Dialog dialog;

    public NameInputViewTask(Activity activity, String baseText) {
        Log.d("NameInputViewTask", "create A");
        this.activity = activity;
        this.editText = baseText;
        this.isDialogCreate = false;
    }

    protected Integer doInBackground(String... params) {
        Log.d("NameInputViewTask", "doInBackground: [" + params[0] + "]");

        try {
            while(true) {
                Log.d("NameInputViewTask", "CheckCnacel: [" + this.isCancelled() + "]");
                if (this.isCancelled()) {
                    Log.d("NameInputViewTask", "Cancelled!");
                    break;
                }

                if (this.dialog != null && !this.dialog.isShowing()) {
                    Log.d("NameInputViewTask", "Cancelled2!");
                    break;
                }

                this.publishProgress(new Integer[0]);
                Thread.sleep(1000L);
            }
        } catch (InterruptedException var3) {
            Log.d("NameInputViewTask", "InterruptedException in doInBackground");
        }

        return 0;
    }

    public void onProgressUpdate(Integer... values) {
        Log.d("NameInputViewTask", "onProgressUpdate:");
        if (!this.isDialogCreate) {
            this.isDialogCreate = true;
            Log.i("NameInputViewTask", this.editText);
            if (this.dialog == null) {
                Log.d("NameInputViewTask", "create common alert dialog");
                Builder builder = new Builder(this.activity);
                builder.setMessage(this.editText);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("NameInputViewTask", "Positive which :" + which);
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
        Log.d("NameInputViewTask", "onPostExecute: [" + result + "]");
        if (this.dialog != null) {
            this.dialog.dismiss();
        }

    }

    protected void onCancelled() {
        Log.d("NameInputViewTask", "onCancelled:");
        if (this.dialog != null) {
            this.dialog.dismiss();
        }

    }
}
