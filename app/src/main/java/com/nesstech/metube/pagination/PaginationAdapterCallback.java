package com.nesstech.metube.pagination;

import android.support.v7.widget.RecyclerView;

import com.nesstech.metube.adapter.HorizontalRVListAdapter;

public interface PaginationAdapterCallback {

    void retryPageLoad(int adapterPosition, RecyclerView.Adapter loadingVH);
}
