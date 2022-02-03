package com.tinf.qmobile.fragment.view;

import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentViewJournalBinding;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Clazz_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period_;

import java.text.SimpleDateFormat;
import java.util.Locale;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.query.QueryBuilder;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class JournalViewFragment extends Fragment {
    private FragmentViewJournalBinding binding;
    private DataSubscription sub1, sub2;
    private long id;
    private boolean lookup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            id = bundle.getLong("ID");
            lookup = bundle.getBoolean("LOOKUP", false);
        }

        DataObserver observer = data -> setText();

        sub1 = DataBase.get().getBoxStore().subscribe(Journal.class)
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
        View view = inflater.inflate(R.layout.fragment_view_journal, container, false);
        binding = FragmentViewJournalBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setText();
    }

    private void setText() {
        Journal journal = DataBase.get().getBoxStore().boxFor(Journal.class).get(id);

        if (journal == null) {
            return;
        }

        if (!journal.isSeen_()) {
            journal.see();
            DataBase.get().getBoxStore().boxFor(Journal.class).put(journal);
        }

        SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        binding.title.setText(journal.getTitle());
        binding.grade.setText(String.format(getString(R.string.diarios_Nota), journal.getGrade(), journal.getMax()));
        binding.weight.setText(String.format(getString(R.string.diarios_Peso), journal.getWeight()));
        binding.typeShort.setText(journal.getShort());
        binding.type.setText(journal.getType());
        binding.time.setText(date.format(journal.getDate()));
        binding.matter.setText(String.format("%s・%s", journal.getMatter(), journal.getPeriod()));
        binding.colorImg.setImageTintList(ColorStateList.valueOf(journal.getColor()));

        if (lookup && !journal.matter.isNull()) {
            binding.headerLayout.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), MatterActivity.class);
                intent.putExtra("ID", journal.matter.getTargetId());
                intent.putExtra("PAGE", JOURNAL);
                getContext().startActivity(intent);
            });
        } else {
            binding.headerLayout.setOnClickListener(null);
        }

        QueryBuilder<Clazz> clazzBuilder = DataBase.get().getBoxStore().boxFor(Clazz.class)
                .query().between(Clazz_.date_, journal.getStartTime(), journal.getStartTime());//.and()
                //.equal(Clazz_.title, journal.getTitle(), QueryBuilder.StringOrder.CASE_INSENSITIVE);

        clazzBuilder.link(Clazz_.period)
                .equal(Period_.id, journal.period.getTargetId());

        Clazz clazz = null;

        try {
            clazz = clazzBuilder.build().findUnique();
        } catch (NonUniqueResultException e) {
            e.printStackTrace();
        }

        if (clazz != null) {
            binding.presenceLayout.setVisibility(clazz.getAbsences_() > 0 ? View.VISIBLE : View.GONE);
            binding.absences.setText(String.format(getString(R.string.class_absence), clazz.getAbsences_()));
        } else {
            binding.presenceLayout.setVisibility(View.GONE);
        }

        Log.d(journal.getTitle(), String.valueOf(clazz));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sub1.cancel();
        sub2.cancel();
    }

}
