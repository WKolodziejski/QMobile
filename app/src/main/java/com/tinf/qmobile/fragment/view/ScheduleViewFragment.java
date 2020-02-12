package com.tinf.qmobile.fragment.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.activity.calendar.EventCreateActivity;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.network.Client;

import org.threeten.bp.format.TextStyle;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jlurena.revolvingweekview.DayTime;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.activity.calendar.EventCreateActivity.SCHEDULE;

public class ScheduleViewFragment extends Fragment implements OnUpdate {
    @BindView(R.id.schedule_view_start_time)          TextView date_txt;
    @BindView(R.id.schedule_view_matter_text)         TextView matter_txt;
    @BindView(R.id.schedule_view_description)         TextView description_txt;
    @BindView(R.id.schedule_view_title)               TextView title_txt;
    @BindView(R.id.schedule_view_room)                TextView room_txt;
    @BindView(R.id.schedule_view_notification_text)   TextView notification_txt;
    @BindView(R.id.schedule_view_color_img)           ImageView color_img;
    @BindView(R.id.schedule_view_description_layout)  LinearLayout description_layout;
    @BindView(R.id.schedule_view_notification_layout) LinearLayout notification_layout;
    @BindView(R.id.schedule_view_room_layout)         LinearLayout room_layout;
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
        View view = inflater.inflate(R.layout.fragment_view_schedule, container, false);
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
        Schedule schedule = DataBase.get().getBoxStore().boxFor(Schedule.class).get(id);

        DayTime start = schedule.getStartTime();
        DayTime end = schedule.getEndTime();

        title_txt.setText(schedule.getTitle().isEmpty() ? getString(R.string.event_no_title) : schedule.getTitle());
        date_txt.setText(start.getDay().getDisplayName(TextStyle.FULL, Locale.getDefault()) + "・" + String.format("%02d:%02d", start.getHour(), start.getMinute()));
        color_img.setImageTintList(ColorStateList.valueOf(schedule.getColor()));

        if (schedule.getRoom().isEmpty()) {
            room_layout.setVisibility(GONE);
        } else {
            room_layout.setVisibility(VISIBLE);
            room_txt.setText(schedule.getRoom());
        }

        if (schedule.getDescription().isEmpty()) {
            description_layout.setVisibility(GONE);
        } else {
            description_layout.setVisibility(VISIBLE);
            description_txt.setText(schedule.getDescription());
        }

        if (schedule.getAlarm() == 0) {
            notification_layout.setVisibility(GONE);
        } else {
            String[] strings = new String[4];

            strings[0] = getString(R.string.event_no_alarm);
            strings[1] = getString(R.string.alarm_30min);
            strings[2] = getString(R.string.alarm_1h);
            strings[3] = getString(R.string.alarm_1d);

            notification_layout.setVisibility(VISIBLE);
            notification_txt.setText(strings[schedule.getDifference()]);
        }

        if (!end.equals(start)) {
            date_txt.append(" ー " + String.format("%02d:%02d", end.getHour(), end.getMinute()));
        }

        if (schedule.getMatter().isEmpty()) {
            matter_txt.setVisibility(GONE);
        } else {
            matter_txt.setVisibility(VISIBLE);
            matter_txt.setText(schedule.getMatter());
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
                intent.putExtra("TYPE", SCHEDULE);
                intent.putExtra("ID", id);
                startActivity(intent);
                return true;

            case R.id.action_delete:

                new MaterialAlertDialogBuilder(getActivity())
                        .setMessage(getString(R.string.dialog_delete_txt))
                        .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                            DataBase.get().getBoxStore().boxFor(Schedule.class).remove(id);
                            Client.get().removeOnUpdateListener(this);
                            ((EventViewActivity) getActivity()).finish();
                            Client.get().requestUpdate();
                            Toast.makeText(getContext(), getString(R.string.event_removed), Toast.LENGTH_SHORT).show();
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