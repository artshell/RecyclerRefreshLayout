package com.dinuscxj.example.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HeaderViewRecyclerAdapter extends RecyclerView.Adapter {
    private static final int BASE_HEADER_VIEW_TYPE = -1 << 10;
    private static final int BASE_FOOTER_VIEW_TYPE = -1 << 11;

    private final List<FixedViewInfo> mHeaderList;
    private final List<FixedViewInfo> mFooterList;

    private final RecyclerView.Adapter mAdapter;

    private boolean mIsStaggeredGrid;

    public HeaderViewRecyclerAdapter(RecyclerView.Adapter adapter) {
        this(adapter, null, null);
    }

    public HeaderViewRecyclerAdapter(RecyclerView.Adapter adapter, List<FixedViewInfo> headerList, List<FixedViewInfo> footerList) {
        this.mAdapter = adapter;
        this.mHeaderList = headerList == null ? new ArrayList<FixedViewInfo>() : headerList;
        this.mFooterList = footerList == null ? new ArrayList<FixedViewInfo>() : headerList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeaderViewType(viewType)) {
            int headerIndex = Math.abs(viewType - BASE_HEADER_VIEW_TYPE);
            View headerView = mHeaderList.get(headerIndex).view;

            return createHeaderFooterViewHolder(headerView);
        } else if (isFooterViewType(viewType)) {
            int footerIndex = Math.abs(viewType - BASE_FOOTER_VIEW_TYPE);
            View footerView = mFooterList.get(footerIndex).view;

            return createHeaderFooterViewHolder(footerView);
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getHeadersCount() || position >= getHeadersCount() + mAdapter.getItemCount()) {
            return;
        }

        mAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaderList.get(position).viewType;
        } else if (isFooterPosition(position)) {
            return mFooterList.get(position - mAdapter.getItemCount() - getHeadersCount()).viewType;
        } else {
            return mAdapter.getItemViewType(position - getHeadersCount());
        }
    }

    @Override
    public int getItemCount() {
        return getFootersCount() + getHeadersCount() + mAdapter.getItemCount();
    }

    public static class FixedViewInfo {
        public int viewType;
        public View view;
    }

    public int getHeadersCount() {
        return mHeaderList.size();
    }

    public int getFootersCount() {
        return mFooterList.size();
    }

    private boolean isHeaderPosition(int position) {
        return position < mHeaderList.size();
    }

    private boolean isFooterPosition(int position) {
        return position >= mHeaderList.size() + mAdapter.getItemCount();
    }

    public boolean isEmpty() {
        return mAdapter == null || mAdapter.getItemCount() == 0;
    }

    public boolean removeHeaderView(View v) {
        for (int i = 0; i < mHeaderList.size(); i++) {
            FixedViewInfo info = mHeaderList.get(i);
            if (info.view == v) {
                mHeaderList.remove(i);
                notifyDataSetChanged();
                return true;
            }
        }

        return false;
    }

    public boolean removeFooterView(View v) {
        for (int i = 0; i < mFooterList.size(); i++) {
            FixedViewInfo info = mFooterList.get(i);
            if (info.view == v) {
                mFooterList.remove(i);
                notifyDataSetChanged();
                return true;
            }
        }

        return false;
    }

    public void removeAllHeaderView() {
        if (!mHeaderList.isEmpty()) {
            mHeaderList.clear();
            notifyDataSetChanged();
        }
    }

    public void removeAllFooterView() {
        if (!mFooterList.isEmpty()) {
            mFooterList.clear();
            notifyDataSetChanged();
        }
    }

    public void addHeaderView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null");
        }
        final FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_HEADER_VIEW_TYPE + mHeaderList.size();
        mHeaderList.add(info);
        notifyDataSetChanged();
    }

    public void addFooterView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null!");
        }
        final FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_FOOTER_VIEW_TYPE + mFooterList.size();
        mFooterList.add(info);
        notifyDataSetChanged();
    }

    public boolean containsFooterView(View v) {
        for (int i = 0; i < mFooterList.size(); i++) {
            FixedViewInfo info = mFooterList.get(i);
            if (info.view == v) {
                return true;
            }
        }
        return false;
    }

    public boolean containsHeaderView(View v) {
        for (int i = 0; i < mHeaderList.size(); i++) {
            FixedViewInfo info = mHeaderList.get(i);
            if (info.view == v) {
                return true;
            }
        }
        return false;
    }

    public void setHeaderVisibility(boolean shouldShow) {
        for (FixedViewInfo fixedViewInfo : mHeaderList) {
            fixedViewInfo.view.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
        notifyDataSetChanged();
    }

    public void setFooterVisibility(boolean shouldShow) {
        for (FixedViewInfo fixedViewInfo : mFooterList) {
            fixedViewInfo.view.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
        notifyDataSetChanged();
    }

    private boolean isHeaderViewType(int viewType) {
        return viewType >= BASE_HEADER_VIEW_TYPE
                && viewType < (BASE_HEADER_VIEW_TYPE + mHeaderList.size());
    }

    private boolean isFooterViewType(int viewType) {
        return viewType >= BASE_FOOTER_VIEW_TYPE
                && viewType < (BASE_FOOTER_VIEW_TYPE + mFooterList.size());
    }

    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        if (mIsStaggeredGrid) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                    StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
        } else {
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
        }

        return new RecyclerView.ViewHolder(view) {
        };
    }

    public void adjustSpanSize(RecyclerView recycler) {
        if (recycler.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recycler.getLayoutManager();
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                @Override
                public int getSpanSize(int position) {
                    return isHeaderPosition(position) || isFooterPosition(position) ? layoutManager.getSpanCount() : 1;
                }

            });
        }

        if (recycler.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            this.mIsStaggeredGrid = true;
        }
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);

        mAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);

        mAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }
}
