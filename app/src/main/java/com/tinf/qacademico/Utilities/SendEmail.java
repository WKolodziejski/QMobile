package com.tinf.qacademico.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.tinf.qacademico.R;
import com.tinf.qacademico.WebView.SingletonWebView;

import java.util.List;

import static com.tinf.qacademico.Utilities.Utils.email_from;
import static com.tinf.qacademico.Utilities.Utils.email_from_pwd;
import static com.tinf.qacademico.Utilities.Utils.email_to;

public class SendEmail {

    public static void openGmail(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException ignored) {}
        String[] TO = {email_to};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QAcadMobile| " + context.getResources().getString(R.string.email_assunto_bug));
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        final PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") ||
                    info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        context.startActivity(emailIntent);
    }

    /*public static void bugReport(Context context, int id) {
        if (Utils.isConnected(context)) {

            SingletonWebView mainWebView = SingletonWebView.getInstance();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View theView = inflater.inflate(R.layout.dialog_bug, null);
            EditText message = (EditText) theView.findViewById(R.id.bug_message);
            CheckBox check_boletim = (CheckBox) theView.findViewById(R.id.bug_check_boletim);
            CheckBox check_diarios = (CheckBox) theView.findViewById(R.id.bug_check_diarios);
            CheckBox check_horario = (CheckBox) theView.findViewById(R.id.bug_check_horario);
            CheckBox check_outro = (CheckBox) theView.findViewById(R.id.bug_check_outro);
            GridLayout grid_boletim = (GridLayout) theView.findViewById(R.id.bug_grid_boletim);
            GridLayout grid_diarios = (GridLayout) theView.findViewById(R.id.bug_grid_diarios);
            GridLayout grid_horario = (GridLayout) theView.findViewById(R.id.bug_grid_horario);
            GridLayout grid_outro = (GridLayout) theView.findViewById(R.id.bug_grid_outro);
            ImageView img_boletim = (ImageView) theView.findViewById(R.id.bug_img_boletim);
            ImageView img_diarios = (ImageView) theView.findViewById(R.id.bug_img_diarios);
            ImageView img_horario = (ImageView) theView.findViewById(R.id.bug_img_horario);
            ImageView img_outros = (ImageView) theView.findViewById(R.id.bug_img_outros);
            TextView txt_boletim = (TextView) theView.findViewById(R.id.bug_txt_boletim);
            TextView txt_diarios = (TextView) theView.findViewById(R.id.bug_txt_diarios);
            TextView txt_horario = (TextView) theView.findViewById(R.id.bug_txt_horario);
            TextView txt_outros = (TextView) theView.findViewById(R.id.bug_txt_outros);

            if (id == R.id.navigation_notas) {
                check_diarios.setChecked(true);
                check_diarios.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    check_diarios.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.ok)));
                    img_diarios.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.ok)));
                    txt_diarios.setTextColor(context.getResources().getColor(R.color.ok));
                }
            } else {
                checkBoxBugReport(context, grid_diarios, check_diarios, img_diarios, txt_diarios);
            }

            if (id == R.id.navigation_notas) {
                check_boletim.setChecked(true);
                check_boletim.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    check_boletim.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.ok)));
                    img_boletim.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.ok)));
                    txt_boletim.setTextColor(context.getResources().getColor(R.color.ok));
                }
            } else {
                checkBoxBugReport(context, grid_boletim, check_boletim, img_boletim, txt_boletim);
            }

            if (id == R.id.navigation_horario) {
                check_horario.setChecked(true);
                check_horario.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    check_horario.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.ok)));
                    img_horario.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.ok)));
                    txt_horario.setTextColor(context.getResources().getColor(R.color.ok));
                }
            } else {
                checkBoxBugReport(context, grid_horario, check_horario, img_horario, txt_horario);
            }

            checkBoxBugReport(context, grid_outro, check_outro, img_outros, txt_outros);

            new AlertDialog.Builder(context).setView(theView)
                    .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_bug_report_black_24dp, R.string.email_assunto_bug, R.color.colorPrimary))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                        String message_final = "";

                        if (!message.getText().toString().equals("")) {

                            if (check_boletim.isChecked() && !mainWebView.bugBoletim.equals("")) {
                                message_final += "\n---------------------------------------------------------------------------------------------------";
                                message_final += "BOLETIM";
                                message_final += "---------------------------------------------------------------------------------------------------\n";
                                message_final += mainWebView.bugBoletim;
                            }

                            if (check_diarios.isChecked() && !mainWebView.bugDiarios.equals("")) {
                                message_final += "\n---------------------------------------------------------------------------------------------------";
                                message_final += "EXPANDABLE_LIST";
                                message_final += "---------------------------------------------------------------------------------------------------\n";
                                message_final += mainWebView.bugDiarios;
                            }

                            if (check_horario.isChecked() && !mainWebView.bugBoletim.equals("")) {
                                message_final += "\n---------------------------------------------------------------------------------------------------";
                                message_final += "HORARIO";
                                message_final += "---------------------------------------------------------------------------------------------------\n";
                                message_final += mainWebView.bugHorario;
                            }

                            if (!message_final.equals("") || !message.getText().toString().equals("")) {
                                emailPattern(context, "QAcadMobile Bug Report", message.getText().toString() + message_final);
                            } else {
                                new AlertDialog.Builder(context)
                                        .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_sync_problem_black_24dp, R.string.error_title, R.color.warning))
                                        .setMessage(R.string.page_load_empty)
                                        .setPositiveButton(R.string.dialog_close, null)
                                        .show();
                            }
                        } else {
                            new AlertDialog.Builder(context)
                                    .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_sentiment_neutral_black_24dp, R.string.error_title_oops, R.color.sad))
                                    .setMessage(R.string.email_empty)
                                    .setPositiveButton(R.string.dialog_close, null)
                                    .show();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
        }
    }*/

    public static void sendSuggestion(Context context) {
        if (Utils.isConnected(context)) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View theView = inflater.inflate(R.layout.dialog_sug, null);
            EditText message = (EditText) theView.findViewById(R.id.email_message);
            RatingBar rating = (RatingBar) theView.findViewById(R.id.ratingBar);

            new AlertDialog.Builder(context).setView(theView)
                    .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_chat_black_24dp, R.string.email_assunto_sug, R.color.sug_dialog))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                        if (!message.getText().toString().equals("")) {
                            emailPattern(context, "QAcadMobile Sugestion", message.getText().toString() + "\n\nNota: " + String.valueOf(rating.getRating()));
                        } else {
                            new AlertDialog.Builder(context)
                                    .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_sentiment_neutral_black_24dp, R.string.error_title_oops, R.color.warning))
                                    .setMessage(R.string.email_empty)
                                    .setPositiveButton(R.string.dialog_close, null)
                                    .show();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private static void checkBoxBugReport(Context context, GridLayout layout, CheckBox chk, ImageView img, TextView txt) {
        layout.setOnClickListener(v -> {
            if (chk.isChecked()) {
                chk.setChecked(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    chk.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                    img.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                    txt.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }
            } else {
                chk.setChecked(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    chk.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.ok)));
                    img.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.ok)));
                    txt.setTextColor(context.getResources().getColor(R.color.ok));
                }
            }
        });

        chk.setOnClickListener(v -> {
            if (chk.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    chk.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.ok)));
                    img.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.ok)));
                    txt.setTextColor(context.getResources().getColor(R.color.ok));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    chk.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                    img.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                    txt.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }
            }
        });
    }

    private static void emailPattern(Context context, String subject, String message) {
        BackgroundMail.newBuilder(context)
                .withUsername(email_from)
                .withPassword(email_from_pwd)
                .withMailto(email_to)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject(subject)
                .withBody(message)
                .withSendingMessage(R.string.email_sending)
                .withSendingMessageError(null)
                .withSendingMessageSuccess(null)
                .withOnSuccessCallback(() -> new AlertDialog.Builder(context)
                        .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_sentiment_very_satisfied_black_24dp, R.string.success_title, R.color.happy))
                        .setMessage(R.string.email_success)
                        .setPositiveButton(R.string.dialog_close, null)
                        .show())
                .withOnFailCallback(() -> new AlertDialog.Builder(context)
                        .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_cancel_black_24dp, R.string.error_title, R.color.error))
                        .setMessage(R.string.email_error)
                        .setPositiveButton(R.string.dialog_close, null)
                        .show())
                .send();
    }
}
