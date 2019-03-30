package com.nesstech.metube.customview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class TempRecyclerView extends RecyclerView {

    protected TempRecyclerView(Context context) {
        this(context, null);
    }

    protected TempRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    protected TempRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

   // @Override
    protected void absorbGlows(int velocityX, int velocityY) {}
}
