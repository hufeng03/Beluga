package com.hufeng.filemanager.kanbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.hufeng.filemanager.BaseFragment;
import com.hufeng.filemanager.R;

import java.lang.ref.WeakReference;

/**
 * Created by feng on 13-11-26.
 */
public class KanBoxIntroFragment extends BaseFragment implements View.OnClickListener {

    private Button mStartButton;
    private TextView mStartTip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kanbox_intro_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStartButton = (Button)view.findViewById(R.id.kanbox_start);
        mStartTip = (TextView)view.findViewById(R.id.kanbox_start_tip);
        mStartButton.setOnClickListener(this);
//        PushSharePreference sPreference = new PushSharePreference(getSherlockActivity(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
//        if(sPreference.contains("accecc_token") && sPreference.contains("refresh_token")){
//            mStartButton.setText("进入酷盘");
//            mStartTip.setVisibility(View.GONE);
//        } else {
//            mStartButton.setText("登录酷盘账号");
//            mStartTip.setVisibility(View.VISIBLE);
//        }
        KanBoxApi.TOKEN_STATUS token = KanBoxApi.getInstance().getTokenStatus();
        switch (token) {
            case VALID:
            case OBSOLETE:
                mStartButton.setText(R.string.kanbox_intro_enter);
                mStartTip.setVisibility(View.GONE);
                break;
            case NONE:
                mStartButton.setText(R.string.kanbox_intro_login);
                mStartTip.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }


    WeakReference<KanBoxIntroFragmentListener> mWeakListener;

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.kanbox_start:
                if(mWeakListener!=null) {
                    KanBoxIntroFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onKanBoxAuthStart();
                    }
                }
                break;
        }
    }

    public static interface KanBoxIntroFragmentListener {
        public void onKanBoxAuthStart();
    }

    public void setListener(KanBoxIntroFragmentListener listener) {
        mWeakListener = new WeakReference<KanBoxIntroFragmentListener>(listener);
    }
}
