package com.tinf.qmobile.fragment.view;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.AttachmentsAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.FragmentViewMessageBinding;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataSubscription;

public class MessageViewFragment extends Fragment {
    private FragmentViewMessageBinding binding;
    private DataSubscription sub1;
    private long id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null)
            id = bundle.getLong("ID");

        sub1 = DataBase.get().getBoxStore()
                .boxFor(Message.class)
                .query()
                .equal(Message_.id, id)
                .build()
                .subscribe()
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(data -> setText());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_message, container, false);
        binding = FragmentViewMessageBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setText();
    }

    private void setText() {
        Message message = DataBase.get().getBoxStore().boxFor(Message.class).get(id);

        if (message != null) {

            if (!message.isSeen_()) {
                message.see();
                DataBase.get().getBoxStore().boxFor(Message.class).put(message);
            }

            binding.progressBar.setVisibility(message.getText_() == null ? View.VISIBLE : View.GONE);

            binding.title.setText(message.getSubject_());
            binding.date.setText(message.formatDate());
            binding.sender.setText(message.sender.getTarget().getName_());
            binding.header.setBackgroundTintList(ColorStateList.valueOf(message.sender.getTarget().getColor_()));
            binding.header.setText(message.sender.getTarget().getSign());
            binding.content.setText(message.getContent());

            if (message.isSolved_()) {
                binding.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                TextViewCompat.setCompoundDrawableTintList(binding.title, ColorStateList.valueOf(getResources().getColor(R.color.amber_a700)));
            }

            if (!message.attachments.isEmpty()) {
                //binding.recycler.setHasFixedSize(true);
                binding.recycler.setItemViewCacheSize(3);
                binding.recycler.setDrawingCacheEnabled(true);
                binding.recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                binding.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                binding.recycler.setAdapter(new AttachmentsAdapter(getContext(), message.attachments, false));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sub1.cancel();
    }

}
