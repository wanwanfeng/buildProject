//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package unity.plugins.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.SparseArray;
import com.unity3d.player.UnityPlayer;

public class DialogManager {
    private static DialogManager _instance;
    private int _id = 0;
    private SparseArray<AlertDialog> _dialogs = new SparseArray();
    private String decideLabel = "Yes";
    private String cancelLabel = "No";
    private String closeLabel = "Close";

    private DialogManager() {
    }

    public static DialogManager getInstance() {
        if (_instance == null) {
            _instance = new DialogManager();
        }

        return _instance;
    }

    public int showSelectDialog(final String msg) {
        ++this._id;
        final int id = this._id;
        final Activity a = UnityPlayer.currentActivity;
        a.runOnUiThread(new Runnable() {
            public void run() {
                OnClickListener positiveListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DialogManager.this.log("submit " + id + ":" + msg);
                        UnityPlayer.UnitySendMessage("DialogManager", "OnSubmit", String.valueOf(id));
                        DialogManager.this._dialogs.delete(id);
                    }
                };
                OnClickListener negativeListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DialogManager.this.log("defuse " + id + ":" + msg);
                        UnityPlayer.UnitySendMessage("DialogManager", "OnCancel", String.valueOf(id));
                        DialogManager.this._dialogs.delete(id);
                    }
                };
                AlertDialog dialog = (new Builder(a)).setMessage(msg).setNegativeButton(DialogManager.this.cancelLabel, negativeListener).setPositiveButton(DialogManager.this.decideLabel, positiveListener).setCancelable(false).show();
                DialogManager.this._dialogs.put(Integer.valueOf(id), dialog);
            }
        });
        return id;
    }

    public int showSelectDialog(final String title, final String msg) {
        ++this._id;
        final int id = this._id;
        final Activity a = UnityPlayer.currentActivity;
        a.runOnUiThread(new Runnable() {
            public void run() {
                OnClickListener positiveListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DialogManager.this.log("submit " + id + ":" + msg);
                        UnityPlayer.UnitySendMessage("DialogManager", "OnSubmit", String.valueOf(id));
                        DialogManager.this._dialogs.delete(id);
                    }
                };
                OnClickListener negativeListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DialogManager.this.log("defuse " + id + ":" + msg);
                        UnityPlayer.UnitySendMessage("DialogManager", "OnCancel", String.valueOf(id));
                        DialogManager.this._dialogs.delete(id);
                    }
                };
                AlertDialog dialog = (new Builder(a)).setTitle(title).setMessage(msg).setNegativeButton(DialogManager.this.cancelLabel, negativeListener).setPositiveButton(DialogManager.this.decideLabel, positiveListener).setCancelable(false).show();
                DialogManager.this._dialogs.put(Integer.valueOf(id), dialog);
            }
        });
        return id;
    }

    public int showSubmitDialog(final String msg) {
        ++this._id;
        final int id = this._id;
        final Activity a = UnityPlayer.currentActivity;
        a.runOnUiThread(new Runnable() {
            public void run() {
                OnClickListener positiveListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DialogManager.this.log("submit " + id + ":" + msg);
                        UnityPlayer.UnitySendMessage("DialogManager", "OnSubmit", String.valueOf(id));
                        DialogManager.this._dialogs.remove(id);
                    }
                };
                AlertDialog dialog = (new Builder(a)).setMessage(msg).setPositiveButton(DialogManager.this.closeLabel, positiveListener).setCancelable(false).show();
                DialogManager.this._dialogs.put(Integer.valueOf(id), dialog);
            }
        });
        return id;
    }

    public int showSubmitDialog(final String title, final String msg) {
        ++this._id;
        final int id = this._id;
        final Activity a = UnityPlayer.currentActivity;
        a.runOnUiThread(new Runnable() {
            public void run() {
                OnClickListener positiveListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DialogManager.this.log("submit " + id + ":" + msg);
                        UnityPlayer.UnitySendMessage("DialogManager", "OnSubmit", String.valueOf(id));
                        DialogManager.this._dialogs.remove(id);
                    }
                };
                AlertDialog dialog = (new Builder(a)).setTitle(title).setMessage(msg).setPositiveButton(DialogManager.this.closeLabel, positiveListener).setCancelable(false).show();
                DialogManager.this._dialogs.put(Integer.valueOf(id), dialog);
            }
        });
        return id;
    }

    public void dissmissDialog(int id) {
        AlertDialog dialog = (AlertDialog)this._dialogs.get(id);
        if (dialog != null) {
            dialog.dismiss();
            this._dialogs.remove(id);
        }
    }

    public void setLabel(String decide, String cancel, String close) {
        this.decideLabel = decide;
        this.cancelLabel = cancel;
        this.closeLabel = close;
    }

    public static int ShowSelectDialog(String msg) {
        return getInstance().showSelectDialog(msg);
    }

    public static int ShowSelectTitleDialog(String title, String msg) {
        return getInstance().showSelectDialog(title, msg);
    }

    public static int ShowSubmitDialog(String msg) {
        return getInstance().showSubmitDialog(msg);
    }

    public static int ShowSubmitTitleDialog(String title, String msg) {
        return getInstance().showSubmitDialog(title, msg);
    }

    public static void DissmissDialog(int id) {
        getInstance().dissmissDialog(id);
    }

    public static void SetLabel(String decide, String cancel, String close) {
        getInstance().setLabel(decide, cancel, close);
    }

    private void log(String msg) {
    }
}
