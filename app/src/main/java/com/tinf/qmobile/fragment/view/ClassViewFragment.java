package com.tinf.qmobile.fragment.view;

import static com.tinf.qmobile.model.ViewType.CLASS;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentViewClassBinding;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Matter;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class ClassViewFragment extends Fragment {
  private FragmentViewClassBinding binding;
  private DataSubscription sub1, sub2;
  private long id;
  private boolean lookup;

  @Override
  public void onCreate(
      @Nullable
      Bundle savedInstanceState) {
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
  public View onCreateView(
      @NonNull
      LayoutInflater inflater,
      @Nullable
      ViewGroup container,
      @Nullable
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_view_class, container, false);
    binding = FragmentViewClassBinding.bind(view);
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
    Clazz clazz = DataBase.get().getBoxStore().boxFor(Clazz.class).get(id);

    if (clazz == null) {
      return;
    }

    SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    binding.title.setText(clazz.getMatter());
    binding.absences.setText(
        String.format(getString(R.string.class_absence), clazz.getAbsences_()));
    binding.given.setText(String.format(getString(R.string.class_given), clazz.getClassesCount_()));
    binding.teacher.setText(clazz.getTeacher());
    binding.timeText.setText(date.format(clazz.getDate()));
    binding.matterText.setText(clazz.getPeriod());
    binding.contentText.setText(clazz.getContent());
    binding.colorImg.setImageTintList(
        ColorStateList.valueOf(clazz.period.getTarget().matter.getTarget().getColor()));

    if (lookup) {
      binding.headerLayout.setOnClickListener(view -> {
        Intent intent = new Intent(getContext(), MatterActivity.class);
        intent.putExtra("ID", clazz.period.getTarget().matter.getTargetId());
        intent.putExtra("PAGE", CLASS);
        getContext().startActivity(intent);
      });
    } else {
      binding.headerLayout.setOnClickListener(null);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    sub1.cancel();
    sub2.cancel();
  }

}
