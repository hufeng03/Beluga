package com.belugamobile.filemanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.belugamobile.filemanager.utils.LogUtil;


public class BelugaBaseFragment extends Fragment{

    private static final boolean DEBUG = BuildConfig.DEBUG;

    protected Context mContext;

	@Override
	public void onSaveInstanceState(Bundle outState) {
        if (DEBUG)
    		LogUtil.i(((Object) this).getClass().getSimpleName(), "onSaveInstanceState " + this.hashCode());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onViewCreated "+this.hashCode());
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onViewStateRestored "+this.hashCode());
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onActivityCreated "+this.hashCode());
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onAttach "+this.hashCode());
		super.onAttach(activity);
        mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onCreate "+this.hashCode());
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onCreateView "+this.hashCode());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onDestroy "+this.hashCode());
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onDestroyView "+this.hashCode());
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onDetach "+this.hashCode());
		super.onDetach();
        mContext = null;
	}

	@Override
	public void onPause() {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onPause "+this.hashCode());
		super.onPause();
	}

	@Override
	public void onResume() {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onResume "+this.hashCode());
		super.onResume();
	}

	@Override
	public void onStart() {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onStart "+this.hashCode());
		super.onStart();
	}

	@Override
	public void onStop() {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onStop "+this.hashCode());
		super.onStop();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onHiddenChanged "+hidden+" "+this.hashCode());
		super.onHiddenChanged(hidden);
	}
	
	@Override
    public void setMenuVisibility(final boolean visible) {
        if (DEBUG)
            LogUtil.i(((Object)this).getClass().getSimpleName(), "setMenuVisibility "+visible+" "+this.hashCode());
        super.setMenuVisibility(visible);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (DEBUG)
            LogUtil.i(((Object)this).getClass().getSimpleName(), "setUserVisibleHint "+isVisibleToUser+" "+this.hashCode());
        super.setUserVisibleHint(isVisibleToUser);
    }

}
