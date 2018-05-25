package com.qacademico.qacademico.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.Objects;

import static com.qacademico.qacademico.Utilities.Utils.pg_change_password;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class ChangePassword {

    public static void changePassword(Context context) {

        SingletonWebView mainWebView = SingletonWebView.getInstance();

        if (Utils.isConnected(context) && mainWebView.pg_home_loaded) {

            SharedPreferences login_info = context.getSharedPreferences("login_info", 0);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View theView = Objects.requireNonNull(inflater).inflate(R.layout.dialog_password_change, null);
            TextInputEditText pass_atual = (TextInputEditText) theView.findViewById(R.id.pass_atual);
            TextInputEditText pass_nova = (TextInputEditText) theView.findViewById(R.id.pass_nova);
            TextInputEditText pass_nova_confirm = (TextInputEditText) theView.findViewById(R.id.pass_nova_confirm);
            TextInputLayout pass_atual_ly = (TextInputLayout) theView.findViewById(R.id.pass_atual_ly);
            ImageView img = (ImageView) theView.findViewById(R.id.pass_img);
            TextView txt = (TextView) theView.findViewById(R.id.pass_txt);

            pass_atual.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                        pass_atual_ly.setErrorEnabled(true);
                        pass_atual_ly.setError("teste");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                        pass_atual_ly.setErrorEnabled(false);
                    }
                    if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8
                            && pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                        img.setImageResource(R.drawable.ic_done_all_black_24dp);
                        txt.setText(R.string.passchange_txt_equals);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            img.setImageTintList(ColorStateList.valueOf((context.getResources().getColor(R.color.ok))));
                            txt.setTextColor(context.getResources().getColor(R.color.ok));
                        }
                    } else if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8) {
                        img.setImageResource(R.drawable.ic_check_black_24dp);
                        txt.setText(R.string.passchange_txt_old_equals);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            img.setImageTintList(ColorStateList.valueOf((context.getResources().getColor(R.color.check))));
                            txt.setTextColor(context.getResources().getColor(R.color.check));
                        }
                    }
                }
            });

            passwordCheck(context, login_info, pass_nova, pass_atual, pass_nova, pass_nova_confirm, pass_atual_ly, img, txt);
            passwordCheck(context, login_info, pass_nova_confirm, pass_atual, pass_nova, pass_nova_confirm, pass_atual_ly, img, txt);

            new AlertDialog.Builder(context).setView(theView)
                    .setTitle(R.string.menu_password)
                    .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_lock_outline_black_24dp, R.string.menu_password, R.color.password_dialog))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {
                        if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8
                                && pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                            mainWebView.new_password = pass_nova.getText().toString();
                            //showProgressDialog();
                            mainWebView.html.loadUrl(url + pg_change_password);
                        } else {
                            new AlertDialog.Builder(context)
                                    .setCustomTitle(Utils.customAlertTitle(context, R.drawable.ic_cancel_black_24dp, R.string.error_title, R.color.error))
                                    .setMessage(R.string.passchange_txt_error_message)
                                    .setPositiveButton(R.string.dialog_close, null)
                                    .show();
                        }
                    }).setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.text_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private static void passwordCheck(Context context, SharedPreferences login_info, TextInputEditText obj,
                                        TextInputEditText pass_atual, TextInputEditText pass_nova, TextInputEditText pass_nova_confirm,
                                        TextInputLayout pass_atual_ly, ImageView img, TextView txt) { //Checa os campos para alterar a senha

        obj.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                    pass_atual_ly.setErrorEnabled(true);
                }
                if (pass_nova.getText().toString().equals("") || pass_nova_confirm.getText().toString().equals("")) {
                    img.setImageResource(R.drawable.ic_edit_black_24dp);
                    txt.setText(R.string.passchange_txt_empty);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageTintList(ColorStateList.valueOf((context.getResources().getColor(R.color.error))));
                        txt.setTextColor(context.getResources().getColor(R.color.error));
                    }
                } else if (!pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString())) {
                    img.setImageResource(R.drawable.ic_cancel_black_24dp);
                    txt.setText(R.string.passchange_txt_different);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageTintList(ColorStateList.valueOf((context.getResources().getColor(R.color.error))));
                        txt.setTextColor(context.getResources().getColor(R.color.error));
                    }
                } else if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && count < 8) {
                    img.setImageResource(R.drawable.ic_short_text_black_24dp);
                    txt.setText(R.string.passchange_txt_short);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageTintList(ColorStateList.valueOf((context.getResources().getColor(R.color.warning))));
                        txt.setTextColor(context.getResources().getColor(R.color.warning));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                    pass_atual_ly.setErrorEnabled(false);
                }
                if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8 && pass_atual.getText().toString().equals(login_info.getString("password", ""))) {
                    img.setImageResource(R.drawable.ic_done_all_black_24dp);
                    txt.setText(R.string.passchange_txt_equals);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageTintList(ColorStateList.valueOf((context.getResources().getColor(R.color.ok))));
                        txt.setTextColor(context.getResources().getColor(R.color.ok));
                    }
                } else if (pass_nova.getText().toString().equals(pass_nova_confirm.getText().toString()) && pass_nova.getText().length() >= 8) {
                    img.setImageResource(R.drawable.ic_check_black_24dp);
                    txt.setText(R.string.passchange_txt_old_equals);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageTintList(ColorStateList.valueOf((context.getResources().getColor(R.color.check))));
                        txt.setTextColor(context.getResources().getColor(R.color.check));
                    }
                }
            }
        });
    }
}
