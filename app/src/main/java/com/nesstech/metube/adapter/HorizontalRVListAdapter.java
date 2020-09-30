package com.nesstech.metube.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nesstech.metube.R;
import com.nesstech.metube.Utility.Utills;
import com.nesstech.metube.pagination.PaginationAdapterCallback;
import com.nesstech.metube.youmodel.Item;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HorizontalRVListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private int viewShowPos = -1;

    private List<Item> movieResults;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private SetVideoClickListener mListener;
    private PaginationAdapterCallback mCallback;

    HorizontalRVListAdapter(Context context, VerticalRVListAdapter ctx, SetVideoClickListener mListener, int viewShowPos) {
        this.context = context;
        this.mCallback = ctx;
        movieResults = new ArrayList<>();
        this.mListener = mListener;
        this.viewShowPos = viewShowPos;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                View v1 = null;
                if (viewShowPos == 2) {
                    v1 = inflater.inflate(R.layout.list_single_card_vertical, parent, false);
                } else if (viewShowPos == 4) {
                    v1 = inflater.inflate(R.layout.list_single_card_horizontal, parent, false);
                } else if (viewShowPos == 6) {
                    v1 = inflater.inflate(R.layout.list_single_card_vertical, parent, false);
                } else if (viewShowPos == 8) {
                    v1 = inflater.inflate(R.layout.list_single_card_horizontal, parent, false);
                } else {
                    v1 = inflater.inflate(R.layout.list_single_card_new, parent, false);
                }
                viewHolder = new SingleItemRowHolder(v1);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progres, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                final SingleItemRowHolder holder = (SingleItemRowHolder) vh;
                holder.bindData(movieResults.get(position));
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) vh;
                loadingVH.bindLoader();
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return movieResults == null ? 0 : movieResults.size();
    }

    private void performClick(int id, Item result) {
        switch (id) {
            case R.id.overflow:
                if (mListener != null) {
                    mListener.onMoreIconClick(result);
                }
                break;
            default:
                if (mListener != null) {
                    mListener.onVideoItemClick(result);
                }
                break;
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null;
    }

    /*Helper*/
    void addAll(List<Item> moveResults) {
        for (Item result : moveResults) {
            add(result);
        }
    }

    private void add(Item r) {
        movieResults.add(r);
        notifyItemInserted(movieResults.size() - 1);
    }

    void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Item());
    }

    void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = movieResults.size() - 1;
        Item result = getItem(position);
        if (result != null) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Item getItem(int position) {
        return movieResults.get(position);
    }


    private void showRetry() {
        retryPageLoad = false;
        notifyItemChanged(movieResults.size() - 1);
    }


    public interface SetVideoClickListener {
        void onVideoItemClick(Item item);

        void onMoreIconClick(Item item);

        void onVideoItemLongClick(Item item);
    }

    /* View Holders*/
    private class SingleItemRowHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView tvTitle;
        TextView chanelName;
        TextView tvDuration;
        TextView views;
        TextView date;
        ImageView overflow;
        ProgressBar mProgress;
        View itemView;

        private SingleItemRowHolder(View view) {
            super(view);
            itemView = view;
            itemImage = view.findViewById(R.id.itemImage);
            tvTitle = view.findViewById(R.id.tvTitle);
            chanelName = view.findViewById(R.id.chanel_name);
            tvDuration = view.findViewById(R.id.tv_duration);
            views = view.findViewById(R.id.views);
            date = view.findViewById(R.id.date);
            overflow = view.findViewById(R.id.overflow);
            mProgress = view.findViewById(R.id.movie_progress);
        }

        private void bindData(final Item result) {
            if (result == null)
                return;
            String videoChannelTitle = result.getSnippet().getChannelTitle() == null ? "Unknown" : result.getSnippet().getChannelTitle();
            String videoImage = result.getSnippet().getThumbnails().getHigh().getUrl() == null ?
                    (result.getSnippet().getThumbnails().getMedium().getUrl() == null ?
                            (result.getSnippet().getThumbnails().getDefault().getUrl() == null ?
                                    result.getSnippet().getThumbnails().getStandard().getUrl() : result.getSnippet().getThumbnails().getDefault().getUrl()) : result.getSnippet().getThumbnails().getMedium().getUrl()) :
                    result.getSnippet().getThumbnails().getHigh().getUrl();
            String videoUploadTime = result.getSnippet().getPublishedAt() == null ? "2017-09-23T17:15:33.000Z" : result.getSnippet().getPublishedAt();
            String videoTitle = result.getSnippet().getTitle() == null ? "Unknown" : result.getSnippet().getTitle();
            // String videoId = result.getId() == null ? "1" : result.getId();
            String videoDuration = result.getContentDetails().getDuration() == null ? "PT5M11S" : result.getContentDetails().getDuration();
            String videoViewCount = result.getStatistics().getViewCount() == null ? "260930" : result.getStatistics().getViewCount();
            tvTitle.setText(videoTitle);
            Utills.setThumbVideo(context, videoImage, mProgress, itemImage);
            chanelName.setText(context.getString(R.string.by).concat(" ").concat(videoChannelTitle));
            tvDuration.setText(Utills.parseDuration(videoDuration));
            views.setText(NumberFormat.getNumberInstance(Locale.ENGLISH).format(Integer.parseInt(videoViewCount)).concat(" ").concat(context.getString(R.string.views)));
            date.setText(Utills.parseDate(videoUploadTime));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performClick(v.getId(), result);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null) {
                        mListener.onVideoItemLongClick(result);
                    }
                    return false;
                }
            });

            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performClick(v.getId(), result);
                }
            });

        }
    }

    private class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private LinearLayout mErrorLayout;

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

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:
                    showRetry();
                    mCallback.retryPageLoad(getAdapterPosition(), HorizontalRVListAdapter.this);
                    break;
            }
        }
    }

}
