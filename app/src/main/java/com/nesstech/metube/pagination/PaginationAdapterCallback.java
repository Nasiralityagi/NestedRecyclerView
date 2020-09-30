package com.nesstech.metube.pagination;

import androidx.recyclerview.widget.RecyclerView;

public interface PaginationAdapterCallback {

    void retryPageLoad(int adapterPosition, RecyclerView.Adapter loadingVH);
}
