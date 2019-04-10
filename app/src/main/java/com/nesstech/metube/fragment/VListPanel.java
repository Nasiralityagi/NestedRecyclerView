package com.nesstech.metube.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nesstech.metube.R;
import com.nesstech.metube.Retrofit2.VIdeoApi;
import com.nesstech.metube.Retrofit2.VideoService;
import com.nesstech.metube.activity.MainActivity;
import com.nesstech.metube.adapter.HorizontalRVListAdapter;
import com.nesstech.metube.adapter.VListAdapter;
import com.nesstech.metube.adapter.VerticalRVListAdapter;
import com.nesstech.metube.customview.SpringRecyclerView;
import com.nesstech.metube.pagination.PaginationAdapterCallback;
import com.nesstech.metube.pagination.PaginationScrollListener;
import com.nesstech.metube.youmodel.Item;
import com.nesstech.metube.youmodel.YoutubeData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VListPanel extends Fragment implements PaginationAdapterCallback {

    private static final int GRID_SPAN = 1;
    private Button btnRetry;
    private LinearLayout errorLayout;
    private String cID;
    private VideoService movieService;
    private VListAdapter adapter;
    private SpringRecyclerView rv;
    private Context mContext;

    private int PAGE_START = 0;
    private int TOTAL_PAGES = 10;
    private String pageToken = "";
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private int itemLoadCount = 5;

    public VListPanel() {
        // Required empty public constructor
    }

    public static VListPanel newInstance(String param1) {
        VListPanel fragment = new VListPanel();
        Bundle args = new Bundle();
        args.putString("cID", param1);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cID = getArguments().getString("cID");
        } else {
            cID = "0";
        }
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video_list, container, false);
        rv = v.findViewById(R.id.main_recycler);
        rv.setHasFixedSize(false);
        errorLayout = v.findViewById(R.id.error_layout);
        btnRetry = v.findViewById(R.id.error_btn_retry);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        movieService = VIdeoApi.getClient().create(VideoService.class);

        if (!isNetworkConnected()) {
            showErrorView();
        } else {
            hideErrorView();
            setupPagination();
        }

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkConnected()) {
                    showErrorView();
                } else {
                    hideErrorView();
                    setupPagination();
                }
            }
        });
    }

    @Override
    public void onDetach() {
        System.gc();
        super.onDetach();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null;
    }

    private void showErrorView() {
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorView() {
        errorLayout.setVisibility(View.GONE);
    }

    private void setupPagination() {
        // If RecyclerView was recently bound, unbind
        adapter = new VListAdapter(((MainActivity)mContext),this);
        rv.setLayoutManager(new GridLayoutManager(mContext,GRID_SPAN, GridLayoutManager.VERTICAL, false));
        //rv.setItemAnimator(new SlideInUpAnimator());
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new PaginationScrollListener(rv.getLayoutManager()) {
            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += itemLoadCount;
                loadNextPage(cID);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }
        });

        loadFirstPage(cID);
    }

    private void loadFirstPage(final String id) {
        movieService.getTopRatedMovies("snippet,contentDetails,statistics", itemLoadCount,"mostPopular",  id, "AIzaSyAWIt3tzvIHGydiKU5UOj2GDj73rfjeeZs",pageToken).enqueue(new Callback<YoutubeData>() {
            @Override
            public void onResponse(@NonNull Call<YoutubeData> call, @NonNull Response<YoutubeData> response) {
                if (response.body() != null && response.isSuccessful()) {
                    YoutubeData data = response.body();
                    int totalResult = data.getPageInfo().getTotalResults();
                    if (totalResult > 0) {
                        TOTAL_PAGES = totalResult;
                        List<Item> results = fetchResults(data);

                        adapter.addAll(results);
                        if (currentPage <= TOTAL_PAGES)
                            adapter.addLoadingFooter();
                        else isLastPage = true;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<YoutubeData> call, @NonNull Throwable t) {
              showErrorView();
            }
        });
    }

    private List<Item> fetchResults(@NonNull YoutubeData data) {
        pageToken = data.getNextPageToken();
        return data.getItems();
    }


    private void loadNextPage(final String id) {
        if (pageToken.isEmpty()) {
            adapter.removeLoadingFooter();
            isLoading = false;
            isLastPage = true;
            return;
        }


        movieService.getTopRatedMovies("snippet,contentDetails,statistics", itemLoadCount,"mostPopular",  id, "AIzaSyAWIt3tzvIHGydiKU5UOj2GDj73rfjeeZs", pageToken).enqueue(new Callback<YoutubeData>() {
            @Override
            public void onResponse(@NonNull Call<YoutubeData> call, @NonNull Response<YoutubeData> response) {
                if (response.body() != null && response.isSuccessful()) {
                    List<Item> results = fetchResults(response.body());
                    adapter.removeLoadingFooter();
                    isLoading = false;
                    adapter.addAll(results);
                    if (currentPage != TOTAL_PAGES)
                        adapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }

            @Override
            public void onFailure(@NonNull Call<YoutubeData> call, @NonNull Throwable t) {
              showErrorView();
            }
        });
    }

    @Override
    public void retryPageLoad(final int position, final RecyclerView.Adapter adapter) {
        if (adapter instanceof VListAdapter) {
            loadNextPage(cID);
        }
    }
}
