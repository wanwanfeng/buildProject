package com.hello.stella;

import com.unity3d.player.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.LinearLayout;

public class PermissionActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    public static final String TAG = "Unity";
    private LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        if (!PermissionUtils.CheckPermissions(this)) {
            PermissionUtils.requestMultiPermissions(this, mPermissionGrant);
        } else {
            StartMainActivity();
        }

    }

    public String getCPID() {
        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String myApiKey = bundle.getString("BILIGAME_CHANNEL_CPID");
            return myApiKey;
        } catch (Exception e) {
            Log.e("error", "no cpid");
            return "0";
        }
    }

    public PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {

            Log.i("cao", "requestPermission requestCode:@~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + requestCode);

            switch (requestCode) {
                default:
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions,
                                           int[] grantResults) {
        Log.d("Permissions", "Permissions result");

        //PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);


        boolean hasPermissionDismiss = false;

        if (requestCode == PermissionUtils.CODE_MULTI_PERMISSION) {

            StartMainActivity();


        }

        Log.d("cao", "Permissions result:" + requestCode + " " + permissions.length + " " + grantResults.length);

    }

    private void StartMainActivity() {
        Intent intent = new Intent(this, UnityPlayerNativeActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * ???????????????????????????????????????
     */
    AlertDialog mPermissionDialog;
    String mPackName = "com.hello.stella";

    private void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("?????????????????????????????????")
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //?????????????????????????????????
                            cancelPermissionDialog();

                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    //???????????????
    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
    }

}
