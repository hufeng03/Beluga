package com.hufeng.filemanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Feng Hu on 15-02-14.
 * <p/>
 * TODO: Add a class header comment.
 */
public class WrapContentLinearLayoutManager extends LinearLayoutManager{

    public WrapContentLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);
        int width = 0;
        int height = 0;
        for (int i = 0; i < getItemCount(); i++) {
            int[] size = measureScrapChild(recycler, i,
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED));
            if (size != null) {
                if (getOrientation() == HORIZONTAL) {
                    width = width + size[0];
                    if (height < size[1]) {
                        height = size[1];
                    }
                } else {
                    height = height + size[1];
                    if (width < size[0]) {
                        width = size[0];
                    }
                }
            }
        }
        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        if (width == 0) {
            width = widthSize;
        }

        if (height == 0) {
            height = heightSize;
        }

        setMeasuredDimension(width, height);
    }

    private int[] measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                   int heightSpec) {
        int[] size = null;
        View view = recycler.getViewForPosition(position);
        if (view != null) {
            size = new int[2];
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                    getPaddingLeft() + getPaddingRight(), p.width);
            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                    getPaddingTop() + getPaddingBottom(), p.height);
            view.measure(childWidthSpec, childHeightSpec);
            size[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
            size[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
            recycler.recycleView(view);
        }
        return size;
    }
}
