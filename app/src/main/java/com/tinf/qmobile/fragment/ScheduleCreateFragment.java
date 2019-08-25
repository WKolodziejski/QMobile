package com.tinf.qmobile.fragment;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.calendar.EventCreateActivity;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.AlarmReceiver;
import com.tinf.qmobile.utility.User;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import ca.antonious.materialdaypicker.MaterialDayPicker;
import ca.antonious.materialdaypicker.SelectionMode;
import ca.antonious.materialdaypicker.SelectionState;
import io.objectbox.Box;
import me.jlurena.revolvingweekview.DayTime;

import static android.content.Context.ALARM_SERVICE;

public class ScheduleCreateFragment extends Fragment {
    private static final String TAG = "EventCreateFragment";
    @BindView(R.id.schedule_create_start_day)     TextView startDate_txt;
    @BindView(R.id.schedule_create_start_time)    TextView startTime_txt;
    @BindView(R.id.schedule_create_end_time)      TextView endTime_txt;
    @BindView(R.id.schedule_create_color_text)    TextView color_txt;
    @BindView(R.id.schedule_create_matter_text)   TextView matter_txt;
    @BindView(R.id.schedule_create_alarm_text)    TextView alarm_txt;
    @BindView(R.id.schedule_create_description)   EditText description_edt;
    @BindView(R.id.schedule_create_title)         EditText title_edt;
    @BindView(R.id.schedule_create_color_layout)  LinearLayout color_btn;
    @BindView(R.id.schedule_create_matter_layout) LinearLayout matter_btn;
    @BindView(R.id.schedule_create_alarm_layout)  LinearLayout alarm_btn;
    @BindView(R.id.schedule_create_color_img)     ImageView color_img;
    private boolean isRanged;
    private int color, matter, alarmDif;
    private List<Matter> matters;
    private String title, description;
    private long id, alarm;
    private DayTime start, end;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Box<Matter> matterBox = App.getBox().boxFor(Matter.class);

        matters = matterBox.query()
                .equal(Matter_.year_, User.getYear(0)).and()
                .equal(Matter_.period_, User.getPeriod(0))
                .build().find();

        Bundle bundle = getArguments();

        if (bundle != null) {

            id = bundle.getLong("ID");

            if (id != 0) {

                Schedule schedule = App.getBox().boxFor(Schedule.class).get(id);

                if (schedule != null) {

                    title = schedule.getTitle();
                    color = schedule.getColor();
                    description = schedule.getDescription();

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
                    ((EventCreateActivity) getActivity()).finish();
                }
            } else {
                color = getResources().getColor(R.color.colorPrimary);

                Calendar calendar = Calendar.getInstance();
                start = new DayTime(calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                end = start;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_create, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title_edt.setText(title);
        title_edt.requestFocus();

        description_edt.setText(description);

        updateText();

        startDate_txt.setOnClickListener(view1 -> {
            String[] strings = new String[7];

            for (int i = 0; i < 7; i++) {
                strings[i] = DayOfWeek.values()[i].getDisplayName(TextStyle.FULL, Locale.getDefault());
            }

            new AlertDialog.Builder(getContext())
                    .setTitle("DIA")
                    .setItems(strings, (dialog, which) -> {
                        start = new DayTime(which + 1, start.getHour(), start.getMinute());
                        updateText();
                    })
                    .create().show();
        });

        startTime_txt.setOnClickListener(view1 -> {

            TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, h, m) -> {
                start = new DayTime(start.getDayValue(), h, m);

                if (start.isAfter(end)) {
                    end = start;
                }

                updateText();

            }, start.getHour(), start.getMinute(), true);

            dialog.show();
        });

        endTime_txt.setOnClickListener(view1 -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, h, m) -> {
                end = new DayTime(end.getDayValue(), h, m);

                if (end.isBefore(start)) {
                    start = end;
                }

                updateText();

            }, end.getHour(), end.getMinute(), true);

