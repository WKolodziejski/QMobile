package com.tinf.qmobile.fragment.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventCreateActivity;
import static com.tinf.qmobile.activity.EventCreateActivity.EVENT;
import static com.tinf.qmobile.activity.EventCreateActivity.SCHEDULE;

public class CreateFragment extends BottomSheetDialogFragment {
    //private FragmentCreateBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_create, container, false);
        //binding = FragmentCreateBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void onEventClick(View view) {
        Intent intent = new Intent(getContext(), EventCreateActivity.class);
        intent.putExtra("TYPE", EVENT);
        startActivity(intent);
        dismiss();
    }

    public void onScheduleClick(View view) {
        Intent intent = new Intent(getContext(), EventCreateActivity.class);
        intent.putExtra("TYPE", SCHEDULE);
        startActivity(intent);
        dismiss();
    }

}
