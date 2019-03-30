package com.nesstech.metube.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.nesstech.metube.R;
import com.nesstech.metube.Retrofit2.VIdeoApi;
import com.nesstech.metube.Retrofit2.VideoService;
import com.nesstech.metube.activity.MainActivity;
import com.nesstech.metube.adapter.VListAdapter;
import com.nesstech.metube.customview.SpringRecyclerView;
import com.nesstech.metube.paginate.Paginate;
import com.nesstech.metube.paginate.recycler.LoadingListItemSpanLookup;
import com.nesstech.metube.youmodel.Item;
import com.nesstech.metube.youmodel.YoutubeData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VListPanel extends Fragment implements Paginate.Callbacks {

    private static final int GRID_SPAN = 1;
    private Button btnRetry;
    private LinearLayout errorLayout;
    private String cID;
    private VideoService movieService;
    private boolean loading = false;
    private int page = 0;
    private Paginate paginate;
    private int totalPages;
    private VListAdapter adapter;
    private SpringRecyclerView rv;
    private String pageToken="";
    private Context mContext;
    private int itemLoadCount=5;

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
        if (paginate != null) {
            paginate.unbind();
        }
        loadFirstPage(cID);
        adapter = new VListAdapter((MainActivity) mContext);
        rv.setLayoutManager(new GridLayoutManager(mContext,GRID_SPAN, GridLayoutManager.VERTICAL, false));
        //rv.setItemAnimator(new SlideInUpAnimator());
        rv.setAdapter(adapter);
        paginate = Paginate.with(rv, this)
                .setLoadingTriggerThreshold(4)
                .addLoadingListItem(true)
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return GRID_SPAN;
                    }
                })
                .build();

    }

    private void loadFirstPage(final String id) {
        movieService.getTopRatedMovies("snippet,contentDetails,statistics", itemLoadCount,"mostPopular",  id, "AIzaSyAWIt3tzvIHGydiKU5UOj2GDj73rfjeeZs",pageToken).enqueue(new Callback<YoutubeData>() {
            @Override
            public void onResponse(@NonNull Call<YoutubeData> call, @NonNull Response<YoutubeData> response) {
                if (response.isSuccessful()) {
                    YoutubeData data = response.body();
                    totalPages = data.getPageInfo().getTotalResults();
                    if (totalPages > 0) {
                        List<Item> results = fetchResults(data);
                        addLoadedData(results);
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

    private void addLoadedData(List<Item> results) {
        page = page + results.size();
        adapter.add(results);
        loading = false;
    }


    /*@Override
    public void retryPageLoad(final int position, final RecyclerView.Adapter adapter) {
        if (adapter instanceof AllVideoRVListAdapter) {
            AllVideoRVListAdapter adapter1 = (AllVideoRVListAdapter) adapter;
            loadNextPage(cID, adapter1);
        }
    }*/

    private void loadNextPage(final String id) {
        if (pageToken.isEmpty()) {
            page = totalPages;
            loading = false;
            return;
        }


        movieService.getTopRatedMovies("snippet,contentDetails,statistics", itemLoadCount,"mostPopular",  id, "AIzaSyAWIt3tzvIHGydiKU5UOj2GDj73rfjeeZs", pageToken).enqueue(new Callback<YoutubeData>() {
            @Override
            public void onResponse(@NonNull Call<YoutubeData> call, @NonNull Response<YoutubeData> response) {
                if (response.isSuccessful()) {
                    List<Item> results = fetchResults(response.body());
                    addLoadedData(results);
                }
            }

            @Override
            public void onFailure(@NonNull Call<YoutubeData> call, @NonNull Throwable t) {
              showErrorView();
            }
        });
    }

    @Override
    public synchronized void onLoadMore() {
        Log.d("Paginate", "onLoadMore");
        loading = true;
        loadNextPage(cID);
    }

    @Override
    public synchronized boolean isLoading() {
        return loading; // Return boolean weather data is already loading or not
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == totalPages; // If all pages are loaded return true
    }

}