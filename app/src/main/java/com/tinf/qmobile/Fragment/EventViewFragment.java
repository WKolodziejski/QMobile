package com.tinf.qmobile.Fragment;

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
import com.tinf.qmobile.Activity.Calendar.EventCreateActivity;
import com.tinf.qmobile.Activity.EventViewActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Base.CalendarBase;
import com.tinf.qmobile.Class.Calendario.EventQ;
import com.tinf.qmobile.Class.Calendario.EventUser;
import com.tinf.qmobile.R;
import java.text.SimpleDateFormat;
import java.util.Locale;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class EventViewFragment extends Fragment {
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
    private int type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            id = bundle.getLong("ID");
            type = bundle.getInt("TYPE");
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

        if (type == CalendarBase.ViewType.USER) {
            setHasOptionsMenu(true);

            EventUser event = App.getBox().boxFor(EventUser.class).get(id);

            fillFields(event.getTitle(), event.getDescription(), event.getMatter(), event.getStartTime(), event.getEndTime(), event.getAlarmDifference(), event.getColor(), true);

        } else {

            EventQ event = App.getBox().boxFor(EventQ.class).get(id);

            fillFields(event.getDescription(), "", event.getMatter(), event.getStartTime(), event.getEndTime(), "", event.getColor(), false);
        }
    }

    private void fillFields(String title, String description, String matter, long start, long end, String alarm, @ColorInt int color, boolean isUserEvent) {
        SimpleDateFormat date = isUserEvent ? new SimpleDateFormat("dd MMM yyyy ・ HH:mm", Locale.getDefault()) : new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        title_txt.setText(title.isEmpty() ? getString(R.string.event_no_title) : title);
        startTime_txt.setText(date.format(start));
        color_img.setImageTintList(ColorStateList.valueOf(color));

        if (description.isEmpty()) {
            description_layout.setVisibility(GONE);
        } else {
            description_txt.setText(description);
        }

        if (alarm.isEmpty()) {
            notification_layout.setVisibility(GONE);
        } else {
            notification_txt.setText(alarm);
        }

        if (end == 0) {
            endTime_txt.setVisibility(GONE);
        } else {
            endTime_txt.setText(date.format(end));
        }

        if (matter.isEmpty()) {
            matter_txt.setVisibility(GONE);
        } else {
            matter_txt.setText(matter);
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
                intent.putExtra("ID", id);
                startActivity(intent);
                return true;

            case R.id.action_delete:

                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.dialog_delete_txt))
                        .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                            App.getBox().boxFor(EventUser.class).remove(id);
                            ((EventViewActivity) getActivity()).finish();
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .create()
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
