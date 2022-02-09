package com.tinf.qmobile.fragment.create;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventCreateActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentCreateScheduleBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.service.AlarmReceiver;
import com.tinf.qmobile.utility.User;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.objectbox.Box;
import me.jlurena.revolvingweekview.DayTime;

import static android.content.Context.ALARM_SERVICE;
import static android.view.View.GONE;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;

public class ScheduleCreateFragment extends Fragment {
    private FragmentCreateScheduleBinding binding;
    private boolean isFromSite;
    private int color, matter, alarmDif;
    private List<Matter> matters;
    private String title, description, room;
    private long id, alarm, id2;
    private DayTime start, end;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Box<Matter> matterBox = DataBase.get().getBoxStore().boxFor(Matter.class);

        matters = matterBox.query()
                .equal(Matter_.year_, User.getYear(0)).and()
                .equal(Matter_.period_, User.getPeriod(0))
                .build().find();

        start = new DayTime(DayOfWeek.MONDAY, 12, 0);
        end = new DayTime(DayOfWeek.MONDAY, 13, 0);

        Bundle bundle = getArguments();

        if (bundle != null) {

            id = bundle.getLong("ID");
            id2 = bundle.getLong("ID2");

            if (id != 0) {

                Schedule schedule = DataBase.get().getBoxStore().boxFor(Schedule.class).get(id);

                if (schedule != null) {

                    isFromSite = schedule.isFromSite();
                    title = schedule.getTitle();
                    color = schedule.getColor();
                    description = schedule.getDescription();
                    room = schedule.getRoom();

                    start = schedule.getStartTime();
                    end = schedule.getEndTime();

                    alarm = schedule.getAlarm();
                    alarmDif = schedule.getDifference();

                    for (int i = 0; i < matters.size(); i++) {
                        if (matters.get(i).id == schedule.matter.getTargetId()) {
                            matter = i + 1;
                            break;
                        }
                    }
                } else {
                    getActivity().finish();
                }
            } else {
                color = getResources().getColor(R.color.colorPrimary);
            }

            if (id2 != 0) {
                color = DataBase.get().getBoxStore().boxFor(Matter.class).get(id2).getColor();

                for (int i = 0; i < matters.size(); i++) {
                    if (matters.get(i).id == id2) {
                        matter = i + 1;
                        break;
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_schedule, container, false);
        binding = FragmentCreateScheduleBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isFromSite) {
            binding.titleEdt.setEnabled(false);
        }

        binding.titleEdt.setText(title);
        binding.titleEdt.requestFocus();

        binding.descriptionEdt.setText(description);

        binding.roomEdt.setText(room);

        updateText();

        binding.startDay.setOnClickListener(view1 -> {
            String[] strings = new String[7];

            for (int i = 0; i < 7; i++) {
                strings[i] = DayOfWeek.values()[i].getDisplayName(TextStyle.FULL, Locale.getDefault());
            }

            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(getString(R.string.dialog_choose_day))
                    .setItems(strings, (dialog, which) -> {
                        start = new DayTime(which + 1, start.getHour(), start.getMinute());
                        updateText();
                    })
                    .create().show();
        });

        binding.startTime.setOnClickListener(view1 -> {

            TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, h, m) -> {
                start = new DayTime(start.getDayValue(), h, m);

                if (start.isAfter(end)) {
                    end = new DayTime(start);
                }

                updateText();

            }, start.getHour(), start.getMinute(), true);

            dialog.show();
        });

