package com.tinf.qmobile.fragment.create;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
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
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentCreateEventBinding;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.service.AlarmReceiver;
import com.tinf.qmobile.service.Works;
import com.tinf.qmobile.utility.User;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import io.objectbox.Box;
import static android.content.Context.ALARM_SERVICE;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.USER;

public class EventCreateFragment extends Fragment {
    private FragmentCreateEventBinding binding;
    private Calendar start, end;
    private boolean isRanged;
    private int color, matter, alarmDif;
    private List<Matter> matters;
    private String title, description;
    private long id, alarm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Box<Matter> matterBox = DataBase.get().getBoxStore().boxFor(Matter.class);

        matters = matterBox.query()
                .equal(Matter_.year_, User.getYear(0)).and()
                .equal(Matter_.period_, User.getPeriod(0))
                .build().find();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        start = (Calendar) calendar.clone();
        end = (Calendar) calendar.clone();
        end.add(Calendar.HOUR_OF_DAY, 1);

        Bundle bundle = getArguments();

        if (bundle != null) {

            id = bundle.getLong("ID");

            if (id != 0) {

                EventUser event = DataBase.get().getBoxStore().boxFor(EventUser.class).get(id);

                if (event != null) {

                    title = event.getTitle();
                    color = event.getColor();
                    description = event.getDescription();

                    start.setTimeInMillis(event.getStartTime());

                    isRanged = event.isRanged();

                    alarm = event.getAlarm();
                    alarmDif = event.getDifference();

                    if (isRanged)
                        end.setTimeInMillis(event.getEndTime());
                    else
                        end.setTimeInMillis(event.getStartTime());

                    for (int i = 0; i < matters.size(); i++) {
                        if (matters.get(i).id == event.matter.getTargetId()) {
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
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        binding = FragmentCreateEventBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.titleEdt.setText(title);
        binding.titleEdt.requestFocus();

        binding.descriptionEdt.setText(description);

        updateText();

        binding.startDay.setOnClickListener(view1 -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext(), (datePicker, y, m, d) -> {
                start.set(Calendar.YEAR, y);
                start.set(Calendar.MONTH, m);
                start.set(Calendar.DAY_OF_MONTH, d);

                if (!isRanged || end.getTimeInMillis() < start.getTimeInMillis()) {
                    end.set(Calendar.YEAR, y);
                    end.set(Calendar.MONTH, m);
                    end.set(Calendar.DAY_OF_MONTH, d);
                }

                updateText();
            }, start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));

            dialog.show();

        });

        binding.startTime.setOnClickListener(view1 -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, h, m) -> {
                start.set(Calendar.HOUR_OF_DAY, h);
                start.set(Calendar.MINUTE, m);

                if (!isRanged || end.getTimeInMillis() < start.getTimeInMillis()) {
                    end.set(Calendar.HOUR_OF_DAY, h);
                    end.set(Calendar.MINUTE, m);
                }

                updateText();
            }, start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE), true);

            dialog.show();
        });

        binding.endDay.setOnClickListener(view1 -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext(), (datePicker, y, m, d) -> {
                Calendar temp = (Calendar) end.clone();
                temp.set(Calendar.YEAR, y);
                temp.set(Calendar.MONTH, m);
                temp.set(Calendar.DAY_OF_MONTH, d);

                if (temp.getTimeInMillis() >= start.getTimeInMillis()) {
                    end.set(Calendar.YEAR, y);
                    end.set(Calendar.MONTH, m);
                    end.set(Calendar.DAY_OF_MONTH, d);
                    isRanged = true;
                }

                updateText();
            }, end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH));

            dialog.show();
        });

        binding.endTime.setOnClickListener(view1 -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, h, m) -> {
                Calendar temp = (Calendar) end.clone();
                temp.set(Calendar.HOUR_OF_DAY, h);
                temp.set(Calendar.MINUTE, m);

                if (temp.getTimeInMillis() >= start.getTimeInMillis()) {
                    end.set(Calendar.HOUR_OF_DAY, h);
                    end.set(Calendar.MINUTE, m);
                    isRanged = true;
                }

                updateText();
            }, end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE), true);

            dialog.show();
        });

        binding.colorLayout.setOnClickListener(v -> {
            ColorPickerDialogBuilder
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
                    .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> {})
                    .build()
                    .show();
        });

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

        ((EventCreateActivity) getActivity()).binding.add.setOnClickListener(v -> {

            if (end.getTimeInMillis() == start.getTimeInMillis()) {
                end.setTimeInMillis(0);
            }

            Calendar alarmTime = (Calendar) start.clone();

            switch (alarmDif) {

                case 0: alarm = 0;
                    break;

                case 1: alarmTime.add(Calendar.MINUTE, -30);
                    alarm = alarmTime.getTimeInMillis();
                    break;

                case 2: alarmTime.add(Calendar.HOUR_OF_DAY, -1);
                    alarm = alarmTime.getTimeInMillis();
                    break;

                case 3: alarmTime.add(Calendar.DAY_OF_MONTH, -1);
                    alarm = alarmTime.getTimeInMillis();
                    break;
            }

            EventUser event = new EventUser(binding.titleEdt.getText().toString().trim(),
                    start.getTimeInMillis(), end.getTimeInMillis(), alarm, alarmDif);

            if (id != 0) {
                event.id = id;
            }

            event.setDescription(binding.descriptionEdt.getText().toString().trim());
            event.setColor(color);

            if (matter > 0) {
                event.matter.setTarget(matters.get(matter - 1));
            }

            Toast.makeText(getContext(), getString(id == 0 ? R.string.event_added : R.string.event_edited), Toast.LENGTH_SHORT).show();

            Box<EventUser> eventBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
            id = eventBox.put(event);

            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            intent.putExtra("ID", id);
            intent.putExtra("TYPE", EVENT);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int) id, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                if (alarmDif != 0) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.getAlarm(), pendingIntent);
                } else {
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                }
            }

            getActivity().finish();
        });
    }

    private void updateText() {
        SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.getDefault());

        binding.startDay.setText(date.format(start.getTimeInMillis()));
        binding.startTime.setText(time.format(start.getTimeInMillis()));
        binding.endDay.setText(date.format(end.getTimeInMillis()));
        binding.endTime.setText(time.format(end.getTimeInMillis()));
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
