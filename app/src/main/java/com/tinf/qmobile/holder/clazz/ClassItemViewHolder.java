package com.tinf.qmobile.holder.clazz;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.databinding.ClassItemBinding;
import com.tinf.qmobile.model.matter.Clazz;
import static com.tinf.qmobile.model.ViewType.CLASS;

public class ClassItemViewHolder extends ClassBaseViewHolder<Clazz> {
    private ClassItemBinding binding;

    public ClassItemViewHolder(View view) {
        super(view);
        binding = ClassItemBinding.bind(view);
    }

    @Override
    public void bind(Context context, Clazz clazz) {
        binding.date.setText(clazz.formatDate());
        binding.content.setText(clazz.getContent_());
        binding.absence.setVisibility(clazz.getAbsences_() > 0 ? View.VISIBLE : View.INVISIBLE);

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", clazz.id);
            intent.putExtra("TYPE", CLASS);
            context.startActivity(intent);
        });

        if (clazz.highlight) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.notificationBackground));
        }
    }

}
