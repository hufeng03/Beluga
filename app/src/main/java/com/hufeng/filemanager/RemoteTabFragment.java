package com.hufeng.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ppareit.swiftp.gui.FsPreferenceActivity;

/**
 * Created by feng on 2014-09-10.
 */
public class RemoteTabFragment extends BaseFragment{

    RemoteSelectionFragment.RemoteProtocol mProtocol;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if(Constants.RETAIN_FRAGMENT){
            setRetainInstance(true);
        }
        setHasOptionsMenu(true);
    }

    BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == RemoteProtocolSelectEvent.INTENT_ACTION) {
                RemoteProtocolSelectEvent event = new RemoteProtocolSelectEvent(intent);
                onRemoteProtocolSelected(event);
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mEventReceiver, new IntentFilter(RemoteProtocolSelectEvent.INTENT_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mEventReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.ftp_tab_fragment, container, false);

        showRemotePanel();
        return view;
    }


    public void onRemoteProtocolSelected(RemoteProtocolSelectEvent event) {
        if (event != null) {
            mProtocol = RemoteSelectionFragment.RemoteProtocol.valueOf(event.protocol);
            showSingleRemotePanel(mProtocol);
        }
    }


    public void showRemotePanel() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        RemoteSelectionFragment fragment = (RemoteSelectionFragment) getChildFragmentManager().findFragmentByTag(RemoteSelectionFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = RemoteSelectionFragment.newFragment();
            ft.replace(R.id.fragment_container, fragment, RemoteSelectionFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
    }

    public void showSingleRemotePanel(RemoteSelectionFragment.RemoteProtocol protocol) {
//        final FragmentManager fm = getChildFragmentManager();
//        final FragmentTransaction ft = fm.beginTransaction();
//        RemoteConnectionFragment fragment = (RemoteConnectionFragment) getChildFragmentManager().findFragmentByTag(RemoteConnectionFragment.class.getSimpleName());
//        if (fragment == null) {
//            fragment = RemoteConnectionFragment.newFragment();
//            ft.replace(R.id.fragment_container, fragment, RemoteConnectionFragment.class.getSimpleName());
//        } else {
//            if (fragment.isDetached()) {
//                ft.attach(fragment);
//            }
//        }
//        ft.commit();
        Intent intent = new Intent(getActivity(), FsPreferenceActivity.class);
        startActivity(intent);
    }

}
