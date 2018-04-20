package com.qacademico.qacademico.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

            String[][] boletimTable = new String[boletim.size()][5];

            for (int i = 0; i < boletim.size(); i++) {
                boletimTable[i][0] = boletim.get(i).getMateria();
                boletimTable[i][1] = boletim.get(i).getNotaPrimeiraEtapa();
                boletimTable[i][2] = boletim.get(i).getFaltasPrimeiraEtapa();
                boletimTable[i][3] = boletim.get(i).getNotaFinalPrimeiraEtapa();
                boletimTable[i][4] = boletim.get(i).getRPPrimeiraEtapa();
            }

            AdaptiveTableLayout table = (AdaptiveTableLayout) view.findViewById(R.id.tableBoletim);
            table.setAdapter(new BoletimAdapter(getActivity().getApplicationContext(), boletimTable));
            table.setFocusable(false);
        }

        return view;
    }
}
