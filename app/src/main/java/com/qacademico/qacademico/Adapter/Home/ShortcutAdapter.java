package com.qacademico.qacademico.Adapter.Home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qacademico.qacademico.Class.Shortcut;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.ShortcutViewHolder;
import java.util.List;

public class ShortcutAdapter extends RecyclerView.Adapter {
    private List<Shortcut> shortcutList;
    private Context context;
    private OnShortcutClicked onClick;

    public ShortcutAdapter(List<Shortcut> shortcutList, Context context) {
        this.shortcutList = shortcutList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.table_shortcut, parent, false);
        return new ShortcutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final ShortcutViewHolder holder = (ShortcutViewHolder) viewHolder;
        final Shortcut shortcut  = shortcutList.get(position) ;

        holder.title.setText(shortcut.getTitle());
        holder.icon.setImageResource(shortcut.getImage());

        holder.layout.setOnClickListener(v -> {
            onClick.OnShortcutClick(position, v);
        });
    }

    @Override
    public int getItemCount() {
        return shortcutList.size();
    }

    public interface OnShortcutClicked {
        void OnShortcutClick(int position, View view);
    }

    public void setOnClickListener(OnShortcutClicked onClick) {
        this.onClick = onClick;
    }
}
