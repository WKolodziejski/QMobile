package com.tinf.qmobile.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tinf.qmobile.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SheetFragment extends BottomSheetDialogFragment {
    @BindView(R.id.sheet_title)         TextView title;
    @BindView(R.id.sheet_msg)           TextView message;
    @BindView(R.id.sheet_checkBox)      CheckBox show;
    private String t, m;
    private WebView webView;

    public SheetFragment(WebView webView, String t, String m) {
        this.webView = webView;
        this.t = t;
        this.m = m;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sheet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(t);
        message.setText(m);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if (show.isChecked())
            webView.loadUrl("javascript:(function() {" +
                    "MarcarComoLida(mensagens[indiceMensagens].cod_mensagem);" +
                    "})()");
    }

}