        binding.endTime.setOnClickListener(view1 -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, h, m) -> {
                end = new DayTime(start.getDayValue(), h, m);

                if (end.isBefore(start)) {
                    start = new DayTime(end);
                }

                updateText();

            }, end.getHour(), end.getMinute(), true);

            dialog.show();
        });

        if (!isFromSite) {

            binding.colorLayout.setOnClickListener(v -> ColorPickerDialogBuilder
                    .with(getContext())
                    .setTitle(getString(R.string.dialog_choose_color))
                    .initialColor(color)
                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(6)
                    .lightnessSliderOnly()
                    .setPositiveButton(getString(R.string.dialog_select), (dialog, selectedColor, allColors) -> {
                        color = selectedColor;
                        updateText();
                    })
                    .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> {
                    })
                    .build()
                    .show());

            binding.matterLayout.setOnClickListener(v -> {
                String[] strings = new String[matters.size() + 1];

                strings[0] = getString(R.string.event_none);

                for (int i = 0; i < matters.size(); i++) {
                    strings[i + 1] = matters.get(i).getTitle();
                }

                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getString(R.string.dialog_choose_matter))
                        .setItems(strings, (dialog, which) -> {
                            matter = which;
                            if (which > 0) {
                                color = matters.get(which - 1).getColor();
                                binding.colorLayout.setClickable(false);
                            } else {
                                binding.colorLayout.setClickable(true);
                            }
                            updateText();
                        })
                        .create().show();
            });

            binding.alarmLayout.setOnClickListener(v -> {
                String[] strings = new String[4];

                strings[0] = getString(R.string.event_no_alarm);
                strings[1] = getString(R.string.alarm_30min);
                strings[2] = getString(R.string.alarm_1h);
                strings[3] = getString(R.string.alarm_1d);

                new MaterialAlertDialogBuilder(getContext())
                        .setItems(strings, (dialog, which) -> {
                            alarmDif = which;
                            updateText();
                        })
                        .create().show();
            });
        } else {
            binding.matterLayout.setVisibility(GONE);
            binding.colorLayout.setVisibility(GONE);
            binding.alarmLayout.setVisibility(GONE);
            binding.alarmDecoration.setVisibility(GONE);
            binding.matterDecoration.setVisibility(GONE);
            binding.colorDecoration.setVisibility(GONE);
        }

        ((EventCreateActivity) getActivity()).binding.add.setOnClickListener(v -> {
            end = new DayTime(start.getDayValue(), end.getHour(), end.getMinute());

            Calendar alarmTime = Calendar.getInstance();
            alarmTime.set(Calendar.DAY_OF_WEEK, start.getDayValue() + 1);
            alarmTime.set(Calendar.HOUR_OF_DAY, start.getHour());
            alarmTime.set(Calendar.MINUTE, start.getMinute());
            alarmTime.set(Calendar.SECOND, 0);
            alarmTime.set(Calendar.MILLISECOND, 0);

            switch (alarmDif) {

                case 0:
                    alarm = 0;
                    break;

                case 1:
                    alarmTime.add(Calendar.MINUTE, -30);
                    alarm = alarmTime.getTimeInMillis();
                    break;

                case 2:
                    alarmTime.add(Calendar.HOUR_OF_DAY, -1);
                    alarm = alarmTime.getTimeInMillis();
                    break;

                case 3:
                    alarmTime.add(Calendar.DAY_OF_WEEK, -1);
                    alarm = alarmTime.getTimeInMillis();
                    break;
            }

            Schedule schedule = new Schedule(binding.titleEdt.getText().toString().trim(), start, end, alarmDif, User.getYear(0), User.getPeriod(0), isFromSite);

            if (id != 0) {
                schedule.id = id;
            }

            schedule.setDescription(binding.descriptionEdt.getText().toString().trim());
            schedule.setColor(color);
            schedule.setAlarm(alarm);
            schedule.setRoom(binding.roomEdt.getText().toString().trim());

            if (matter > 0) {
                schedule.matter.setTarget(matters.get(matter - 1));
            }

            Toast.makeText(getContext(), getString(id == 0 ? R.string.event_added : R.string.event_edited), Toast.LENGTH_SHORT).show();

            Box<Schedule> scheduleBox = DataBase.get().getBoxStore().boxFor(Schedule.class);
            id = scheduleBox.put(schedule);

            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            intent.putExtra("ID", id);
            intent.putExtra("TYPE", SCHEDULE);

            PendingIntent pendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                    PendingIntent.getBroadcast(getContext(), (int) id, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE) :
                    PendingIntent.getBroadcast(getContext(), (int) id, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                if (alarmDif != 0) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, schedule.getAlarm(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                } else {
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                }
            }

            getActivity().finish();
        });
    }

    private void updateText() {
        binding.startDay.setText(start.getDay().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        binding.startTime.setText(String.format(Locale.getDefault(), "%02d:%02d", start.getHour(), start.getMinute()));
        binding.endTime.setText(String.format(Locale.getDefault(), "%02d:%02d", end.getHour(), end.getMinute()));
        binding.colorImg.setImageTintList(ColorStateList.valueOf(color));

        String[] alarms = new String[4];

        alarms[0] = getString(R.string.event_no_alarm);
        alarms[1] = getString(R.string.alarm_30min);
        alarms[2] = getString(R.string.alarm_1h);
        alarms[3] = getString(R.string.alarm_1d);

        binding.alarmText.setText(alarmDif > 0 ? alarms[alarmDif] : "");

        if (matter > 0) {
            binding.matterText.setText(matters.get(matter - 1).getTitle());

            binding.colorText.setText(color == matters.get(matter - 1).getColor() ?
                    matters.get(matter - 1).getTitle() : getString(R.string.event_custom_color));
        } else {
            binding.matterText.setText("");

            binding.colorText.setText(color == getResources().getColor(R.color.colorPrimary) ?
                    getString(R.string.event_default_color) : getString(R.string.event_custom_color));
        }
    }

}
