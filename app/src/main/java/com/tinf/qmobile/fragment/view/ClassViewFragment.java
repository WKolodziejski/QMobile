package com.tinf.qmobile.fragment.view;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Matter;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class ClassViewFragment extends Fragment {
    @BindView(R.id.class_view_time_text)            TextView date_txt;
    @BindView(R.id.class_view_title)                TextView title_txt;
    @BindView(R.id.class_view_matter_text)          TextView matter_txt;
    @BindView(R.id.class_view_absences)             TextView absences_txt;
    @BindView(R.id.class_view_given)                TextView given_txt;
    @BindView(R.id.class_view_teacher)              TextView teacher_txt;
    @BindView(R.id.class_view_content_text)         TextView content_txt;
    @BindView(R.id.class_view_color_img)            ImageView color_img;
    private DataSubscription sub1, sub2;
    private long id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();

        if (bundle != null)
            id = bundle.getLong("ID");

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
        View view = inflater.inflate(R.layout.fragment_view_class, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setText();
    }

    private void setText() {
        Clazz clazz = DataBase.get().getBoxStore().boxFor(Clazz.class).get(id);

        if (clazz != null) {

            SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

            title_txt.setText(clazz.getMatter());
            absences_txt.setText(String.format(getString(R.string.class_absence), clazz.getAbsences_()));
            given_txt.setText(String.format(getString(R.string.class_given), clazz.getClassesCount_()));
            teacher_txt.setText(clazz.getTeacher());
            date_txt.setText(date.format(clazz.getDate()));
            matter_txt.setText(clazz.getPeriod());
            content_txt.setText(clazz.getContent());
            color_img.setImageTintList(ColorStateList.valueOf(clazz.period.getTarget().matter.getTarget().getColor()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sub1.cancel();
        sub2.cancel();
    }

}
