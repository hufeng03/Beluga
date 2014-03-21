package com.hufeng.filemanager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hufeng.filemanager.treeview.AbstractTreeViewAdapter;
import com.hufeng.filemanager.treeview.TreeViewList;


public class TreeFragment extends BaseFragment {
    static final int INTERNAL_EMPTY_ID = 0x00ff0001;
    static final int INTERNAL_PROGRESS_CONTAINER_ID = 0x00ff0002;
    static final int INTERNAL_TREE_CONTAINER_ID = 0x00ff0003;

    final private Handler mHandler = new Handler();

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mTree.focusableViewAvailable(mTree);
        }
    };

    final private AdapterView.OnItemClickListener mOnClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onTreeItemClick((TreeViewList) parent, v, position, id);
        }
    };

    public void onTreeItemClick(TreeViewList parent, View v, int position, long id) {
    }

    AbstractTreeViewAdapter mAdapter;
    TreeViewList mTree;
    View mEmptyView;
    TextView mStandardEmptyView;
    View mProgressContainer;
    View mTreeContainer;
    CharSequence mEmptyText;
    boolean mTreeShown;

    public TreeFragment() {
    }

    /**
     * Provide default implementation to return a simple list view.  Subclasses
     * can override to replace with their own layout.  If doing so, the
     * returned view hierarchy <em>must</em> have a TreeViewList whose id
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

        // ------------------------------------------------------------------

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
        lframe.setId(INTERNAL_TREE_CONTAINER_ID);

        TextView tv = new TextView(getActivity());
        tv.setId(INTERNAL_EMPTY_ID);
        tv.setGravity(Gravity.CENTER);
        lframe.addView(tv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TreeViewList lv = new TreeViewList(getActivity());
        lv.setId(android.R.id.list);
//        lv.setDrawSelectorOnTop(false);
//        lv.setStretchMode(TreeViewList.STRETCH_COLUMN_WIDTH);
        lframe.addView(lv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        root.addView(lframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // ------------------------------------------------------------------

        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return root;
    }

    /**
     * Attach to list view once the view hierarchy has been created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureTree();
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
        mTree = null;
        mTreeShown = false;
        mEmptyView = mProgressContainer = mTreeContainer = null;
        mStandardEmptyView = null;
        super.onDestroyView();
    }

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getTreeViewList().getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param g The TreeViewList where the click happened
     * @param v The view that was clicked within the TreeViewList
     * @param position The position of the view in the list
     * @param id The row id of the item that was clicked
     */
    public void mTreeItemClick(TreeViewList g, View v, int position, long id) {
    }

    /**
     * Provide the cursor for the list view.
     */
    public void setTreeAdapter(AbstractTreeViewAdapter adapter) {
        boolean hadAdapter = mAdapter != null;
        mAdapter = adapter;
        if (mTree != null) {
            mTree.setAdapter(adapter);
            if (!mTreeShown && !hadAdapter) {
                // The list was hidden, and previously didn't have an
                // adapter.  It is now time to show it.
                setTreeShown(true, getView().getWindowToken() != null);
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
        ensureTree();
        mTree.setSelection(position);
    }

    /**
     * Get the position of the currently selected list item.
     */
    public int getSelectedItemPosition() {
        ensureTree();
        return mTree.getSelectedItemPosition();
    }

    /**
     * Get the cursor row ID of the currently selected list item.
     */
    public long getSelectedItemId() {
        ensureTree();
        return mTree.getSelectedItemId();
    }

    /**
     * Get the activity's list view widget.
     */
    public TreeViewList getTreeViewList() {
        ensureTree();
        return mTree;
    }

    /**
     * The default content for a GridFragment has a TextView that can
     * be shown when the list is empty.  If you would like to have it
     * shown, call this method to supply the text it should use.
     */
    public void setEmptyText(CharSequence text) {
        ensureTree();
        if (mStandardEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        mStandardEmptyView.setText(text);
        if (mEmptyText == null) {
            mTree.setEmptyView(mStandardEmptyView);
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
     * showing it once an adapter is given with {@link #setTreeAdapter(AbstractTreeViewAdapter)}.
     * If the list at that point had not been shown, when it does get shown
     * it will be do without the user ever seeing the hidden state.
     *
     * @param shown If true, the list view is shown; if false, the progress
     * indicator.  The initial value is true.
     */
    public void setTreeShown(boolean shown) {
        setTreeShown(shown, true);
    }

    /**
     * Like {@link #setTreeShown(boolean)}, but no animation is used when
     * transitioning from the previous state.
     */
    public void setTreeShownNoAnimation(boolean shown) {
        setTreeShown(shown, false);
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
    private void setTreeShown(boolean shown, boolean animate) {
        ensureTree();
        if (mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mTreeShown == shown) {
            return;
        }
        mTreeShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mTreeContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mTreeContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mTreeContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mTreeContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mTreeContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mTreeContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Get the GridAdapter associated with this activity's TreeViewList.
     */
    public AbstractTreeViewAdapter getTreeAdapter() {
        return mAdapter;
    }

    private void ensureTree() {
        if (mTree != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof TreeViewList) {
            mTree = (TreeViewList)root;
        } else {
            mStandardEmptyView = (TextView)root.findViewById(INTERNAL_EMPTY_ID);
            if (mStandardEmptyView == null) {
                mEmptyView = root.findViewById(android.R.id.empty);
            } else {
                mStandardEmptyView.setVisibility(View.GONE);
            }
            mProgressContainer = root.findViewById(INTERNAL_PROGRESS_CONTAINER_ID);
            mTreeContainer = root.findViewById(INTERNAL_TREE_CONTAINER_ID);
            View rawTreeViewList = root.findViewById(android.R.id.list);
            if (!(rawTreeViewList instanceof TreeViewList)) {
                if (rawTreeViewList == null) {
                    throw new RuntimeException(
                            "Your content must have a TreeViewList whose id attribute is " +
                                    "'android.R.id.list'");
                }
                throw new RuntimeException(
                        "Content has view with id attribute 'android.R.id.list' "
                                + "that is not a TreeViewList class");
            }
            mTree = (TreeViewList)rawTreeViewList;
            if (mEmptyView != null) {
                mTree.setEmptyView(mEmptyView);
            } else if (mEmptyText != null) {
                mStandardEmptyView.setText(mEmptyText);
                mTree.setEmptyView(mStandardEmptyView);
            }
        }
        mTreeShown = true;
        mTree.setOnItemClickListener(mOnClickListener);
        if (mAdapter != null) {
            AbstractTreeViewAdapter adapter = mAdapter;
            mAdapter = null;
            setTreeAdapter(adapter);
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            if (mProgressContainer != null) {
                setTreeShown(false, false);
            }
        }
        mHandler.post(mRequestFocus);
    }
}
