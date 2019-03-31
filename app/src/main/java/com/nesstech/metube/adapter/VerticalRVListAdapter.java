package com.nesstech.metube.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nesstech.metube.R;
import com.nesstech.metube.Retrofit2.VIdeoApi;
import com.nesstech.metube.Retrofit2.VideoService;
import com.nesstech.metube.Utility.Utills;
import com.nesstech.metube.activity.MainActivity;
import com.nesstech.metube.adapter.HorizontalRVListAdapter.SetVideoClickListener;
import com.nesstech.metube.adapter.SiteItemAdapter.SetSiteItemListener;
import com.nesstech.metube.customview.SpringRecyclerView;
import com.nesstech.metube.model.ModelSites;
import com.nesstech.metube.model.SectionDataModel;
import com.nesstech.metube.pagination.PaginationAdapterCallback;
import com.nesstech.metube.pagination.PaginationScrollListener;
import com.nesstech.metube.youmodel.Item;
import com.nesstech.metube.youmodel.YoutubeData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerticalRVListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PaginationAdapterCallback {

    private static final int HEADER = 2;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private final Context mContext;
    private final FragmentManager childFragmentManager;
    private VideoService movieService;
    private List<SectionDataModel> listItems;
    private int ONEPLUS = 0;
    private boolean isLoadingAdded = false;
    private List<Item> resultsTopTrending;
    private SetViewMoreClickListener mVrvItemListener;
    private SetVideoClickListener mHrvItemListener;
    private SetSiteItemListener mSiteItemListener;

    public VerticalRVListAdapter(List<Item> resultsTopTrending, MainActivity context, FragmentManager childFragmentManager) {
        this.listItems = new ArrayList<>();
        this.listItems.add(null);//this adding null at 0th position responsible to add header i.e. Pager
        this.mContext = context;
        this.childFragmentManager = childFragmentManager;
        this.resultsTopTrending = resultsTopTrending;
        this.mVrvItemListener = context;
        this.mHrvItemListener = context;
        this.mSiteItemListener = context;
        movieService = VIdeoApi.getClient().create(VideoService.class);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case HEADER:
                View v1 = inflater.inflate(R.layout.pager_item, parent, false);
                return new PagerViewHolder(v1);
            case ITEM:
                View v3 = inflater.inflate(R.layout.videos_list_item, parent, false);
                return new VideosListViewHolder(v3);
            case LOADING:
                View v4 = inflater.inflate(R.layout.item_progress, parent, false);
                return new LoadingVH(v4);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                final VideosListViewHolder holder = (VideosListViewHolder) vh;
                holder.bindVideoList(getItem(position));
                break;

            case HEADER:
                PagerViewHolder h1 = (PagerViewHolder) vh;
                h1.bindPagerItems();
                break;

            case LOADING:
            default:
                LoadingVH loadingVH = (LoadingVH) vh;
                loadingVH.bindLoader();
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return (position == listItems.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    private SectionDataModel getItem(int position) {
        return listItems.get(position);
    }

    public void addAll(List<SectionDataModel> listItems) {
        for (SectionDataModel result : listItems) {
            add(result);
        }
    }

    public void add(SectionDataModel r) {
        listItems.add(r);
        notifyItemInserted(listItems.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new SectionDataModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = listItems.size() - 1;
        SectionDataModel result = getItem(position);
        if (result != null) {
            listItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void retryPageLoad(final int position, final RecyclerView.Adapter adapter) {
        if (adapter instanceof HorizontalRVListAdapter) {
            //loadNextPage(getItem(position), adapter);
        }
    }

    public interface SetViewMoreClickListener {
        void onViewMoreClick(SectionDataModel model);
    }

    public interface SetPagerItemListener {
        void onPagerItemClick(Item item);
    }

    public static class Page extends Fragment {
        @SuppressWarnings("deprecation")
        @Nullable
        @Override
        public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.page, container, false);
            final SetPagerItemListener mPagerItemListener = (SetPagerItemListener) container.getContext();
            final ProgressBar mProgress = view.findViewById(R.id.movie_progress);
            final ImageView mImageView = view.findViewById(R.id.itemImage);
            mProgress.setVisibility(View.VISIBLE);
            final Item data = (Item) getArguments().getSerializable("ITEM");
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPagerItemListener != null)
                        mPagerItemListener.onPagerItemClick(data);
                }
            });

            Glide.with(getContext())
                    .load(Utills.getVideoImage(data))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // image ready, hide progress now
                            mProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(mImageView);
            return view;
        }
    }

    private class PagerViewHolder extends RecyclerView.ViewHolder {
        final ViewPager pager;

        final SpringRecyclerView recyclerView;
        final List<ModelSites> arrayList;
        final SiteItemAdapter adapter;

        PagerViewHolder(View itemView) {
            super(itemView);
            pager = itemView.findViewById(R.id.pager);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            arrayList = new ArrayList<>();
            arrayList.add(new ModelSites("Youtube", R.drawable.ic_youtube, "#09A9FF", "https://m.youtube.com/"));
            arrayList.add(new ModelSites("Facebook", R.drawable.ic_facebook, "#3E51B1", "https://m.facebook.com/"));
            arrayList.add(new ModelSites("Instagram", R.drawable.ic_instagram, "#F94336", "https://www.instagram.com/"));
            arrayList.add(new ModelSites("Dailymotion", R.drawable.ic_dailymotion, "#0A9B88", "http://www.dailymotion.com/"));
            arrayList.add(new ModelSites("Vimeo", R.drawable.ic_vimeo, "#673BB7", "https://vimeo.com/"));
            //arrayList.add(new ModelSites("Vine", R.drawable.ic_vines, "#4BAA50", "https://vine.co/"));
            adapter = new SiteItemAdapter(mContext, arrayList, mSiteItemListener);
        }

        private void bindPagerItems() {
            pager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, mContext.getResources().getDisplayMetrics()));
            PagerAdapter pagerAdapter = new FragmentPagerAdapter(childFragmentManager) {
                @Override
                public Fragment getItem(int position) {
                    int fragmentPos = position % 15;
                    Bundle args = new Bundle();
                    args.putSerializable("ITEM", resultsTopTrending.get(fragmentPos));
                    return Page.instantiate(mContext, Page.class.getName(), args);
                }

                @Override
                public int getCount() {
                    return Integer.MAX_VALUE;
                }
            };
            pager.setAdapter(pagerAdapter);
            pager.setCurrentItem((Integer.MAX_VALUE / 2));

            GridLayoutManager manager = new GridLayoutManager(mContext, 5, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }


    private class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private LinearLayout mErrorLayout;
        private boolean retryPageLoad = false;

        LoadingVH(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);
            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        private void bindLoader() {
            if (retryPageLoad) {
                mErrorLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            } else {
                if (!isNetworkConnected()) {
                    mErrorLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mErrorLayout.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        }

        private boolean isNetworkConnected() {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            return (cm != null ? cm.getActiveNetworkInfo() : null) != null;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:
                    //showRetry();
                    //mCallback.retryPageLoad(getAdapterPosition(), HorizontalRVListAdapter.this);
                    break;
            }
        }
    }

    private class VideosListViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final TextView txtName;
        private final TextView tvMore;
        private final SpringRecyclerView recyclerView;

        private int PAGE_START = 0;
        private int TOTAL_PAGES = 10;
        private String pageToken = "";
        private boolean isLoading = false;
        private boolean isLastPage = false;
        private int currentPage = PAGE_START;
        private int itemLoadCount = 5;

        VideosListViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            tvMore = view.findViewById(R.id.tvMore);
            recyclerView = view.findViewById(R.id.main_recycler);
            tvMore.setOnClickListener(this);
        }

        private void bindVideoList(final SectionDataModel feed) {
            final HorizontalRVListAdapter adapter = new HorizontalRVListAdapter(mContext, VerticalRVListAdapter.this, mHrvItemListener);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(new PaginationScrollListener(recyclerView.getLayoutManager()) {
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
                    loadNextPage(feed, adapter);
                }

                @Override
                public int getTotalPageCount() {
                    return TOTAL_PAGES;
                }
            });

            loadFirstPage(feed, adapter);
        }

        private void loadNextPage(final SectionDataModel feed, final HorizontalRVListAdapter adapter) {
            System.out.println("Youtube data pageToken = " + pageToken + " IDDD= " + feed.getHeaderId());
            System.out.println("Youtube data currentPage = " + currentPage + " Total page= " + TOTAL_PAGES);

            if (pageToken.isEmpty()) {
                adapter.removeLoadingFooter();
                isLoading = false;
                isLastPage = true;
                return;
            }

            movieService.getTopRatedMovies("snippet,contentDetails,statistics", itemLoadCount, "mostPopular", feed.getHeaderId(), "AIzaSyAWIt3tzvIHGydiKU5UOj2GDj73rfjeeZs", pageToken).enqueue(new Callback<YoutubeData>() {
                @Override
                public void onResponse(@NonNull Call<YoutubeData> call, @NonNull Response<YoutubeData> response) {
                    if (response.isSuccessful()) {
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
                }
            });
        }

        private void loadFirstPage(final SectionDataModel feed, final HorizontalRVListAdapter adapter) {
            txtName.setText("");
            movieService.getTopRatedMovies("snippet,contentDetails,statistics", itemLoadCount, "mostPopular", feed.getHeaderId(), "AIzaSyAWIt3tzvIHGydiKU5UOj2GDj73rfjeeZs", pageToken).enqueue(new Callback<YoutubeData>() {
                @Override
                public void onResponse(@NonNull Call<YoutubeData> call, @NonNull Response<YoutubeData> response) {
                    if (response.isSuccessful()) {
                        YoutubeData data = response.body();
                        int totalResult = data.getPageInfo().getTotalResults();
                        if (totalResult > 0) {
                            TOTAL_PAGES = totalResult;
                            txtName.setText(feed.getHeaderTitle());
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
                }
            });
        }

        private List<Item> fetchResults(@NonNull YoutubeData data) {
            pageToken = data.getNextPageToken();
            return data.getItems();
        }

        @Override
        public void onClick(View v) {
            if (mVrvItemListener != null)
                mVrvItemListener.onViewMoreClick(getItem(getAdapterPosition()));
        }
    }
}
