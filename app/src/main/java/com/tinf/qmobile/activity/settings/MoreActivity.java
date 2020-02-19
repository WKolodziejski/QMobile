package com.tinf.qmobile.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tinf.qmobile.R;
import com.tinf.qmobile.network.Client;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreActivity extends AppCompatActivity {
    @BindView(R.id.tinf_logo)       ImageView logo;
    @BindView(R.id.more_url)        TextView url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        ButterKnife.bind(this);

        logo.setOnLongClickListener(v -> {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            Objects.requireNonNull(vibrator).vibrate(1000);

            Toast toast = Toast.makeText(getApplicationContext(), "\ud83d\udc03", Toast.LENGTH_SHORT);
            ViewGroup group = (ViewGroup) toast.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(35);
            toast.show();

            return true;
        });

        url.setText(Client.get().getURL());

    }

}
