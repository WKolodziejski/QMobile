package com.tinf.qmobile.utility;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.MESSAGE;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.activity.settings.SplashActivity;

public class NotificationUtils {

  public static void debug(String text) {
    if (!BuildConfig.DEBUG)
      return;

    NotificationUtils.show("Debug", text, -1, 0,
                           new Intent(getContext(), SplashActivity.class));
  }

  public static void show(String title, String txt, int channelID, int id, Intent intent) {
    if (channelID == -1)
      return;

    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(getContext(), getChannelName(channelID));

    NotificationCompat.BigTextStyle text = new NotificationCompat.BigTextStyle();
    text.bigText(txt);
    text.setBigContentTitle(title);
    text.setSummaryText(getChannelName(channelID));

    builder.setSmallIcon(R.drawable.ic_launcher);
    builder.setLargeIcon(
        BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_launcher));
    builder.setPriority(Notification.PRIORITY_DEFAULT);
    builder.setContentTitle(title);
    builder.setContentText(txt);
    builder.setStyle(text);

    NotificationManager manager = (NotificationManager) getContext()
        .getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
      if (manager.getNotificationChannel(String.valueOf(channelID)) == null) {
        manager.createNotificationChannel(
            new NotificationChannel(String.valueOf(channelID), getChannelName(channelID),
                                    NotificationManager.IMPORTANCE_DEFAULT));
      }
    }

    builder.setChannelId(String.valueOf(channelID));
    builder.setAutoCancel(true);

    TaskStackBuilder stack = TaskStackBuilder.create(getContext());
    stack.addParentStack(MatterActivity.class);
    stack.addNextIntent(intent);

    PendingIntent pendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                  stack.getPendingIntent(id,
                                                         PendingIntent.FLAG_UPDATE_CURRENT |
                                                         PendingIntent.FLAG_IMMUTABLE) :
                                  stack.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

    builder.setContentIntent(pendingIntent);

    if (manager != null) {
      manager.notify(id, builder.build());
    }
  }

  private static String getChannelName(int id) {
    switch (id) {
      case -1:
        return "DEBUG";

      case 0:
        return getContext().getResources().getString(R.string.app_name);

      case JOURNAL:
        return getContext().getResources().getString(R.string.title_diarios);

      case SCHEDULE:
        return getContext().getResources().getString(R.string.title_horario);

      case MATERIAL:
        return getContext().getResources().getString(R.string.title_materiais);

      case MESSAGE:
        return getContext().getResources().getString(R.string.title_messages);

      case EVENT:
        return getContext().getResources().getString(R.string.title_calendario);
    }

    return getContext().getResources().getString(R.string.app_name);
  }

}
