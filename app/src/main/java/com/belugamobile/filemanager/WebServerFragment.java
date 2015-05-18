package com.belugamobile.filemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.belugamobile.filemanager.services.UiCallServiceHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Feng on 2015-05-10.
 */
public class WebServerFragment extends BelugaBaseFragment{

    @InjectView(R.id.web_server_start)
    Button mStart;

    @InjectView(R.id.web_server_stop)
    Button mStop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_server_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick(R.id.web_server_start)
    public void onStartButtonClicked() {
        UiCallServiceHelper.getInstance().startWebServer();
    }

    @OnClick(R.id.web_server_stop)
    public void onStopButtonClicked() {
        UiCallServiceHelper.getInstance().stopWebServer();
    }
}
