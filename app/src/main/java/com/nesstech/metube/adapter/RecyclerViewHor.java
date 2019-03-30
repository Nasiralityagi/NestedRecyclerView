package com.nesstech.metube.adapter;//package com.nesstech.metube.adapter;
//
//import android.support.annotation.NonNull;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.view.View;
//
//import com.nesstech.metube.Retrofit2.VideoService;
//import com.nesstech.metube.activity.MainActivity;
//import com.nesstech.metube.adapter.VerticleRVListAdapter.VideosListViewHolder;
//import com.nesstech.metube.model.SectionDataModel;
//import com.nesstech.metube.pagination.PaginationScrollListener;
//import com.nesstech.metube.youmodel.Item;
//import com.nesstech.metube.youmodel.YoutubeData;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//class RecyclerViewHor {
//    private static final int PAGE_START = 1;
//    private static final int TOTAL_PAGES = 650;
//    private static String pageToken = "";
//    private static boolean isLoading = false;
//    private static boolean isLastPage = false;
//    private static int currentPage = PAGE_START;
//    private static VideoService movieService;
//
//    public static void bindList(VideoService movieServic, final SectionDataModel feed, final VideosListViewHolder holder, MainActivity mContext, VerticleRVListAdapter verticleRVListAdapter) {
//            movieService = movieServic;
//            holder.txtName.setText(feed.getHeaderTitle());
//            final HorizontalRVListAdapter adapter = new HorizontalRVListAdapter(mContext, verticleRVListAdapter, holder.videoItemListener);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
//            holder.recyclerView.setHasFixedSize(true);
//            holder.recyclerView.setLayoutManager(linearLayoutManager);
//            holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
//            holder.recyclerView.setAdapter(adapter);
//            holder.recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
//                @Override
//                protected void loadMoreItems() {
//                    isLoading = true;
//                    currentPage += 1;
//                    loadNextPage(holder, feed, adapter);
//                }
//
//                @Override
//                public int getTotalPageCount() {
//                    return TOTAL_PAGES;
//                }
//
//                @Override
//                public boolean isLastPage() {
//                    return isLastPage;
//                }
//
//                @Override
//                public boolean isLoading() {
//                    return isLoading;
//                }
//            });
//
//            loadFirstPage(holder, feed, adapter);
//    }
//
//    private static void loadNextPage(final VideosListViewHolder holder, final SectionDataModel feed, final HorizontalRVListAdapter adapter) {
//        //  System.out.println("Youtube data pageToken = " + pageToken + " Temp Id= " + tempId + " IDDD= " + feed.getHeaderId());
//        //  System.out.println("Youtube data currentPage = " + currentPage + " Total page= " + TOTAL_PAGES);
//
//        if (null == pageToken) {
//            pageToken = "";
//            adapter.removeLoadingFooter();
//            isLoading = false;
//            isLastPage = false;
//            return;
//        }
//
//        movieService.getTopRatedMovies("snippet,contentDetails,statistics", "mostPopular", "IN", feed.getHeaderId(), "AIzaSyAWIt3tzvIHGydiKU5UOj2GDj73rfjeeZs", pageToken).enqueue(new Callback<YoutubeData>() {
//            @Override
//            public void onResponse(@NonNull Call<YoutubeData> call, @NonNull Response<YoutubeData> response) {
//                if (response.isSuccessful()) {
//                    List<Item> results = fetchResults(response.body());
//                    adapter.removeLoadingFooter();
//                    isLoading = false;
//                    adapter.addAll(results);
//                    if (currentPage != TOTAL_PAGES)
//                        adapter.addLoadingFooter();
//                    else isLastPage = true;
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<YoutubeData> call, @NonNull Throwable t) {
//                holder.progressBar.setVisibility(View.GONE);
//            }
//        });
//    }
//
//    private static void loadFirstPage(final VideosListViewHolder holder, final SectionDataModel feed, final HorizontalRVListAdapter adapter) {
//        holder.progressBar.setVisibility(View.GONE);
//        holder.txtName.setText(feed.getHeaderTitle());
//        movieService.getTopRatedMovies1("snippet,contentDetails,statistics", "mostPopular", "IN", feed.getHeaderId(), "AIzaSyAWIt3tzvIHGydiKU5UOj2GDj73rfjeeZs").enqueue(new Callback<YoutubeData>() {
//            @Override
//            public void onResponse(@NonNull Call<YoutubeData> call, @NonNull Response<YoutubeData> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getPageInfo().getTotalResults() != 0) {
//                        List<Item> results = fetchResults(response.body());
//                        adapter.addAll(results);
//                        if (currentPage <= TOTAL_PAGES)
//                            adapter.addLoadingFooter();
//                        else isLastPage = true;
//
//                    } else {
//                        holder.progressBar.setVisibility(View.GONE);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<YoutubeData> call, @NonNull Throwable t) {
//                holder.progressBar.setVisibility(View.GONE);
//            }
//        });
//    }
//
//    private static List<Item> fetchResults(@NonNull YoutubeData data) {
//        pageToken = data.getNextPageToken();
//        return data.getItems();
//    }
//
//   /* @Override
//    public void retryPageLoad(final int position, final HorizontalRVListAdapter adapter) {
//        //loadNextPage(getItem(position), adapter);
//    }*/
//}
