package com.tinf.qmobile.Fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.Activity.Calendar.CalendarioActivity;
import com.tinf.qmobile.Activity.Calendar.EventCreateActivity;
import com.tinf.qmobile.Activity.EventViewActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.EventUser;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import static android.view.View.GONE;
import static com.tinf.qmobile.Activity.Calendar.EventCreateActivity.EVENT;

public class EventViewFragment extends Fragment implements OnUpdate {
    @BindView(R.id.event_view_start_time)          TextView startTime_txt;
    @BindView(R.id.event_view_end_time)            TextView endTime_txt;
    @BindView(R.id.event_view_matter_text)         TextView matter_txt;
    @BindView(R.id.event_view_description)         TextView description_txt;
    @BindView(R.id.event_view_title)               TextView title_txt;
    @BindView(R.id.event_view_notification_text)   TextView notification_txt;
    @BindView(R.id.event_view_color_img)           ImageView color_img;
    @BindView(R.id.event_view_description_layout)  LinearLayout description_layout;
    @BindView(R.id.event_view_notification_layout) LinearLayout notification_layout;
    private long id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            id = bundle.getLong("ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        setText();
    }

    private void setText() {
        SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy ・ HH:mm", Locale.getDefault());

        EventUser event = App.getBox().boxFor(EventUser.class).get(id);

        title_txt.setText(event.getTitle().isEmpty() ? getString(R.string.event_no_title) : event.getTitle());
        startTime_txt.setText(date.format(event.getStartTime()));
        color_img.setImageTintList(ColorStateList.valueOf(event.getColor()));

        if (event.getDescription().isEmpty()) {
            description_layout.setVisibility(GONE);
        } else {
            description_layout.setVisibility(View.VISIBLE);
            description_txt.setText(event.getDescription());
        }

        if (event.getAlarmDifference().isEmpty()) {
            notification_layout.setVisibility(GONE);
        } else {
            notification_layout.setVisibility(View.VISIBLE);
            notification_txt.setText(event.getAlarmDifference());
        }

        if (event.getEndTime() == 0) {
            endTime_txt.setVisibility(GONE);
        } else {
            endTime_txt.setVisibility(View.VISIBLE);
            endTime_txt.setText(date.format(event.getEndTime()));
        }

        if (event.getMatter().isEmpty()) {
            matter_txt.setVisibility(GONE);
        } else {
            matter_txt.setVisibility(View.VISIBLE);
            matter_txt.setText(event.getMatter());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_edit:

                Intent intent = new Intent(getContext(), EventCreateActivity.class);
                intent.putExtra("TYPE", EVENT);
                intent.putExtra("ID", id);
                startActivity(intent);
                return true;

            case R.id.action_delete:

                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.dialog_delete_txt))
                        .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                            App.getBox().boxFor(EventUser.class).remove(id);
                            ((EventViewActivity) getActivity()).finish();
                            Client.get().requestUpdate();
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdate(int pg) {
        setText();
    }

    @Override
    public void onScrollRequest() {

    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Client.get().removeOnUpdateListener(this);
    }

}
