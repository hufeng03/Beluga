package com.hufeng.filemanager.kanbox;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.FileGrouperFragment;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.FileTabFragment;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.resource.FileDownloader;
import com.kanbox.api.Token;

import java.io.File;
import java.util.List;

/**
 * Created by feng on 13-11-21.
 */
public class KanBoxTabFragment extends FileTabFragment implements
        KanBoxAuthFragment.KanBoxAuthFragmentListener,
        FileGrouperFragment.FileGrouperFragmentListener,
        KanBoxBrowserFragment.KanBoxBrowserListener,
        KanBoxIntroFragment.KanBoxIntroFragmentListener,
        View.OnClickListener, FileDownloader.FileDownloaderListener{

    private static final String TAG = KanBoxTabFragment.class.getSimpleName();

//    private KanBoxBrowserFragment mBrowserFragment = null;

    private Fragment mIntroFragment, mAuthFragment, mBrowserFragment, mUploadFragment, mFileGrouperFragment;

    private LinearLayout mAdLayout;

    private void clearFragmentInstance(){
        mIntroFragment = null;
        mAuthFragment = null;
        mBrowserFragment = null;
        mUploadFragment = null;
        mFileGrouperFragment = null;
        mCurrentChildFragment = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kanbox_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdLayout = (LinearLayout)view.findViewById(R.id.kanbox_ad);
        TextView ad_ok = (TextView)view.findViewById(R.id.kanbox_ad_ok);
        ImageButton ad_close = (ImageButton)view.findViewById(R.id.kanbox_ad_close);
        ad_ok.setOnClickListener(this);
        ad_close.setOnClickListener(this);
        mAdLayout.setVisibility(View.GONE);
//        KanBoxApi.TOKEN_STATUS token = KanBoxApi.getInstance().getTokenStatus();
//        switch (token) {
//            case VALID:
//                showKanBoxBrowserFragment();
//                break;
//            case OBSOLETE:
//                showKanBoxAuthFragment();
//                break;
//            case NONE:
//                showKanBoxIntroFragment();
//                break;
//        }

        if (TextUtils.isEmpty(Token.getInstance().getAccessToken())) {
            showKanBoxIntroFragment();
        } else if (Token.getInstance().isExpired()){
            showKanBoxAuthFragment();
        } else {
            showKanBoxBrowserFragment();
        }

        FileDownloader.addFileDownloaderListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FileDownloader.removeFileDownloaderListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAdLayout();
    }

    @Override
    public boolean onBackPressed() {
        if (super.onBackPressed()) {
            return true;
        }
        if (mFileGrouperFragment != null || mUploadFragment != null) {
            showKanBoxBrowserFragment();
            return true;
        } else if (mAuthFragment != null) {
            showKanBoxIntroFragment();
            return true;
        }
        return false;
    }

    private void refreshAdLayout() {
        if (mBrowserFragment == null || mAdLayout == null) {
            return;
        }

        List<FileEntry> uploadingFiles = KanBoxApi.getInstance().getUploadingFiles();
        List<FileEntry> uploadingSuccessFiles = KanBoxApi.getInstance().getUploadingSuccessFiles();
        List<FileEntry> uploadingFailedFiles = KanBoxApi.getInstance().getUploadingFailedFiles();
        int uploading_size = uploadingFiles == null ? 0 :uploadingFiles.size();
        int uploading_success_size = uploadingSuccessFiles == null ? 0 :uploadingSuccessFiles.size();
        int uploading_failed_size = uploadingFailedFiles == null ? 0 :uploadingFailedFiles.size();
        if (uploading_size > 0 || uploading_failed_size > 0) {
            TextView text = (TextView) mAdLayout.findViewById(R.id.kanbox_ad_ok);
            boolean flag_show_uploading = true;
            if (uploading_size > 0) {
                if (uploading_success_size > 0 && uploading_failed_size > 0) {
                    text.setText(getString(R.string.kanbox_uploading_files_with_success_and_failed, uploading_size, uploading_failed_size, uploading_success_size));
                } else if (uploading_failed_size > 0) {
                    text.setText(getString(R.string.kanbox_uploading_files_with_failed, uploading_size, uploading_failed_size));
                } else if (uploading_success_size > 0) {
                    text.setText(getString(R.string.kanbox_uploading_files_with_success, uploading_size, uploading_success_size));
                } else {
                    text.setText(getString(R.string.kanbox_uploading_files, uploading_size));
                }
            } else {
                if (uploading_success_size > 0 && uploading_failed_size > 0) {
                    text.setText(getString(R.string.kanbox_uploading_files_all_failed_or_success, uploading_failed_size, uploading_success_size));
                } else if (uploading_failed_size > 0) {
                    text.setText(getString(R.string.kanbox_uploading_files_all_failed, uploading_failed_size));
                } else if (uploading_success_size > 0) {
                    //no showing, all success
                    flag_show_uploading = false;
                } else {
                    //no showing
                    flag_show_uploading = false;
                }
            }

            if (flag_show_uploading) {
                mAdLayout.setVisibility(View.VISIBLE);
                mAdLayout.findViewById(R.id.kanbox_ad_close).setVisibility(View.GONE);
                text.setTag("up");
                return;
            }
        }

        PackageManager pm = getActivity().getPackageManager();
        PackageInfo info = null;
        try{
            info = pm.getPackageInfo("com.kanbox.wp", PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch(PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
        }
        if(info!=null) {
            mAdLayout.setVisibility(View.GONE);
        } else {
            //if(ChannelUtil.isKanBoxChannel(FileManager.getAppContext())) {
            if (Constants.SHOW_KANBOX_CATEGORY) {
                String path = KanBoxUtil.getKanboxApkPath(FileManager.getAppContext());
                TextView text = (TextView)mAdLayout.findViewById(R.id.kanbox_ad_ok);
                if (new File(path).exists()) {
                    text.setText(R.string.install_kanbox_android_client);
                } else if (FileDownloader.isDownloading(Constants.KANBOX_APK_URL)) {
                    int progress = FileDownloader.getDownloadProgress(Constants.KANBOX_APK_URL);
                    text.setText(getResources().getString(R.string.downloading_kanbox_android_client, progress));
                } else {
                    text.setText(R.string.download_kanbox_android_client);
                }
                mAdLayout.setVisibility(View.VISIBLE);
                mAdLayout.findViewById(R.id.kanbox_ad_close).setVisibility(View.GONE);
                text.setTag("ad");
            } else {
                mAdLayout.setVisibility(View.GONE);
            }
        }
    }

    private void showKanBoxIntroFragment() {
        mAdLayout.setVisibility(View.GONE);
        final FragmentManager fm = getChildFragmentManager();
//        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        final FragmentTransaction ft = fm.beginTransaction();
        KanBoxIntroFragment fragment = (KanBoxIntroFragment) getChildFragmentManager().findFragmentByTag(KanBoxIntroFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new KanBoxIntroFragment();
            ft.replace(R.id.fragment_container, fragment, KanBoxIntroFragment.class.getSimpleName());
            //transaction.addToBackStack(null);
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        fragment.setListener(this);
        ft.commit();
        clearFragmentInstance();
        mIntroFragment = fragment;
    }

    private void showKanBoxAuthFragment() {
        mAdLayout.setVisibility(View.GONE);
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        KanBoxAuthFragment fragment = (KanBoxAuthFragment) getChildFragmentManager().findFragmentByTag(KanBoxAuthFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new KanBoxAuthFragment();
            ft.replace(R.id.fragment_container, fragment, KanBoxAuthFragment.class.getSimpleName());
            //transaction.addToBackStack(null);
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        fragment.setListener(this);
        ft.commit();
        clearFragmentInstance();
        mAuthFragment = fragment;
    }


    private void showKanBoxBrowserFragment() {
        final FragmentManager fm = getChildFragmentManager();
//        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        final FragmentTransaction ft = fm.beginTransaction();
        KanBoxBrowserFragment fragment = (KanBoxBrowserFragment) getChildFragmentManager().findFragmentByTag(KanBoxBrowserFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new KanBoxBrowserFragment();
            ft.replace(R.id.fragment_container, fragment, KanBoxBrowserFragment.class.getSimpleName());
            //transaction.addToBackStack(null);
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        fragment.setKanBoxBrowserListener(this);
        ft.commit();
        clearFragmentInstance();
        mBrowserFragment = fragment;
        mCurrentChildFragment = fragment;
        refreshAdLayout();
    }

    private void showKanBoxUploadFragment() {
        mAdLayout.setVisibility(View.GONE);
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        KanBoxUploadFragment fragment = (KanBoxUploadFragment) getChildFragmentManager().findFragmentByTag(KanBoxUploadFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new KanBoxUploadFragment();
            ft.replace(R.id.fragment_container, fragment, KanBoxUploadFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
//        ft.addToBackStack(null);
        ft.commit();
        clearFragmentInstance();
        mUploadFragment = fragment;
        mCurrentChildFragment = fragment;
    }

    private void showFileGrouperFragment(int category) {
        mAdLayout.setVisibility(View.GONE);
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        FileGrouperFragment fragment = (FileGrouperFragment) fm.findFragmentByTag(FileGrouperFragment.class.getSimpleName());
        if(fragment == null) {
            fragment = FileGrouperFragment.newCloudUploadSelectInstance(category, mCurrentChildFragment.getParentFile());
            ft.replace(R.id.fragment_container, fragment, FileGrouperFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        fragment.setListener(this);
//        ft.addToBackStack(null);
        ft.commit();
        clearFragmentInstance();
        mFileGrouperFragment = fragment;
        mCurrentChildFragment = fragment;
    }

    @Override
    public void onKanBoxAuthSuccess() {
        showKanBoxBrowserFragment();
    }

    @Override
    public void onKanBoxAuthFailed() {
        showKanBoxIntroFragment();
    }

    @Override
    public void onKanBoxAuthStart() {
        showKanBoxAuthFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Fragment fragment = (Fragment)getChildFragmentManager().findFragmentById(R.id.fragment_container);
            if(fragment!=null) {
                fragment.onCreateOptionsMenu(menu, inflater);
            }
    }

    @Override
    public void onDestroyOptionsMenu(){
        Fragment fragment = (Fragment)getChildFragmentManager().findFragmentById(R.id.fragment_container);
        if(fragment!=null) {
            fragment.onDestroyOptionsMenu();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == R.id.menu_cloud_upload_cancel) {
//            if (getFileOperation() != null) {
//                getFileOperation().clearOperationFiles();
//            }
//            getSherlockActivity().invalidateOptionsMenu();
//            return true;
//        } else {
            Fragment fragment = (Fragment)getChildFragmentManager().findFragmentById(R.id.fragment_container);
            return fragment.onOptionsItemSelected(item);
//        }
    }


    @Override
    public void onImageFileClicked(ImageView view, String path) {

    }

    @Override
    public void onAddFileIntoCloud(int category) {
        if(category == FileUtils.FILE_TYPE_ALL) {

        } else {
            showFileGrouperFragment(category);
        }
    }

    @Override
    public void onLogoutCloud() {
        showKanBoxIntroFragment();
    }

    @Override
    public void onUploadFileRefresh() {
        refreshAdLayout();
    }

//    @Override
//    public void onDialogDone(DialogInterface dialog, int dialog_id, int button, Object param) {
//        switch(dialog_id) {
//            case FmDialogFragment.ADD_TO_CLOUD_DIALOG:
//                getFileOperation().onOperationAddToCloudConfirm(getActivity());
//                onBackPressed();
//                break;
//        }
//    }

    @Override
    protected void showFile(String path) {
        return;
    }

    @Override
    protected void closeFile(String path) {
        return;
    }

    @Override
    public void refreshFiles() {
        if(mCurrentChildFragment != null) {
            mCurrentChildFragment.refreshUI();
        }
    }


    @Override
    public String[] getAllFiles() {
        if(mCurrentChildFragment != null) {
            return mCurrentChildFragment.getAllFiles();
        } else {
            return null;
        }
    }

    @Override
    public String getParentFile() {
        if(mCurrentChildFragment != null) {
            return mCurrentChildFragment.getParentFile();
        } else {
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kanbox_ad_close:
                mAdLayout.setVisibility(View.GONE);
                break;
            case R.id.kanbox_ad_ok:
                String tag = (String)v.getTag();
                if(tag != null && "up".equals(tag)) {
                    showKanBoxUploadFragment();
                } else {
    //                mAdLayout.setVisibility(Vi);
    //                Uri uri = Uri.parse("http://www.kanbox.com/related/download#android");
    //                Uri uri = Uri.parse("http://hufeng.info/kanbox/");
    //                Intent intent = new Intent(Intent.ACTION_VIEW);
    //                intent.setData(uri);
    //                getSherlockActivity().startActivity(intent);

    //                Intent intent = new Intent(getActivity(),KanboxWebActivity.class);
    //                startActivity(intent);
                    String path = KanBoxUtil.getKanboxApkPath(v.getContext());
                    if (new File(path).exists()) {
    //                    Intent intent = new Intent(Intent.ACTION_VIEW);
    //                    intent.setData(Uri.fromFile(new File(path)));
    //                    startActivity(intent);
                        FileAction.viewFile(getActivity(), path);
                    } else  {
                        FileDownloader.downloadFile(getActivity(), Constants.KANBOX_APK_URL, new File(path).getParent() , new File(path).getName());
                    }
                }
                break;
        }
    }

    public void showBrowserRoot() {
        if (mBrowserFragment != null) {
            ((KanBoxBrowserFragment)mBrowserFragment).showRootDirs();
        }
    }


    @Override
    public void onFileDownloading(String url, String path, int progress) {
        refreshAdLayout();
    }

    @Override
    public void onFileDownloaded(String url, String path, int status) {
        refreshAdLayout();
        if (url.startsWith("http://www.kanbox.com/")) {
            if (status == FileDownloader.STATUS.FAILED.ordinal()) {
                Toast.makeText(getActivity(), R.string.kanbox_apk_download_failed, Toast.LENGTH_SHORT).show();
            } else if (status == FileDownloader.STATUS.PAUSED.ordinal()) {
                Toast.makeText(getActivity(), R.string.kanbox_apk_download_paused, Toast.LENGTH_SHORT).show();
            } else if (status == FileDownloader.STATUS.SUCCESS.ordinal()) {
                if (new File(path).exists()) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.fromFile(new File(path)));
//                    startActivity(intent);
                    FileAction.viewFile(getActivity(), path);
                }
            }
        }
    }

}
