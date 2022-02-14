package com.tinf.qmobile.fragment.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventCreateActivity;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentViewScheduleBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Schedule;
import org.threeten.bp.format.TextStyle;
import java.util.Locale;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import me.jlurena.revolvingweekview.DayTime;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;

public class ScheduleViewFragment extends Fragment {
    private FragmentViewScheduleBinding binding;
    private DataSubscription sub1, sub2;
    private long id;
    private boolean lookup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();

        if (bundle != null) {
            id = bundle.getLong("ID");
            lookup = bundle.getBoolean("LOOKUP", false);
        }

        DataObserver observer = data -> setText();

        sub1 = DataBase.get().getBoxStore().subscribe(Schedule.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_schedule, container, false);
        binding = FragmentViewScheduleBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setText();
    }

    private void setText() {
        Schedule schedule = DataBase.get().getBoxStore().boxFor(Schedule.class).get(id);

        if (schedule == null) {
            return;
        }

        DayTime start = schedule.getStartTime();
        DayTime end = schedule.getEndTime();

        binding.title.setText(schedule.getTitle().isEmpty() ? getString(R.string.event_no_title) : schedule.getTitle());
        binding.startTime.setText(start.getDay().getDisplayName(TextStyle.FULL, Locale.getDefault()) + "・" + String.format("%02d:%02d", start.getHour(), start.getMinute()));
        binding.colorImg.setImageTintList(ColorStateList.valueOf(schedule.getColor()));

        if (schedule.getRoom().isEmpty()) {
            binding.roomLayout.setVisibility(GONE);
        } else {
            binding.roomLayout.setVisibility(VISIBLE);
            binding.room.setText(schedule.getRoom());
        }

        if (schedule.getDescription().isEmpty()) {
            binding.descriptionLayout.setVisibility(GONE);
        } else {
            binding.descriptionLayout.setVisibility(VISIBLE);
            binding.description.setText(schedule.getDescription());
        }

        if (schedule.getAlarm() == 0) {
            binding.notificationLayout.setVisibility(GONE);
        } else {
            String[] strings = new String[4];

            strings[0] = getString(R.string.event_no_alarm);
            strings[1] = getString(R.string.alarm_30min);
            strings[2] = getString(R.string.alarm_1h);
            strings[3] = getString(R.string.alarm_1d);

            binding.notificationLayout.setVisibility(VISIBLE);
            binding.notificationText.setText(strings[schedule.getDifference()]);
        }

        if (!end.equals(start)) {
            binding.startTime.append(" ー " + String.format( Locale.getDefault(), "%02d:%02d", end.getHour(), end.getMinute()));
        }

        if (schedule.getMatter().isEmpty()) {
            binding.matterText.setVisibility(GONE);
        } else {
            binding.matterText.setVisibility(VISIBLE);
            binding.matterText.setText(schedule.getMatter());
        }

        if (lookup && !schedule.matter.isNull()) {
            binding.headerLayout.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), MatterActivity.class);
                intent.putExtra("ID", schedule.matter.getTargetId());
                intent.putExtra("PAGE", SCHEDULE);
                getContext().startActivity(intent);
            });
        } else {
            binding.headerLayout.setOnClickListener(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_edit) {
            Intent intent = new Intent(getContext(), EventCreateActivity.class);
            intent.putExtra("TYPE", SCHEDULE);
            intent.putExtra("ID", id);
            startActivity(intent);
            return true;

        } else if (itemId == R.id.action_delete) {
            new MaterialAlertDialogBuilder(getActivity())
                    .setMessage(getString(R.string.dialog_delete_txt))
                    .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                        DataBase.get().getBoxStore().boxFor(Schedule.class).remove(id);
                        getActivity().finish();
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
    public void onDestroy() {
        super.onDestroy();
        sub1.cancel();
        sub2.cancel();
    }

}