            dialog.show();
        });

        color_btn.setOnClickListener(v -> {
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

        matter_btn.setOnClickListener(v -> {
            String[] strings = new String[matters.size() + 1];

            strings[0] = getString(R.string.event_none);

            for (int i = 0; i < matters.size(); i++) {
                strings[i + 1] = matters.get(i).getTitle();
            }

            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.dialog_choose_matter))
                    .setItems(strings, (dialog, which) -> {
                        matter = which;
                        if (which > 0) {
                            color = matters.get(which - 1).getColor();
                        }
                        updateText();
                    })
                    .create().show();
        });

        alarm_btn.setOnClickListener(v -> {
            String[] strings = new String[4];

            strings[0] = getString(R.string.event_no_alarm);
            strings[1] = getString(R.string.alarm_30min);
            strings[2] = getString(R.string.alarm_1h);
            strings[3] = getString(R.string.alarm_1d);

            new AlertDialog.Builder(getContext())
                    .setItems(strings, (dialog, which) -> {
                        alarmDif = which;
                        Calendar alarmTime = Calendar.getInstance();
                        alarmTime.setTimeInMillis(start.toNumericalUnit());

                        switch (which) {
                            case 0: alarm = 0;
                                    break;

                            case 1: alarmTime.roll(Calendar.MINUTE, 30);
                                    alarm = alarmTime.getTimeInMillis();
                                    break;

                            case 2: alarmTime.roll(Calendar.HOUR_OF_DAY, 1);
                                    alarm = alarmTime.getTimeInMillis();
                                    break;

                            case 3: alarmTime.roll(Calendar.DAY_OF_MONTH, 1);
                                    alarm = alarmTime.getTimeInMillis();
                                    break;
                        }
                        updateText();
                    })
                    .create().show();
        });

        ((EventCreateActivity) getActivity()).add.setOnClickListener(v -> {
            end = new DayTime(start.getDayValue(), end.getHour(), end.getMinute());

            Schedule schedule = new Schedule(title_edt.getText().toString().trim(), start, end, alarmDif, User.getYear(0), User.getPeriod(0));

            if (id != 0) {
                schedule.id = id;
            }

            schedule.setDescription(description_edt.getText().toString());
            schedule.setColor(color);

            if (matter > 0) {
                schedule.matter.setTarget(matters.get(matter - 1));
            }

            Toast.makeText(getContext(), getString(id == 0 ? R.string.event_added : R.string.event_edited), Toast.LENGTH_SHORT).show();

            Box<Schedule> scheduleBox = App.getBox().boxFor(Schedule.class);
            id = scheduleBox.put(schedule);

            /*Intent intent = new Intent(getContext(), AlarmReceiver.class);
            intent.putExtra("ID", id);
            intent.putExtra("TYPE", CalendarBase.ViewType.USER);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int) id, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                if (alarm > 0) {
                    SimpleDateFormat date = new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.getDefault());
                    Log.i(TAG, "Alarm scheduled to " + date.format(alarm));
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.getAlarm(), pendingIntent);
                } else {
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                }
            }*/

            ((EventCreateActivity) getActivity()).finish();
            Client.get().requestUpdate();
        });
    }

    private void updateText() {
        startDate_txt.setText(start.getDay().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        startTime_txt.setText(start.getHour() + ":" + start.getMinute());
        endTime_txt.setText(end.getHour() + ":" + end.getMinute());
        color_img.setImageTintList(ColorStateList.valueOf(color));

        String[] alarms = new String[4];

        alarms[0] = getString(R.string.event_no_alarm);
        alarms[1] = getString(R.string.alarm_30min);
        alarms[2] = getString(R.string.alarm_1h);
        alarms[3] = getString(R.string.alarm_1d);

        if (alarmDif > 0) {
            alarm_txt.setText(alarms[alarmDif]);
        } else {
            alarm_txt.setText("");
        }

        if (matter > 0) {
            matter_txt.setText(matters.get(matter - 1).getTitle());

            if (color == matters.get(matter - 1).getColor()) {
                color_txt.setText(matters.get(matter - 1).getTitle());
            } else {
                color_txt.setText(getString(R.string.event_custom_color));
            }
        } else {
            matter_txt.setText("");

            if (color == getResources().getColor(R.color.colorPrimary)) {
                color_txt.setText(getString(R.string.event_default_color));
            } else {
                color_txt.setText(getString(R.string.event_custom_color));
            }
        }
    }

}
