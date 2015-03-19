package com.belugamobile.filemanager;

import android.app.Activity;
import android.os.Bundle;

import com.belugamobile.filemanager.ui.BelugaActionController;

public abstract class FileTabFragment extends BelugaBaseFragment implements
        BelugaFragmentInterface {
	private final String LOG_TAG = ((Object)this).getClass().getSimpleName();

    public FileRecyclerFragment mCurrentChildFragment;

    @Override
    public BelugaActionController getActionController() {
        if (mCurrentChildFragment != null) {
            return (mCurrentChildFragment).getActionController();
        } else {
            Activity act = getActivity();
            if (act != null && act instanceof BelugaActionControllerActivity) {
                return ((BelugaActionControllerActivity) act).getGlobalActionController();
            }
        }
        return null;
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (Constants.RETAIN_FRAGMENT) {
			setRetainInstance(true);
		}
//		setHasOptionsMenu(true);
	}

    @Override
    public boolean onBackPressed() {
        if (mCurrentChildFragment != null && mCurrentChildFragment.onBackPressed()) {
            return true;
        } else {
            return false;
        }
    }



    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mCurrentChildFragment != null) {
            mCurrentChildFragment.setUserVisibleHint(isVisibleToUser);
        }
    }

    protected void showInitialState() {

    }
}
