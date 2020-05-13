package com.tinf.qmobile.service;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.tinf.qmobile.R;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;
import java.io.File;
import java.net.URLConnection;
import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class DownloadReceiver extends BroadcastReceiver {
    public static String PATH = "QMobile/" + User.getCredential(REGISTRATION);
    public static String PATH_M = PATH + "/" + User.getYear(pos) + "/" + User.getPeriod(pos);
    private OnDownload onDownload;
    private final DownloadManager manager;
    public static long id;

    public DownloadReceiver(DownloadManager manager, OnDownload onDownload) {
        this.manager = manager;
        this.onDownload = onDownload;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = manager.query(query);

        if (cursor.moveToFirst()) {

            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
            int reason = cursor.getInt(columnReason);

            switch (status) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    onDownload.onDownload(id);

                    try {
                        Uri uri = manager.getUriForDownloadedFile(id);
                        openFile(uri);
                    } catch (Exception e) {
                        Toast.makeText(context,
                                "FAILED!\n" + "reason of " + reason,
                                Toast.LENGTH_LONG).show();
                    }

                    break;

                case DownloadManager.STATUS_FAILED:

                    Toast.makeText(context,
                            "FAILED!\n" + "reason of " + reason,
                            Toast.LENGTH_LONG).show();

                    break;
            }
        }

        id = 0;
    }

    private static void openFile(Uri uri) {
        Intent intent = new Intent(ACTION_VIEW);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, getContext().getContentResolver().getType(uri));

        try {
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText( getContext(),  getContext().getResources().getString(R.string.text_no_handler), Toast.LENGTH_LONG).show();
        }
    }

    public static void openFile(String path) {
        openFile(FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName(),
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        + "/" + path)));
    }

    public static long download(Context context, String url, String title, String path) {
        if (Client.isConnected()) {
            String destiny = PATH + "/";

            if (path != null)
                destiny += path + "/";

            destiny += title;

            Log.d("Download", destiny);

            DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            id = dm.enqueue(new DownloadManager.Request(Uri.parse(url))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverRoaming(false)
                    .setMimeType(URLConnection.guessContentTypeFromName(title))
                    .setTitle(title)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, destiny));

            Toast.makeText(context, context.getResources().getString(R.string.materiais_downloading), Toast.LENGTH_SHORT).show();

            return id;
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.client_no_connection), Toast.LENGTH_SHORT).show();

            return 0;
        }
    }

    public interface OnDownload {
        void onDownload(long id);
    }

}
