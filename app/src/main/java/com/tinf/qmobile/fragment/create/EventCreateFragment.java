package com.tinf.qmobile.fragment.create;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
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
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.calendar.EventSimple_;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.base.CalendarBase;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.AlarmReceiver;
import com.tinf.qmobile.utility.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;

import static android.content.Context.ALARM_SERVICE;

public class EventCreateFragment extends Fragment {
    private static final String TAG = "EventCreateFragment";
    @BindView(R.id.event_create_start_day)     TextView startDate_txt;
    @BindView(R.id.event_create_start_time)    TextView startTime_txt;
    @BindView(R.id.event_create_end_day)       TextView endDate_txt;
    @BindView(R.id.event_create_end_time)      TextView endTime_txt;
    @BindView(R.id.event_create_color_text)    TextView color_txt;
    @BindView(R.id.event_create_matter_text)   TextView matter_txt;
    @BindView(R.id.event_create_alarm_text)    TextView alarm_txt;
    @BindView(R.id.event_create_description)   EditText description_edt;
    @BindView(R.id.event_create_title)         EditText title_edt;
    @BindView(R.id.event_create_color_layout)  LinearLayout color_btn;
    @BindView(R.id.event_create_matter_layout) LinearLayout matter_btn;
    @BindView(R.id.event_create_alarm_layout)  LinearLayout alarm_btn;
    @BindView(R.id.event_create_color_img)     ImageView color_img;
    private Calendar start, end;
    private boolean isRanged;
    private int color, matter, alarmDif;
    private List<Matter> matters;
    private String title, description;
    private long id, alarm, firstMonth, lastMonth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Box<Matter> matterBox = App.getBox().boxFor(Matter.class);

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

        List<EventSimple> months = App.getBox().boxFor(EventSimple.class).query().order(EventSimple_.startTime).build().find();

        firstMonth = months.get(0).getStartTime() + 1;

        lastMonth =  months.get(months.size() - 1).getStartTime() - 1;

        Bundle bundle = getArguments();

