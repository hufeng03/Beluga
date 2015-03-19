package com.belugamobile.filemanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

/**
 * Created by Feng Hu on 15-01-23.
 * <p/>
 * TODO: Add a class header comment.
 */
public abstract class BelugaRecyclerFragment extends BelugaBaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        View.OnFocusChangeListener,
        View.OnTouchListener,
        View.OnClickListener {

    View mRootView;

    LinearLayout mProgressContainer;
    ProgressBar mProgress;

    LinearLayout mEmptyContainer;
    TextView mEmpty;

    SwipeRefreshLayout mRefreshContainer;
    RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    GridLayoutManager mLayoutManager;

    FloatingActionButton mFab;

    private boolean mListShown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.beluga_list_fragment, container, false);

        mProgressContainer = (LinearLayout)root.findViewById(R.id.progress_container);
        mProgress = (ProgressBar)root.findViewById(R.id.progress);

        mEmptyContainer = (LinearLayout)root.findViewById(R.id.empty_container);
        mEmpty = (TextView)root.findViewById(R.id.empty);

        mRecyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(mRecyclerView.getContext(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRefreshContainer = (SwipeRefreshLayout)root.findViewById(R.id.list_refresh_container);
        mRefreshContainer.setOnRefreshListener(this);

        mRecyclerView.setOnFocusChangeListener(this);
        mRecyclerView.setOnTouchListener(this);

        mFab = (FloatingActionButton)root.findViewById(R.id.fab);

        mFab.attachToRecyclerView(mRecyclerView);

        mFab.setOnClickListener(this);

        mRootView = root;

        return root;
    }

    public void setLayoutManager(GridLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public GridLayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        disableSwipe();
        disableFloatingActionButton();



    }

    /**
     * Enables swipe gesture
     */
    public void enableSwipe() {
        mRefreshContainer.setEnabled(true);
    }

    /**
     * Disables swipe gesture. It prevents manual gestures but keeps the option tu show
     * refreshing programatically.
     */
    public void disableSwipe() {
        mRefreshContainer.setEnabled(false);
    }

    @Override
    public void onRefresh() {

    }

    protected void onCreate() {
        Toast.makeText(getActivity(), "onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            onCreate();
        }
    }

    private void hideSoftKeyboard() {
        if (mContext == null) {
            return;
        }
        // Hide soft keyboard, if visible
        InputMethodManager inputMethodManager = (InputMethodManager)
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mRecyclerView.getWindowToken(), 0);
    }

    /**
     * Dismisses the soft keyboard when the list takes focus.
     */
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (view == mRecyclerView && hasFocus) {
            hideSoftKeyboard();
        }
    }

    /**
     * Dismisses the soft keyboard when the list is touched.
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (view == mRecyclerView) {
            hideSoftKeyboard();
        }
        return false;
    }

    public void refresh() {

    }

    private void setRecyclerViewShown(boolean shown, boolean animate) {
        if (mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mRefreshContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mRefreshContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mRefreshContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mRefreshContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mRefreshContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mRefreshContainer.setVisibility(View.GONE);
        }
    }


    /**
     * Get the activity's list view widget.
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     *
     * <p>Applications do not normally need to use this themselves.  The default
     * behavior of ListFragment is to start with the list not being shown, only
     * showing it once an adapter is given with {@link #setRecyclerAdapter(RecyclerView.Adapter)}.
     * If the list at that point had not been shown, when it does get shown
     * it will be do without the user ever seeing the hidden state.
     *
     * @param shown If true, the list view is shown; if false, the progress
     * indicator.  The initial value is true.
     */
    public void setRecyclerViewShown(boolean shown) {
        setRecyclerViewShown(shown, true);
    }

    public void setEmptyViewShown(boolean shown) {
        mEmpty.setVisibility(shown?View.VISIBLE:View.GONE);
    }

    /**
     * Like {@link #setRecyclerViewShown(boolean, boolean)}, but no animation is used when
     * transitioning from the previous state.
     */
    public void setRecyclerViewShownNoAnimation(boolean shown) {
        setRecyclerViewShown(shown, false);
    }

    /**
     * The default content for a ListFragment has a TextView that can
     * be shown when the list is empty.  If you would like to have it
     * shown, call this method to supply the text it should use.
     */
    public void setEmptyText(CharSequence text) {
        mEmpty.setText(text);
    }

    /**
     * Provide the cursor for the list view.
     */
    public void setRecyclerAdapter(RecyclerView.Adapter adapter) {
        boolean hadAdapter = mAdapter != null;
        mAdapter = adapter;
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
            if (!mListShown && !hadAdapter) {
                // The list was hidden, and previously didn't have an
                // adapter.  It is now time to show it.
                setRecyclerViewShown(true, getView().getWindowToken() != null);
            }
        }
    }

    /**
     * Get the ListAdapter associated with this activity's ListView.
     */
    public RecyclerView.Adapter getRecyclerAdapter() {
        return mAdapter;
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void showSwipeProgress() {
        mRefreshContainer.setRefreshing(true);
    }

    /**
     * It hides the SwipeRefreshLayout progress
     */
    public void hideSwipeProgress() {
        mRefreshContainer.setRefreshing(false);
    }

    public void enableFloatingActionButton() {
        mFab.setVisibility(View.VISIBLE);
    }

    public void disableFloatingActionButton() {
        mFab.setVisibility(View.GONE);
    }

    public void hideFloatingActionButton(boolean animation) {
        mFab.hide(animation);
    }

    public void showFloatingActionButton(boolean animation) {
        mFab.show(animation);
    }

    public void setFloatingActionBarImage(int image) {
        mFab.setImageResource(image);
    }
}
