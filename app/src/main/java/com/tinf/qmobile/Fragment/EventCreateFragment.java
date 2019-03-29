package com.tinf.qmobile.Fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import androidx.appcompat.app.AlertDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.tinf.qmobile.Activity.Calendar.EventCreateActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.EventUser;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.Class.Materias.Matter_;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;

public class EventCreateFragment extends Fragment {
    @BindView(R.id.event_create_start_day)     TextView startDate_txt;
    @BindView(R.id.event_create_start_time)    TextView startTime_txt;
    @BindView(R.id.event_create_end_day)       TextView endDate_txt;
    @BindView(R.id.event_create_end_time)      TextView endTime_txt;
    @BindView(R.id.event_create_color_text)    TextView color_txt;
    @BindView(R.id.event_create_matter_text)   TextView matter_txt;
    @BindView(R.id.event_create_notification_text) TextView alarm_txt;
    @BindView(R.id.event_create_description)   EditText description_edt;
    @BindView(R.id.event_create_title)         EditText title_edt;
    @BindView(R.id.event_create_color_layout)  LinearLayout color_btn;
    @BindView(R.id.event_create_matter_layout) LinearLayout matter_btn;
    @BindView(R.id.event_create_color_img)     ImageView color_img;
    private Calendar start, end;
    private boolean isRanged;
    private int color, matter;
    private List<Matter> matters;
    private String title, description;
    private long id, alarm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Box<Matter> matterBox = App.getBox().boxFor(Matter.class);

        matters = matterBox.query()
                .equal(Matter_.year, User.getYear(0)).and()
                .equal(Matter_.period, User.getPeriod(0))
                .build().find();

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        start = (Calendar) calendar.clone();
        end = (Calendar) calendar.clone();

        Bundle bundle = getArguments();

        if (bundle != null) {
            EventUser event = App.getBox().boxFor(EventUser.class).get(bundle.getLong("ID"));

            if (event != null) {

                id = event.id;
                title = event.getTitle();
                color = event.getColor();
                description = event.getDescription();

                start.setTimeInMillis(event.getStartTime());

                isRanged = event.isRanged();

                alarm = event.getAlarm();

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
            title = "";
            color = getResources().getColor(R.color.colorPrimary);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_create, container, false);
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
                start.set(Calendar.YEAR, y);
                start.set(Calendar.MONTH, m);
                start.set(Calendar.DAY_OF_MONTH, d);

                if (!isRanged || end.getTimeInMillis() < start.getTimeInMillis()) {
                    end.set(Calendar.YEAR, y);
                    end.set(Calendar.MONTH, m);
                    end.set(Calendar.DAY_OF_MONTH, d);
                }

                updateText();
            },
                    start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));

            dialog.show();
        });

        startTime_txt.setOnClickListener(view1 -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, h, m) -> {
                start.set(Calendar.HOUR_OF_DAY, h);
                start.set(Calendar.MINUTE, m);

                if (!isRanged || end.getTimeInMillis() < start.getTimeInMillis()) {
                    end.set(Calendar.HOUR_OF_DAY, h);
                    end.set(Calendar.MINUTE, m);
                }

                updateText();
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

                if (temp.getTimeInMillis() >= start.getTimeInMillis()) {
                    end.set(Calendar.YEAR, y);
                    end.set(Calendar.MONTH, m);
                    end.set(Calendar.DAY_OF_MONTH, d);
                    isRanged = true;
                }

                updateText();
            },
                    end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH));

            dialog.show();
        });

        endTime_txt.setOnClickListener(view1 -> {
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

        ((EventCreateActivity) getActivity()).add.setOnClickListener(v -> {

            if (end.getTimeInMillis() == start.getTimeInMillis()) {
                end.setTimeInMillis(0);
            }

            EventUser event = new EventUser(title_edt.getText().toString(),
                    start.getTimeInMillis(), end.getTimeInMillis(), 0);

            if (id != 0) {
                event.id = id;
            }

            event.setDescription(description_edt.getText().toString());
            event.setColor(color);

            if (matter > 0) {
                event.matter.setTarget(matters.get(matter - 1));
            }

            Box<EventUser> eventBox = App.getBox().boxFor(EventUser.class);
            eventBox.put(event);

            ((EventCreateActivity) getActivity()).finish();

            Toast.makeText(getContext(), getString(R.string.event_added), Toast.LENGTH_SHORT).show();
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

        if (alarm == 0) {
            alarm_txt.setText(getString(R.string.event_none));
        } else {
            alarm_txt.setText(date.format(alarm) + " " + time.format(alarm));
        }

        if (matter > 0) {
            matter_txt.setText(matters.get(matter - 1).getTitle());

            if (color == matters.get(matter - 1).getColor()) {
                color_txt.setText(matters.get(matter - 1).getTitle());
            } else {
                color_txt.setText(getString(R.string.event_custom));
            }
        } else {
            matter_txt.setText(getString(R.string.event_none));

            if (color == getResources().getColor(R.color.colorPrimary)) {
                color_txt.setText(getString(R.string.event_default));
            } else {
                color_txt.setText(getString(R.string.event_custom));
            }
        }
    }

}