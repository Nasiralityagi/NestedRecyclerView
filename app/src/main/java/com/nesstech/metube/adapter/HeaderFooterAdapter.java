package com.nesstech.metube.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nesstech.metube.R;
import com.nesstech.metube.activity.MainActivity;

public class HeaderFooterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private MainActivity mContext;

    public HeaderFooterAdapter(MainActivity context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pager_item, parent, false);
            return new HeaderrViewHolder(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sites_item, parent, false);
            return new GenericViewHolder(v);
        } else if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item, parent, false);
            return new FooterViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /*if (holder instanceof SiteViewHolder) {
            // TODO: 7/25/2017

           *//*IMPLEMENT YOUR OWN CODE FOR HEADER FRAGMENT*//*
        } else*/
        if (holder instanceof HeaderrViewHolder) {
            HeaderrViewHolder fVH = (HeaderrViewHolder) holder;
            fVH.pager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, mContext.getResources().getDisplayMetrics()));
            fVH.pager.setAdapter(new FragmentStatePagerAdapter(mContext.getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    Bundle args = new Bundle();
                    switch (position) {
                        case 0:
                            args.putInt("bg", R.drawable.bg_red);
                            break;
                        case 1:
                            args.putInt("bg", R.drawable.bg_blue);
                            break;
                        case 2:
                            args.putInt("bg", R.drawable.bg_yellow);
                            break;
                        case 3:
                            args.putInt("bg", R.drawable.bg_voilet);
                            break;
                        case 4:
                            args.putInt("bg", R.drawable.bg_green);
                            break;
                    }
                    return Page.instantiate(mContext, Page.class.getName(), args);
                }

                @Override
                public int getCount() {
                    return 5;
                }
            });
        } /*else if (holder instanceof FooterViewHolder) {
            // TODO: 7/25/2017
           *//*IMPLEMENT YOUR OWN CODE FOR GENERIC FRAGMENT*//*
        }*/
    }

    //    need to override this method
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position == 2;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public static class Page extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.page, container, false);
            view.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), getArguments().getInt("bg")));
            return view;
        }
    }

    class HeaderrViewHolder extends RecyclerView.ViewHolder {
        ViewPager pager;

        HeaderrViewHolder(View itemView) {
            super(itemView);
            pager = itemView.findViewById(R.id.pager);
        }
    }

    private class GenericViewHolder extends RecyclerView.ViewHolder {
        GenericViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
