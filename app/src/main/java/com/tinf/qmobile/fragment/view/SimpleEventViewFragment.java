package com.tinf.qmobile.fragment.view;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentViewSimpleEventBinding;
import com.tinf.qmobile.model.calendar.EventSimple;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class SimpleEventViewFragment extends Fragment {
  private FragmentViewSimpleEventBinding binding;
  private DataSubscription sub1;
  private long id;

  @Override
  public void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    Bundle bundle = getArguments();

    if (bundle != null) {
      id = bundle.getLong("ID");
    }

    DataObserver observer = data -> setText();

    sub1 = DataBase.get()
                   .getBoxStore()
                   .subscribe(EventSimple.class)
                   .on(AndroidScheduler.mainThread())
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull
      LayoutInflater inflater,
      @Nullable
      ViewGroup container,
      @Nullable
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_view_simple_event, container, false);
    binding = FragmentViewSimpleEventBinding.bind(view);
    return view;
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setText();
  }

  private void setText() {
    EventSimple event = DataBase.get()
                                .getBoxStore()
                                .boxFor(EventSimple.class)
                                .get(id);

    if (event == null) {
      return;
    }

    SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy ・ HH:mm", Locale.getDefault());

    binding.title.setText(
        event.getTitle()
             .isEmpty() ? getString(R.string.event_no_title) : event.getTitle());
    binding.startTime.setText(date.format(event.getStartTime()));

    if (event.getEndTime() == 0) {
      binding.endTime.setVisibility(GONE);
    } else {
      binding.endTime.setVisibility(View.VISIBLE);
      binding.endTime.setText(date.format(event.getEndTime()));
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    sub1.cancel();
  }

}
