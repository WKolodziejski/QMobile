package com.tinf.qmobile.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.MessagesFragment;

public class MessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.messages_fragment, new MessagesFragment())
                .commit();
    }

}