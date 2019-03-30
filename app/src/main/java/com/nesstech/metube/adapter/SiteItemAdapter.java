package com.nesstech.metube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nesstech.metube.R;
import com.nesstech.metube.interfaces.SiteItemListener;
import com.nesstech.metube.model.ModelSites;
import com.nesstech.metube.youmodel.Item;

import java.util.List;


public class SiteItemAdapter extends RecyclerView.Adapter<SiteItemAdapter.ItemViewHolder> {

    private List<ModelSites> mValues;
    private Context mContext;
    private SetSiteItemListener mListener;

    public SiteItemAdapter(Context context, List<ModelSites> values, SetSiteItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener = itemListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.site_item_icon_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder Vholder, int position) {
        Vholder.setData(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        ModelSites item;

        ItemViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imageView);
            textView = v.findViewById(R.id.textView);
            v.setOnClickListener(this);
        }

        public void setData(ModelSites item) {
            this.item = item;
            textView.setText(item.text);
            imageView.setImageResource(item.drawable);
        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onSiteItemClick(item);
            }
        }
    }

    public interface SetSiteItemListener{
        void onSiteItemClick(ModelSites item);
    }
}