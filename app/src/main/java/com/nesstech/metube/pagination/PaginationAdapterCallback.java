package com.nesstech.metube.pagination;

import android.support.v7.widget.RecyclerView;

import com.nesstech.metube.adapter.HorizontalRVListAdapter;

/**
 * Created by Suleiman on 16/11/16.
 */

public interface PaginationAdapterCallback {

    void retryPageLoad(int adapterPosition, RecyclerView.Adapter loadingVH);
}
