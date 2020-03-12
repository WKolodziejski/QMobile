package com.tinf.qmobile.fragment.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventCreateActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tinf.qmobile.activity.EventCreateActivity.EVENT;
import static com.tinf.qmobile.activity.EventCreateActivity.SCHEDULE;

public class CreateFragment extends BottomSheetDialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_create, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.sheet_create_event)
    public void onEventClick(View view) {
        Intent intent = new Intent(getContext(), EventCreateActivity.class);
        intent.putExtra("TYPE", EVENT);
        startActivity(intent);
        dismiss();
    }

    @OnClick(R.id.sheet_create_schedule)
    public void onScheduleClick(View view) {
        Intent intent = new Intent(getContext(), EventCreateActivity.class);
        intent.putExtra("TYPE", SCHEDULE);
        startActivity(intent);
        dismiss();
    }

}
