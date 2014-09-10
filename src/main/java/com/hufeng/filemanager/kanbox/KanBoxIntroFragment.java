package com.hufeng.filemanager.kanbox;

import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hufeng.filemanager.BaseFragment;
import com.hufeng.filemanager.BusProvider;
import com.hufeng.filemanager.KanboxAuthStartEvent;
import com.hufeng.filemanager.R;
import com.kanbox.api.Token;

/**
 * Created by feng on 13-11-26.
 */
public class KanBoxIntroFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = KanBoxIntroFragment.class.getSimpleName();

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
        if (TextUtils.isEmpty(Token.getInstance().getAccessToken())) {
            mStartButton.setText(R.string.kanbox_intro_enter);
            mStartTip.setVisibility(View.GONE);
        } else {
            mStartButton.setText(R.string.kanbox_intro_login);
            mStartTip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }


//    WeakReference<KanBoxIntroFragmentListener> mWeakListener;

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.kanbox_start:
//                if(mWeakListener!=null) {
//                    KanBoxIntroFragmentListener listener = mWeakListener.get();
//                    if (listener != null) {
//                        listener.onKanBoxAuthStart();
//                    }
//                }
//                BusProvider.getInstance().post(produceKanboxAuthStartEvent());
//                BusProvider.getInstance().post(new KanboxAuthStartEvent(System.currentTimeMillis()));
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new KanboxAuthStartEvent(System.currentTimeMillis()).buildIntentWithBundle());
                break;
        }
    }

//    @Produce
//    public KanboxAuthStartEvent produceKanboxAuthStartEvent() {
//        LogUtil.i(TAG, "produceKanboxAuthStartFragment()");
//        return new KanboxAuthStartEvent(System.currentTimeMillis());
//    }

//    public static interface KanBoxIntroFragmentListener {
//        public void onKanBoxAuthStart();
//    }

//    public void setListener(KanBoxIntroFragmentListener listener) {
//        mWeakListener = new WeakReference<KanBoxIntroFragmentListener>(listener);
//    }
}
