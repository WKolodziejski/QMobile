package com.tinf.qmobile.fragment.view;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.tinf.qmobile.App;
import com.tinf.qmobile.model.matter.Journal;
import com.tinf.qmobile.R;
import java.text.SimpleDateFormat;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalViewFragment extends Fragment {
    @BindView(R.id.journal_view_time_text)           TextView time_txt;
    @BindView(R.id.journal_view_grade_text)          TextView grade_txt;
    @BindView(R.id.journal_view_matter_text)         TextView matter_txt;
    @BindView(R.id.journal_view_weight_text)         TextView weight_txt;
    @BindView(R.id.journal_view_title)               TextView title_txt;
    @BindView(R.id.journal_view_type_text)           TextView type_txt;
    @BindView(R.id.journal_view_type_short)          TextView short_txt;
    @BindView(R.id.journal_view_color_img)           ImageView color_img;
    private long id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            id = bundle.getLong("ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_journal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        Journal journal = App.getBox().boxFor(Journal.class).get(id);

        title_txt.setText(journal.getTitle());
        grade_txt.setText(String.format(getString(R.string.diarios_Nota), journal.getGrade(), journal.getMax()));
        weight_txt.setText(String.format(getString(R.string.diarios_Peso), journal.getWeight()));
        short_txt.setText(journal.getShort());
        type_txt.setText(journal.getType());
        time_txt.setText(date.format(journal.getDate()));
        matter_txt.setText(journal.getMatter() + "・" + journal.getPeriod());
        color_img.setImageTintList(ColorStateList.valueOf(journal.getColor()));
    }

}
