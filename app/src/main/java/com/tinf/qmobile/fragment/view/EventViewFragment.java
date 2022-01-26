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
import com.tinf.qmobile.databinding.FragmentViewEventBinding;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.matter.Matter;
import java.text.SimpleDateFormat;
import java.util.Locale;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import static android.view.View.GONE;
import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;

public class EventViewFragment extends Fragment {
    private FragmentViewEventBinding binding;
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

        sub1 = DataBase.get().getBoxStore().subscribe(EventUser.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_event, container, false);
        binding = FragmentViewEventBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setText();
    }

    private void setText() {
        EventUser event = DataBase.get().getBoxStore().boxFor(EventUser.class).get(id);

        if (event == null) {
            return;
        }

        SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy ・ HH:mm", Locale.getDefault());

        binding.title.setText(event.getTitle().isEmpty() ? getString(R.string.event_no_title) : event.getTitle());
        binding.startTime.setText(date.format(event.getStartTime()));
        binding.colorImg.setImageTintList(ColorStateList.valueOf(event.getColor()));

        if (event.getDescription().isEmpty()) {
            binding.descriptionLayout.setVisibility(GONE);
        } else {
            binding.descriptionLayout.setVisibility(View.VISIBLE);
            binding.description.setText(event.getDescription());
        }

        if (event.getAlarm() == 0) {
            binding.notificationLayout.setVisibility(GONE);
        } else {
            String[] strings = new String[4];

            strings[0] = getString(R.string.event_no_alarm);
            strings[1] = getString(R.string.alarm_30min);
            strings[2] = getString(R.string.alarm_1h);
            strings[3] = getString(R.string.alarm_1d);

            binding.notificationLayout.setVisibility(View.VISIBLE);
            binding.notificationText.setText(strings[event.getDifference()]);
        }

        if (event.getEndTime() == 0) {
            binding.endTime.setVisibility(GONE);
        } else {
            binding.endTime.setVisibility(View.VISIBLE);
            binding.endTime.setText(date.format(event.getEndTime()));
        }

        if (event.getMatter().isEmpty()) {
            binding.matterText.setVisibility(GONE);
        } else {
            binding.matterText.setVisibility(View.VISIBLE);
            binding.matterText.setText(event.getMatter());
        }

        if (lookup && !event.matter.isNull()) {
            binding.headerLayout.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), MatterActivity.class);
                intent.putExtra("ID", event.matter.getTargetId());
                intent.putExtra("PAGE", EVENT);
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
            intent.putExtra("TYPE", EVENT);
            intent.putExtra("ID", id);
            startActivity(intent);
            return true;

        } else if (itemId == R.id.action_delete) {
            new MaterialAlertDialogBuilder(getActivity())
                    .setMessage(getString(R.string.dialog_delete_txt))
                    .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                        DataBase.get().getBoxStore().boxFor(EventUser.class).remove(id);
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
