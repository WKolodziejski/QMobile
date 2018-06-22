package com.qacademico.qacademico.Utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.qacademico.qacademico.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.File;
import java.io.IOException;

import static com.qacademico.qacademico.Utilities.Utils.download_update_url;

public class CheckUpdate {
    private static float verLocal = 0;
    private static float verWeb = 0;
    private static String linkAtt = "";

    public static void updateApp(Activity activity, boolean showNotFound) {

        Context context = activity.getApplicationContext();

        if (Utils.isConnected(context)) {

            if (!CheckUpdate.checkUpdate(context).equals("")) {
                new AlertDialog.Builder(context)
                        .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_update_black_24dp, R.string.dialog_att_title, R.color.update_dialog))
                        .setMessage(String.format(context.getResources().getString(R.string.dialog_att_encontrada), "" + CheckUpdate.verLocal, "" + CheckUpdate.verWeb))
                        .setPositiveButton(R.string.dialog_att_download, (dialog, which) -> {
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ContextCompat.checkSelfPermission(context,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(activity,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                }
                            }
                            CheckUpdate.startDownload(context, CheckUpdate.checkUpdate(context));
                            /*if (PackageManager.PERMISSION_GRANTED == 0 && Build.VERSION.SDK_INT >= 23) {
                                CheckUpdate.startDownload(context, CheckUpdate.checkUpdate(context));
                            } else {
                                CheckUpdate.startDownload(context, CheckUpdate.checkUpdate(context));
                            }*/
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show();
            } else {
                if (showNotFound) {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_nenhuma_atualizacao), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public static String checkUpdate(Context context) {
        String version = "";
        try {
            PackageInfo pInfo = (context.getPackageManager().getPackageInfo(context.getPackageName(), 0));
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return getWebUpdate(Float.parseFloat(version));
    }

    private static String getWebUpdate(float verLocal) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(download_update_url).get();
                    String verAtt = doc.getElementsByTag("a").last().text();
                    Log.v("VERATT", verAtt);
                    linkAtt = doc.getElementsByTag("a").last().attr("abs:href");
                    if (!verAtt.equals("") && verAtt.contains("Mobile")) {
                        verAtt = verAtt.substring((verAtt.indexOf("Mobile ") + 7), (verAtt.indexOf(".apk")));
                        verWeb = Float.parseFloat(verAtt);
                        Log.v("UPDATEAPP", "web: " + verWeb + " atual: " + verLocal + "  " + linkAtt);
                    }
                } catch (IOException e) {
                    Log.v("UPDATEAPP", "erro");
                }
            }
        }.start();
        return ((verLocal < verWeb) ? linkAtt : "");
    }

    public static void startDownload(Context context, String linkAtt) {
        try {
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            String fileName = "app-att.apk";
            destination += fileName;
            final Uri uri = Uri.parse("file://" + destination);

            File file = new File(destination);
            file.mkdirs();

            File outputFile = new File(file, "app-att.apk");

            if (outputFile.exists()) {
                outputFile.delete();
            }

            DownloadManager mManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request mRqRequest = new DownloadManager.Request(Uri.parse(linkAtt));
            mRqRequest.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            );
            //mRqRequest.setDescription(getResources().getString(R.string.download_description));
            mRqRequest.setDescription("Qacademico Mobile");
            mRqRequest.setTitle("Qacademico Update");
            mRqRequest.setDestinationUri(uri);
            long downloadId = mManager.enqueue(mRqRequest);

            new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(uri,
                            mManager.getMimeTypeForDownloadedFile(downloadId));
                    context.startActivity(install);
                    context.unregisterReceiver(this);
                }
            };
            //registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            Toast.makeText(context, context.getResources().getString(R.string.text_download_start), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.text_download_fail), Toast.LENGTH_SHORT).show();
        }
    }
}
