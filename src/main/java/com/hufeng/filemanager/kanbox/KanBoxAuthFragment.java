package com.hufeng.filemanager.kanbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.hufeng.filemanager.BaseFragment;
import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.kanbox.view.KanBoxLoginWebView;

import java.lang.ref.WeakReference;

/**
 * Created by feng on 13-11-21.
 */
public class KanBoxAuthFragment extends BaseFragment implements KanBoxApi.KanBoxApiListener{

    private static final String TAG = KanBoxAuthFragment.class.getSimpleName();

    private LinearLayout mProgressLayout;
    private LinearLayout mWebLayout;
    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private KanBoxLoginWebView mWebView;
    private ProgressBar mWebLoadingProgressBar;

    WeakReference<KanBoxAuthFragmentListener> mWeakListener;

    public static interface KanBoxAuthFragmentListener {
        public void onKanBoxAuthSuccess();
        public void onKanBoxAuthFailed();
    }

    public void setListener(KanBoxAuthFragmentListener listener) {
        mWeakListener = new WeakReference<KanBoxAuthFragmentListener>(listener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Context context = getActivity();

        FrameLayout root = new FrameLayout(context);

        mProgressLayout = new LinearLayout(context);
        mProgressLayout.setOrientation(LinearLayout.VERTICAL);
        mProgressLayout.setVisibility(View.GONE);
        mProgressLayout.setGravity(Gravity.CENTER);

        mProgressBar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleLarge);
        mProgressText = new TextView(getActivity());
        mProgressText.setGravity(Gravity.CENTER);
        mProgressLayout.addView(mProgressBar, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mProgressLayout.addView(mProgressText, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        root.addView(mProgressLayout, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mWebLayout = new LinearLayout(context);
        mWebLayout.setOrientation(LinearLayout.VERTICAL);

        mWebLoadingProgressBar = new ProgressBar(getActivity(), null,
                android.R.attr.progressBarStyleHorizontal);
        mWebLoadingProgressBar.setMax(100);
        mWebLoadingProgressBar.setVisibility(View.GONE);
        mWebLayout.addView(mWebLoadingProgressBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 15));
        mWebView = new KanBoxLoginWebView(context);
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setSaveFormData(false);
        mWebLayout.addView(mWebView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        root.addView(mWebLayout, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KanBoxApi.getInstance().registerKanBoxApiListener(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.clearCache(true);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.clearFormData();
        mWebView.setWebViewClient(new EmbeddedWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                mWebLoadingProgressBar.setProgress(progress);
            }
        });
//        mWebView.loadUrl(getIntent().getStringExtra("url"));
        //get auth from kanbox
        mProgressLayout.setVisibility(View.VISIBLE);
        mWebLayout.setVisibility(View.GONE);

        KanBoxApi.TOKEN_STATUS token = KanBoxApi.getInstance().getTokenStatus();
        Log.i(TAG, "token status is "+token);
        switch (token) {
            case VALID:
                if(mWeakListener!=null) {
                    KanBoxAuthFragmentListener listener = mWeakListener.get();
                    if (listener != null) {
                        listener.onKanBoxAuthSuccess();
                    }
                }
                break;
            case OBSOLETE:
                mProgressText.setText(R.string.kanbox_refresh_token_start);
                KanBoxApi.getInstance().refreshToken();
                break;
            case NONE:
                mProgressText.setText(R.string.kanbox_get_auth_start);
                String url = KanBoxConfig.OAUTH_URL+"?response_type=code&client_id=" + Constants.CLIENT_ID + "&platform=android" + "&redirect_uri=" + KanBoxConfig.GET_AUTH_REDIRECT_URI + "&user_language=ZH";
                mWebView.loadUrl(url);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KanBoxApi.getInstance().unRegisterKanBoxApiListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onKanBoxApiSuccess(int op_type, String path, String response) {
        if (op_type == KanBoxApi.OP_GET_TOKEN || op_type == KanBoxApi.OP_REFRESH_TOKEN) {
            if(mWeakListener!=null) {
                KanBoxAuthFragmentListener listener = mWeakListener.get();
                if (listener != null) {
                    listener.onKanBoxAuthSuccess();
                }
            }
        }
    }

    @Override
    public void onKanBoxApiFailed(int op_type, String path) {
        if (op_type == KanBoxApi.OP_GET_TOKEN || op_type == KanBoxApi.OP_REFRESH_TOKEN) {
            Toast.makeText(getActivity(), getString(R.string.kanbox_refresh_token_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

        mWebLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mProgressLayout.setVisibility(View.VISIBLE);
        if(op_type == KanBoxApi.OP_REFRESH_TOKEN) {
            mProgressText.setText(R.string.kanbox_refresh_token_failed);
        } else if(op_type == KanBoxApi.OP_GET_TOKEN) {
            mProgressText.setText(R.string.kanbox_get_auth_failed);
        } else {
            mProgressText.setText(R.string.network_error);
        }

        if(mWeakListener!=null) {
            KanBoxAuthFragmentListener listener = mWeakListener.get();
            if (listener != null) {
                listener.onKanBoxAuthFailed();
            }
        }
    }

    @Override
    public void onKanBoxApiProgress(int op_type, String path, int progress) {

    }


    private class EmbeddedWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            Log.i(TAG, "shouldOverrideUrlLoaing:"+url);
            return true;
        }

        private void onProgressFinished() {
            mWebLoadingProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView wv, String url) {
            Log.i(TAG, "onPageFinished:"+url);
            handlUrlEnd(url);
        }

        @Override
        public void onPageStarted(WebView wv, String url, Bitmap favicon) {
            mWebLoadingProgressBar.setVisibility(View.VISIBLE);
            handleUrlStart(url);
            Log.i(TAG, "onPageStarted:"+url);
        }

        @Override
        public void onReceivedError(WebView wv, int errorCode, String description, String failingUrl) {
            Log.i(TAG, "onReceivedError:"+errorCode);
            onProgressFinished();
            onKanBoxApiFailed(0, null);
            mWebError = true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.i(TAG, "onReceivedSslError:"+error);
            handler.proceed();
        }
    }

    private boolean haveGetToken = false;
    private boolean mWebError = false;

    private void handlUrlEnd(String url) {
        mWebLoadingProgressBar.setVisibility(View.GONE);
        if (TextUtils.isEmpty(url)) return;
        if (url.startsWith(KanBoxConfig.OAUTH_URL) && !mWebError) {
            mProgressLayout.setVisibility(View.GONE);
            mWebLayout.setVisibility(View.VISIBLE);
        }
    }

    private void handleUrlStart(String url) {
        if (TextUtils.isEmpty(url)) return;
        if (url.startsWith(KanBoxConfig.GET_AUTH_REDIRECT_URI) && url.contains("code=")) {
            if (haveGetToken) return;
            haveGetToken = true;
            String code = url.substring(url.indexOf("code=") + 5);
            KanBoxApi.getInstance().getToken(code);
            mWebLayout.setVisibility(View.GONE);
            mProgressLayout.setVisibility(View.VISIBLE);
        } else if (url.startsWith(KanBoxConfig.GET_TOKEN_REDIRECT_URI)) {

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    public boolean onBack() {
        if (mWebView.canGoBack() ){
            mWebView.goBack();
            return true;
        } else {
            return false;
        }
    }
}
