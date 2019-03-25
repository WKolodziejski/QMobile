package com.tinf.qmobile.ViewHolder.Calendar.TEST;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.oushangfeng.pinnedsectionitemdecoration.utils.FullSpanUtil;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public abstract class HeaderAdapterTEST<T extends MultiItemEntity> extends BaseMultiItemQuickAdapter<T, BaseViewHolder> {
    public static final int HEADER = 1;
    public static final int DATA = 2;

    public HeaderAdapterTEST(List<T> data) {
        super(data);
        addItemTypes();
    }

    protected abstract void addItemTypes();

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        FullSpanUtil.onAttachedToRecyclerView(recyclerView, this, HEADER);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        FullSpanUtil.onViewAttachedToWindow(holder, this, HEADER);
    }

}
