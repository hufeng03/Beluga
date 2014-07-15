package com.hufeng.filemanager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class GridFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    static final int INTERNAL_EMPTY_ID = 0x00ff0001;
    static final int INTERNAL_PROGRESS_CONTAINER_ID = 0x00ff0002;
    static final int INTERNAL_LIST_CONTAINER_ID = 0x00ff0003;

    final private Handler mHandler = new Handler();

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mGrid.focusableViewAvailable(mGrid);
        }
    };

    final private AdapterView.OnItemClickListener mOnClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onGridItemClick((GridView)parent, v, position, id);
        }
    };

    final private AdapterView.OnItemLongClickListener mOnLongClickListener
            = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            onGridItemSelect((GridView) parent, v, position, id);
            return true;
        }
    };

    @Override
    public void onRefresh() {

    }

    public enum DISPLAY_MODE {
        LIST, GRID;

        public static DISPLAY_MODE valueOf(int value) {
            switch (value) {
                case 0:
                    return LIST;
                case 1:
                    return GRID;
                default:
                    return null;
            }
        }
    }

    private DISPLAY_MODE mDisplayMode = DISPLAY_MODE.LIST;

    GridAdapter mAdapter;
    GridView mGrid;
    View mEmptyView;
    TextView mStandardEmptyView;
    View mProgressContainer;
    View mGridContainer;
    CharSequence mEmptyText;
    boolean mGridShown;
    private View mRootView;
    private SwipeRefreshLayout mRefreshLayout;

    public GridFragment() {
    }

    /**
     * Provide default implementation to return a simple list view.  Subclasses
     * can override to replace with their own layout.  If doing so, the
     * returned view hierarchy <em>must</em> have a GridView whose id
     * is {@link android.R.id#list android.R.id.list} and can optionally
     * have a sibling view id {@link android.R.id#empty android.R.id.empty}
     * that is to be shown when the list is empty.
     *
     * <p>If you are overriding this method with your own custom content,
     * consider including the standard layout {@link android.R.layout#list_content}
     * in your layout file, so that you continue to retain all of the standard
     * behavior of GridFragment.  In particular, this is currently the only
     * way to have the built-in indeterminant progress state be shown.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getActivity();

        FrameLayout root = new FrameLayout(context);

        // refreshing progress bar--------------------------------------------

        LinearLayout pframe = new LinearLayout(context);
        pframe.setId(INTERNAL_PROGRESS_CONTAINER_ID);
        pframe.setOrientation(LinearLayout.VERTICAL);
        pframe.setVisibility(View.GONE);
        pframe.setGravity(Gravity.CENTER);

        ProgressBar progress = new ProgressBar(context, null,
                android.R.attr.progressBarStyleLarge);
        pframe.addView(progress, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        root.addView(pframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // ------------------------------------------------------------------

        FrameLayout lframe = new FrameLayout(context);
        lframe.setId(INTERNAL_LIST_CONTAINER_ID);

        TextView tv = new TextView(getActivity());
        tv.setId(INTERNAL_EMPTY_ID);
        tv.setGravity(Gravity.CENTER);
        tv.setClickable(true);
        lframe.addView(tv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mRefreshLayout = new SwipeRefreshLayout(context);

        GridView lv = new MyGridView(getActivity());
        lv.setId(android.R.id.list);
        lv.setDrawSelectorOnTop(false);
        lv.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        lv.setCacheColorHint(0);
//        lv.setBackgroundColor(getResources().getColor(R.color.red));
        mRefreshLayout.addView(lv, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

//        mRefreshLayout.setBackgroundColor(getResources().getColor(R.color.blue));

        if(mDisplayMode == DISPLAY_MODE.GRID) {
            setDisplayModeAsGrid(lv);
        } else {
            setDisplayModeAsList(lv);
        }

        lframe.addView(mRefreshLayout, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        root.addView(lframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // ------------------------------------------------------------------


        mRootView = root;

        mRootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initSwipeOptions();

        return mRootView;
    }

    private void initSwipeOptions() {
        mRefreshLayout.setOnRefreshListener(this);
        setAppearance();
        disableSwipe();
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void showSwipeProgress() {
        mRefreshLayout.setRefreshing(true);
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void hideSwipeProgress() {
        mRefreshLayout.setRefreshing(false);
    }

    /**
     * Enables swipe gesture
     */
    public void enableSwipe() {
        mRefreshLayout.setEnabled(true);
    }

    /**
     * Disables swipe gesture. It prevents manual gestures but keeps the option tu show
     * refreshing programatically.
     */
    public void disableSwipe() {
        mRefreshLayout.setEnabled(false);
    }

    private void setAppearance() {
        mRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void setRootShown(boolean shown) {
        if (shown) {
            mRootView.setVisibility(View.VISIBLE);
        } else {
            mRootView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Attach to list view once the view hierarchy has been created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureGrid();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {
        mHandler.removeCallbacks(mRequestFocus);
        mGrid = null;
        mGridShown = false;
        mEmptyView = mProgressContainer = mGridContainer = null;
        mStandardEmptyView = null;
        super.onDestroyView();
    }

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getGridView().getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param g The GridView where the click happened
     * @param v The view that was clicked within the GridView
     * @param position The position of the view in the list
     * @param id The row id of the item that was clicked
     */
    public void onGridItemClick(GridView g, View v, int position, long id) {
    }

    public void onGridItemSelect(GridView g, View v, int position, long id) {
    }

    /**
     * Provide the cursor for the list view.
     */
    public void setGridAdapter(GridAdapter adapter) {
        boolean hadAdapter = mAdapter != null;
        mAdapter = adapter;
        if (mGrid != null) {
            mGrid.setAdapter(adapter);
            if(mDisplayMode!=adapter.getDisplayMode()) {
                switchDisplayMode();
            }
            if (!mGridShown && !hadAdapter) {
                // The list was hidden, and previously didn't have an
                // adapter.  It is now time to show it.
                setGridShown(true, getView().getWindowToken() != null);
            }
        }
    }

    /**
     * Set the currently selected list item to the specified
     * position with the adapter's data
     *
     * @param position
     */
    public void setSelection(int position) {
        ensureGrid();
        mGrid.setSelection(position);
    }

    /**
     * Get the position of the currently selected list item.
     */
    public int getSelectedItemPosition() {
        ensureGrid();
        return mGrid.getSelectedItemPosition();
    }

    /**
     * Get the cursor row ID of the currently selected list item.
     */
    public long getSelectedItemId() {
        ensureGrid();
        return mGrid.getSelectedItemId();
    }

    /**
     * Get the activity's list view widget.
     */
    public GridView getGridView() {
        ensureGrid();
        return mGrid;
    }

    /**
     * The default content for a GridFragment has a TextView that can
     * be shown when the list is empty.  If you would like to have it
     * shown, call this method to supply the text it should use.
     */
    public void setEmptyText(CharSequence text) {
        ensureGrid();
        if (mStandardEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        mStandardEmptyView.setText(text);
        if (mEmptyText == null) {
            mGrid.setEmptyView(mStandardEmptyView);
        }
        mEmptyText = text;
    }

    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     *
     * <p>Applications do not normally need to use this themselves.  The default
     * behavior of GridFragment is to start with the list not being shown, only
     * showing it once an adapter is given with {@link #setGridAdapter(GridAdapter)}.
     * If the list at that point had not been shown, when it does get shown
     * it will be do without the user ever seeing the hidden state.
     *
     * @param shown If true, the list view is shown; if false, the progress
     * indicator.  The initial value is true.
     */
    public void setGridShown(boolean shown) {
        setGridShown(shown, true);
    }

    /**
     * Like {@link #setGridShown(boolean)}, but no animation is used when
     * transitioning from the previous state.
     */
    public void setGridShownNoAnimation(boolean shown) {
        setGridShown(shown, false);
    }

    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     *
     * @param shown If true, the list view is shown; if false, the progress
     * indicator.  The initial value is true.
     * @param animate If true, an animation will be used to transition to the
     * new state.
     */
    private void setGridShown(boolean shown, boolean animate) {
        ensureGrid();
        if (mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mGridShown == shown) {
            return;
        }
        mGridShown = shown;
        if (shown) {
//            if (animate) {
//                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
//                        getActivity(), android.R.anim.fade_out));
//                mGridContainer.startAnimation(AnimationUtils.loadAnimation(
//                        getActivity(), android.R.anim.fade_in));
//            } else {
                mProgressContainer.clearAnimation();
                mGridContainer.clearAnimation();
//            }
            mProgressContainer.setVisibility(View.GONE);
            mGridContainer.setVisibility(View.VISIBLE);
        } else {
//            if (animate) {
//                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
//                        getActivity(), android.R.anim.fade_in));
//                mGridContainer.startAnimation(AnimationUtils.loadAnimation(
//                        getActivity(), android.R.anim.fade_out));
//            } else {
                mProgressContainer.clearAnimation();
                mGridContainer.clearAnimation();
//            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mGridContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Get the GridAdapter associated with this activity's GridView.
     */
    public GridAdapter getGridAdapter() {
        return mAdapter;
    }

    public DISPLAY_MODE switchDisplayMode() {
        ensureGrid();
        if (mDisplayMode == DISPLAY_MODE.GRID) {
            mDisplayMode = DISPLAY_MODE.LIST;
            setDisplayModeAsList(mGrid);
        } else if(mDisplayMode == DISPLAY_MODE.LIST) {
            mDisplayMode = DISPLAY_MODE.GRID;
            setDisplayModeAsGrid(mGrid);
        }
        mAdapter.changeDisplayMode(mDisplayMode);
        return mDisplayMode;
    }

    private void setDisplayModeAsGrid(GridView grid) {
        grid.setNumColumns(GridView.AUTO_FIT);
        grid.setColumnWidth(getResources().getDimensionPixelSize(R.dimen.grid_fragment_column_width));
        grid.setVerticalSpacing(getResources().getDimensionPixelOffset(R.dimen.grid_fragment_vertical_spacing));
        grid.setHorizontalSpacing(getResources().getDimensionPixelOffset(R.dimen.grid_fragment_horizontal_spacing));
        int padding = getResources().getDimensionPixelOffset(R.dimen.grid_fragment_padding);
        grid.setPadding(padding, padding, padding, padding);
        grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setDisplayModeAsList(GridView grid) {
        grid.setNumColumns(1);
        grid.setVerticalSpacing(0);
        grid.setHorizontalSpacing(0);
        grid.setPadding(0, 0, 0, 0);
//        grid.setSelector(android.R.drawable.list_selector_background);
        grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    public DISPLAY_MODE getDisplayMode() {
        return mDisplayMode;
    }


    private void ensureGrid() {
        if (mGrid != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof GridView) {
            mGrid = (GridView)root;
        } else {
            mStandardEmptyView = (TextView)root.findViewById(INTERNAL_EMPTY_ID);
            if (mStandardEmptyView == null) {
                mEmptyView = root.findViewById(android.R.id.empty);
            } else {
                mStandardEmptyView.setVisibility(View.GONE);
            }
            mProgressContainer = root.findViewById(INTERNAL_PROGRESS_CONTAINER_ID);
            mGridContainer = root.findViewById(INTERNAL_LIST_CONTAINER_ID);
            View rawGridView = root.findViewById(android.R.id.list);
            if (!(rawGridView instanceof GridView)) {
                if (rawGridView == null) {
                    throw new RuntimeException(
                            "Your content must have a GridView whose id attribute is " +
                                    "'android.R.id.list'");
                }
                throw new RuntimeException(
                        "Content has view with id attribute 'android.R.id.list' "
                                + "that is not a GridView class");
            }
            mGrid = (GridView)rawGridView;
            if (mEmptyView != null) {
                mGrid.setEmptyView(mEmptyView);
            } else if (mEmptyText != null) {
                mStandardEmptyView.setText(mEmptyText);
                mGrid.setEmptyView(mStandardEmptyView);
            }
        }
        mGridShown = true;
        mGrid.setOnItemClickListener(mOnClickListener);
        mGrid.setOnItemLongClickListener(mOnLongClickListener);
        if (mAdapter != null) {
            GridAdapter adapter = mAdapter;
            mAdapter = null;
            setGridAdapter(adapter);
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            if (mProgressContainer != null) {
                setGridShown(false, false);
            }
        }
        mHandler.post(mRequestFocus);
    }

}
