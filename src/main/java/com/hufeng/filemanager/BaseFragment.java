package com.hufeng.filemanager;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class BaseFragment extends Fragment {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    public boolean onBackPressed() {
        return false;
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onSaveInstanceState "+this.hashCode());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onViewCreated "+this.hashCode());
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onViewStateRestored "+this.hashCode());
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onActivityCreated "+this.hashCode());
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onAttach "+this.hashCode());
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onCreate "+this.hashCode());
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onCreateView "+this.hashCode());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onDestroy "+this.hashCode());
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onDestroyView "+this.hashCode());
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onDetach "+this.hashCode());
		super.onDetach();
	}

	@Override
	public void onPause() {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onPause "+this.hashCode());
		super.onPause();
	}

	@Override
	public void onResume() {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onResume "+this.hashCode());
		super.onResume();
	}

	@Override
	public void onStart() {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onStart "+this.hashCode());
		super.onStart();
	}

	@Override
	public void onStop() {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onStop "+this.hashCode());
		super.onStop();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onHiddenChanged "+hidden+" "+this.hashCode());
		super.onHiddenChanged(hidden);
	}
	
	@Override
    public void setMenuVisibility(final boolean visible) {
//		boolean old_visible = getMenuVisibility();
//		Log.i(this.getClass().getSimpleName(), "setMenuVisibility "+visible+ ", old="+old_visible);
        super.setMenuVisibility(visible);
    }

//	public boolean getMenuVisibility() {
//		boolean visible = true;
//		Field field_visible;
//		try {
//			field_visible = Fragment.class.getDeclaredField("mMenuVisible");
//			field_visible.setAccessible(true);
//			try {
//				visible = field_visible.getBoolean(this);
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} catch (NoSuchFieldException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return visible;
//	}
}