        if (bundle != null) {

            id = bundle.getLong("ID");

            if (id != 0) {

                EventUser event = App.getBox().boxFor(EventUser.class).get(id);

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
                    ((EventCreateActivity) getActivity()).finish();
                }
            } else {
                color = getResources().getColor(R.color.colorPrimary);

                if (start.getTimeInMillis() < firstMonth) {
                    start.setTimeInMillis(firstMonth);
                }

                if (end.getTimeInMillis() > lastMonth) {
                    end.setTimeInMillis(lastMonth);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
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
            DatePickerDialog dialog = new DatePickerDialog(getContext(), (datePicker, y, m, d) -> {
                Calendar temp = (Calendar) start.clone();
                temp.set(Calendar.YEAR, y);
                temp.set(Calendar.MONTH, m);
                temp.set(Calendar.DAY_OF_MONTH, d);

                if (temp.getTimeInMillis() >= firstMonth && temp.getTimeInMillis() <= lastMonth) {
                    start.set(Calendar.YEAR, y);
                    start.set(Calendar.MONTH, m);
                    start.set(Calendar.DAY_OF_MONTH, d);

                    if (!isRanged || end.getTimeInMillis() < start.getTimeInMillis()) {
                        end.set(Calendar.YEAR, y);
                        end.set(Calendar.MONTH, m);
                        end.set(Calendar.DAY_OF_MONTH, d);
                    }

                    updateText();
                } else {
                    Toast.makeText(getContext(), R.string.calendar_end_period, Toast.LENGTH_SHORT).show();
                }
            },
                    start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));

            dialog.show();

        });

        startTime_txt.setOnClickListener(view1 -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, h, m) -> {
                Calendar temp = (Calendar) start.clone();
                temp.set(Calendar.HOUR_OF_DAY, h);
                temp.set(Calendar.MINUTE, m);

                if (temp.getTimeInMillis() >= firstMonth && temp.getTimeInMillis() <= lastMonth) {
                    start.set(Calendar.HOUR_OF_DAY, h);
                    start.set(Calendar.MINUTE, m);

                    if (!isRanged || end.getTimeInMillis() < start.getTimeInMillis()) {
                        end.set(Calendar.HOUR_OF_DAY, h);
                        end.set(Calendar.MINUTE, m);
                    }

                    updateText();
                } else {
                    Toast.makeText(getContext(), R.string.calendar_end_period, Toast.LENGTH_SHORT).show();
                }
            },
                    start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE), true);

            dialog.show();
        });

        endDate_txt.setOnClickListener(view1 -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext(), (datePicker, y, m, d) -> {
                Calendar temp = (Calendar) end.clone();
                temp.set(Calendar.YEAR, y);
                temp.set(Calendar.MONTH, m);
                temp.set(Calendar.DAY_OF_MONTH, d);

                if (temp.getTimeInMillis() >= firstMonth && temp.getTimeInMillis() <= lastMonth) {

                    if (temp.getTimeInMillis() >= start.getTimeInMillis()) {
                        end.set(Calendar.YEAR, y);
                        end.set(Calendar.MONTH, m);
                        end.set(Calendar.DAY_OF_MONTH, d);
                        isRanged = true;
                    }

                    updateText();
                } else {
                    Toast.makeText(getContext(), R.string.calendar_end_period, Toast.LENGTH_SHORT).show();
                }
            },
                    end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH));

            dialog.show();
        });

        endTime_txt.setOnClickListener(view1 -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, h, m) -> {
                Calendar temp = (Calendar) end.clone();
                temp.set(Calendar.HOUR_OF_DAY, h);
                temp.set(Calendar.MINUTE, m);

                if (temp.getTimeInMillis() >= firstMonth && temp.getTimeInMillis() <= lastMonth) {

                    if (temp.getTimeInMillis() >= start.getTimeInMillis()) {
                        end.set(Calendar.HOUR_OF_DAY, h);
                        end.set(Calendar.MINUTE, m);
                        isRanged = true;
                    }

                    updateText();
                } else {
                    Toast.makeText(getContext(), R.string.calendar_end_period, Toast.LENGTH_SHORT).show();
                }
            },
                    end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE), true);

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
                        updateText();
                    })
                    .create().show();
        });

        ((EventCreateActivity) getActivity()).add.setOnClickListener(v -> {

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

            EventUser event = new EventUser(title_edt.getText().toString().trim(),
                    start.getTimeInMillis(), end.getTimeInMillis(), alarm, alarmDif);

            if (id != 0) {
                event.id = id;
            }

            event.setDescription(description_edt.getText().toString().trim());
            event.setColor(color);

            if (matter > 0) {
                event.matter.setTarget(matters.get(matter - 1));
            }

            Toast.makeText(getContext(), getString(id == 0 ? R.string.event_added : R.string.event_edited), Toast.LENGTH_SHORT).show();

            Box<EventUser> eventBox = App.getBox().boxFor(EventUser.class);
            id = eventBox.put(event);

            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            intent.putExtra("ID", id);
            intent.putExtra("TYPE", CalendarBase.ViewType.USER);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int) id, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                if (alarmDif != 0) {

                    SimpleDateFormat date = new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.getDefault());
                    Log.i(TAG, "Alarm scheduled to " + date.format(alarm));

                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.getAlarm(), pendingIntent);

                } else {
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                }
            }

            ((EventCreateActivity) getActivity()).finish();
            Client.get().requestUpdate();
        });
    }

    private void updateText() {
        SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.getDefault());

        startDate_txt.setText(date.format(start.getTimeInMillis()));
        startTime_txt.setText(time.format(start.getTimeInMillis()));
        endDate_txt.setText(date.format(end.getTimeInMillis()));
        endTime_txt.setText(time.format(end.getTimeInMillis()));
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
