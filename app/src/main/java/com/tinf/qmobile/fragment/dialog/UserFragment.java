package com.tinf.qmobile.fragment.dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.utility.User;

import java.io.File;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tinf.qmobile.App.getContext;

public class UserFragment extends DialogFragment {
    @BindView(R.id.user_image)      ImageView image;
    @BindView(R.id.user_name)       TextView name;
    @BindView(R.id.user_reg)        TextView reg;
    @BindView(R.id.user_logout)     MaterialCardView logout;
    private OnLogout onLogout;

    public UserFragment(OnLogout onLogout) {
        this.onLogout = onLogout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_user, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name.setText(User.getName());
        reg.setText(User.getCredential(User.REGISTRATION));

        logout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(getResources().getString(R.string.dialog_quit))
                    .setMessage(R.string.dialog_quit_msg)
                    .setPositiveButton(R.string.dialog_quit, (dialog, which) -> onLogout.onLogout())
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .create()
                    .show();
        });

        Drawable picture = User.getProfilePicture(getContext());

        if (picture != null)
            image.setImageDrawable(picture);
    }

    @OnClick(R.id.user_policy)
    public void onPolicyClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/qmobileapp"));
        startActivity(intent);
    }

    public interface OnLogout {
        void onLogout();
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;
        params.y = Math.round(60 * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

        window.setAttributes(params);
    }

}
