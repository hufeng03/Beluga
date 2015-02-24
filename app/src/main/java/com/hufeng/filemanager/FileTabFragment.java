package com.hufeng.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.ImageEntry;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.ui.BelugaActionController;

public abstract class FileTabFragment extends BaseFragment implements
        BelugaFragmentInterface {
	private final String LOG_TAG = ((Object)this).getClass().getSimpleName();

    public BelugaFragmentInterface mCurrentChildFragment;

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
        if (getActivity() != null) {
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    protected void showInitialState() {

    }
}
