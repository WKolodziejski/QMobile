package com.tinf.qmobile.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;

public class PermissionsUtils {

  public static boolean hasPermission(Context context) {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
           PackageManager.PERMISSION_GRANTED
           &&
           ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
           PackageManager.PERMISSION_GRANTED;
  }

  public static void requestPermission(Activity activity,
                                       ActivityResultLauncher<String[]> requestPermissionLauncher) {
    new MaterialAlertDialogBuilder(activity)
        .setTitle(activity.getResources().getString(R.string.dialog_permission_title))
        .setMessage(activity.getResources().getString(R.string.dialog_permission_text))
        .setCancelable(false)
        .setPositiveButton(activity.getResources().getString(R.string.dialog_permission_allow),
                           (dialogInterface, i) ->
                               requestPermissionLauncher.launch(new String[] {
                                   Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                   Manifest.permission.READ_EXTERNAL_STORAGE }))
        .setNeutralButton(activity.getResources().getString(R.string.dialog_permission_cancel),
                          null)
        .create()
        .show();
  }

}
