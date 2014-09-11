package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hufeng.filemanager.browser.FileUtils;

/**
 * Created by feng on 2014-09-10.
 */
public class FtpTabFragment extends BaseFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if(Constants.RETAIN_FRAGMENT){
            setRetainInstance(true);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.ftp_tab_fragment, container, false);

        showPlayFtpFragment();
        return view;
    }


    public void showPlayFtpFragment() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        PlayFtpFragment fragment = (PlayFtpFragment) getChildFragmentManager().findFragmentByTag(PlayFtpFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = PlayFtpFragment.newFragment();
            ft.replace(R.id.fragment_container, fragment, PlayFtpFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
    }

}
