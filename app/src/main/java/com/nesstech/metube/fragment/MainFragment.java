package com.nesstech.metube.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nesstech.metube.R;
import com.nesstech.metube.Retrofit2.VIdeoApi;
import com.nesstech.metube.Retrofit2.VideoService;
import com.nesstech.metube.Utility.Constant;
import com.nesstech.metube.activity.MainActivity;
import com.nesstech.metube.adapter.VerticalRVListAdapter;
import com.nesstech.metube.customview.SpringRecyclerView;
import com.nesstech.metube.model.SectionDataModel;
import com.nesstech.metube.pagination.PaginationScrollListener;
import com.nesstech.metube.youmodel.Item;
import com.nesstech.metube.youmodel.YoutubeData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    private int TOTAL_PAGES = 3;
    private int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private int itemCount = -1;

    private LinearLayout errorLayout;
    private VerticalRVListAdapter adapter;
    private Context mContext;
    private String[] sectionkey, sectionName;
    private ProgressBar mProgress;

    public static MainFragment getInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = getContext();
        mProgress = rootView.findViewById(R.id.progress);
        final SpringRecyclerView rv = rootView.findViewById(R.id.main_recycler);
        /**setHasFixedSize(true) means the RecyclerView has children (items)
         * that has fixed width and height. This allows the RecyclerView
         * to optimize better by figuring out the exact height and width
         * of the entire list based on the your adapter.{@link setHasFixedSize()true}*/
        rv.setHasFixedSize(true);
        /**These below line help to consistent list data in recyclerview vertically
         * Here In my list vertically having only 10 items which i don't want to rebind again
         * getRecycledViewPool()
         * setItemViewCacheSize()
         * both method helps you not to recycle vertical views
         * But if your list is too large then it will cause you OOM error
         */
        rv.getRecycledViewPool().setMaxRecycledViews(0, 10);
        rv.setItemViewCacheSize(10);
        errorLayout = rootView.findViewById(R.id.error_layout);
        Button btnRetry = rootView.findViewById(R.id.error_btn_retry);
        sectionkey = getResources().getStringArray(R.array.youtube_category_video_id_key);
        sectionName = getResources().getStringArray(R.array.youtube_category_video_id_value);
        VideoService movieService = VIdeoApi.getClient().create(VideoService.class);
        if (!isNetworkConnected()) {
            showErrorView();
        } else {
            hideErrorView();
            movieService.getToTrandingVideo(Constant.part, Constant.max_result, Constant.filter, Constant.region_code, Constant.api_key).enqueue(new Callback<YoutubeData>() {
                @Override
                public void onResponse(@NonNull Call<YoutubeData> call, @NonNull Response<YoutubeData> response) {
                    if (response.body()!=null && response.isSuccessful()) {
                        if (response.body().getPageInfo().getTotalResults() > 0) {
                            mProgress.setVisibility(View.GONE);
                            List<Item> resultsTopTrending = response.body().getItems();
                            adapter = new VerticalRVListAdapter(resultsTopTrending, ((MainActivity) getContext()), getChildFragmentManager());
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
                                    // currentPage += itemCount;
                                    currentPage += 1;
                                    loadNextPage();
                                }

                                @Override
                                public int getTotalPageCount() {
                                    return TOTAL_PAGES;
                                }
                            });

                            loadData();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<YoutubeData> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(),"Failed to get response!",Toast.LENGTH_SHORT).show();
                }
            });
        }
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        return rootView;
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @serialData
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null;
    }

    private void showErrorView() {
        mProgress.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorView() {
        errorLayout.setVisibility(View.GONE);
    }

    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<SectionDataModel> feed = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    itemCount++;
                    String name = sectionName[itemCount];
                    String key = sectionkey[itemCount];
                    Log.e("Postition value load n " + key, " " + name + " " + itemCount);
                    SectionDataModel dm = new SectionDataModel();
                    dm.setHeaderId(key);
                    dm.setHeaderTitle(name);
                    feed.add(dm);
                }
                adapter.removeLoadingFooter();
                isLoading = false;
                adapter.addAll(feed);

                if (currentPage != TOTAL_PAGES)
                    adapter.addLoadingFooter();
                else isLastPage = true;
            }
        }, 500);
    }

    private void loadData() {
        if (!isNetworkConnected()) {
            showErrorView();
        } else {
            hideErrorView();
            loadFirstPage();
        }
    }

    private void loadFirstPage() {
        if (adapter == null)
            return;
        List<SectionDataModel> feed = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            itemCount++;
            SectionDataModel dm = new SectionDataModel();
            String name = sectionName[itemCount];
            String key = sectionkey[itemCount];
            Log.e("Postition value load f " + key, " " + name + " " + itemCount);
            dm.setHeaderId(key);
            dm.setHeaderTitle(name);
            feed.add(dm);
        }

        adapter.addAll(feed);
        if (currentPage <= TOTAL_PAGES)
            adapter.addLoadingFooter();
        else isLastPage = true;
    }
}
