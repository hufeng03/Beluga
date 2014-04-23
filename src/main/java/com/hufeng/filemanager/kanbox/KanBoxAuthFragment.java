package com.hufeng.filemanager.kanbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hufeng.filemanager.BaseFragment;
import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.kanbox.view.KanBoxLoginWebView;
import com.kanbox.api.Token;

import java.lang.ref.WeakReference;

/**
 * Created by feng on 13-11-21.
 */
public class KanBoxAuthFragment extends BaseFragment implements KanBoxApi.KanBoxApiListener{

    private static final String TAG = KanBoxAuthFragment.class.getSimpleName();

    private RelativeLayout mProgressLayout;
    private LinearLayout mWebLayout;
    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private TextView mProgressNumber;
    private KanBoxLoginWebView mWebView;
//    private ProgressBar mWebLoadingProgressBar;

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

        mProgressLayout = new RelativeLayout(context);

        mProgressBar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleLarge);
        mProgressBar.setId(1);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mProgressLayout.addView(mProgressBar, params);

        mProgressText = new TextView(getActivity());
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params2.addRule(RelativeLayout.BELOW, 1);
        mProgressLayout.addView(mProgressText, params2);

        mProgressNumber = new TextView(getActivity());
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params3.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mProgressLayout.addView(mProgressNumber, params3);

        root.addView(mProgressLayout, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mWebLayout = new LinearLayout(context);
        mWebLayout.setOrientation(LinearLayout.VERTICAL);

//        mWebLoadingProgressBar = new ProgressBar(getActivity(), null,
//                android.R.attr.progressBarStyleHorizontal);
//        mWebLoadingProgressBar.setMax(100);
//        mWebLoadingProgressBar.setVisibility(View.VISIBLE);
//        mWebLayout.addView(mWebLoadingProgressBar, new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, 15));
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
                Log.i(TAG, "onProgressChanged:"+progress);
                if (progress > 0) {
//                    mProgressLayout.setVisibility(View.GONE);
//                    mWebLoadingProgressBar.setVisibility(View.VISIBLE);
                    mProgressNumber.setText(String.valueOf(progress));
                }
//                mWebLoadingProgressBar.setProgress(progress);
            }
        });
//        mWebView.loadUrl(getIntent().getStringExtra("url"));
        //get auth from kanbox
        mProgressLayout.setVisibility(View.VISIBLE);
        mWebLayout.setVisibility(View.GONE);


//        Log.i(TAG, "token status is "+token);
//        switch (token) {
//            case VALID:
//                if(mWeakListener!=null) {
//                    KanBoxAuthFragmentListener listener = mWeakListener.get();
//                    if (listener != null) {
//                        listener.onKanBoxAuthSuccess();
//                    }
//                }
//                break;
//            case OBSOLETE:
//                mProgressText.setText(R.string.kanbox_refresh_token_start);
//                KanBoxApi.getInstance().refreshToken();
//                break;
//            case NONE:
//                mProgressText.setText(R.string.kanbox_get_auth_start);
//                String url = KanBoxConfig.OAUTH_URL+"?response_type=code&client_id=" + Constants.CLIENT_ID + "&platform=android" + "&redirect_uri=" + KanBoxConfig.GET_AUTH_REDIRECT_URI + "&user_language=ZH";
//                mWebView.loadUrl(url);
//                break;
//        }

        if (TextUtils.isEmpty(Token.getInstance().getAccessToken())) {
            mProgressText.setText(R.string.kanbox_load_auth_page);
            String url = KanBoxConfig.OAUTH_URL+"?response_type=code&client_id=" + Constants.CLIENT_ID + "&platform=android" + "&redirect_uri=" + KanBoxConfig.GET_AUTH_REDIRECT_URI + "&user_language=ZH";
            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(url);
        } else if (Token.getInstance().isExpired()){
            mProgressText.setText(R.string.kanbox_refresh_token_start);
            mWebView.setVisibility(View.GONE);
            KanBoxApi.getInstance().refreshToken();
        } else {
            mProgressText.setText("");
            if(mWeakListener!=null) {
                KanBoxAuthFragmentListener listener = mWeakListener.get();
                if (listener != null) {
                    listener.onKanBoxAuthSuccess();
                }
            }
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
            KanBoxApi.getInstance().getAccountInfo();
            mProgressNumber.setVisibility(View.GONE);
            mProgressText.setText(R.string.kanbox_get_account_info);
        } else if (op_type == KanBoxApi.OP_GET_ACCCOUNT_INFO) {
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
        if (getActivity()!=null) {
            if (op_type == KanBoxApi.OP_GET_TOKEN) {
                Toast.makeText(getActivity(), getString(R.string.kanbox_get_token_failed), Toast.LENGTH_SHORT).show();
            } else if (op_type == KanBoxApi.OP_REFRESH_TOKEN) {
                Toast.makeText(getActivity(), getString(R.string.kanbox_refresh_token_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.kanbox_get_account_info_failed), Toast.LENGTH_SHORT).show();
            }
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
            Log.i(TAG, "onProgressFinished");
            mProgressNumber.setText("");
//            mWebLoadingProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView wv, String url) {
            Log.i(TAG, "onPageFinished:"+url);
            mProgressNumber.setText("");
            handlUrlEnd(url);
        }

        @Override
        public void onPageStarted(WebView wv, String url, Bitmap favicon) {
//            mWebLoadingProgressBar.setVisibility(View.VISIBLE);
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
            mProgressNumber.setText("");
            handler.proceed();
        }
    }

    private boolean haveGetToken = false;
    private boolean mWebError = false;

    private void handlUrlEnd(String url) {
//        mWebLoadingProgressBar.setVisibility(View.GONE);
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
            Token.getInstance().setCode(code);
            KanBoxApi.getInstance().getToken();
            mWebLayout.setVisibility(View.GONE);
            mProgressText.setText(R.string.kanbox_get_auth_start);
            mProgressLayout.setVisibility(View.VISIBLE);
        } else if (url.startsWith(KanBoxConfig.GET_TOKEN_REDIRECT_URI)) {

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

//    public boolean onBack() {
//        if (mWebView.canGoBack() ){
//            mWebView.goBack();
//            return true;
//        } else {
//            return false;
//        }
//    }
}
