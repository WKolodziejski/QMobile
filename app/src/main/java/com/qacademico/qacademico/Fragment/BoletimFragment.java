package com.qacademico.qacademico.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.qacademico.qacademico.Adapter.BoletimAdapter;
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.R;
import java.util.List;

public class BoletimFragment extends Fragment {
    List<Boletim> boletim;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            boletim = (List<Boletim>) getArguments().getSerializable("Boletim");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boletim, container, false);

        if (boletim != null) {

            AdaptiveTableLayout table = (AdaptiveTableLayout) view.findViewById(R.id.tableBoletim);
            BoletimAdapter adapter = new BoletimAdapter(getActivity().getApplicationContext(), boletim);

            table.setAdapter(adapter);

            adapter.setOnHeaderClick(() -> {
                table.setHeaderFixed(!table.isHeaderFixed());
            });
        }
        return view;
    }
}
