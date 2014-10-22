package com.hufeng.playzip;

import android.app.Activity;
import android.os.AsyncTask;

import com.hufeng.filemanager.BaseFragment;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.storage.StorageManager;

/**
 * Created by feng on 2014-03-30.
 */
public class ZipWorkFragment extends BaseFragment {

    private ZipAsyncTask mTask;

    public static ZipWorkFragment newZipWorkFragment() {
        ZipWorkFragment fragment = new ZipWorkFragment();
        return fragment;
    }

    public ZipWorkFragment() {
        setRetainInstance(true);
    }

    public interface ZipWorkProgressInterface {
        public void onUnzipProgress(String path, int progress);
        public void onZipProgress(String path, int progress);
    }

    public ZipWorkProgressInterface mInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mInterface = (ZipWorkProgressInterface)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInterface = null;
    }

    public void unZipSingleFile(String zip_file, String single_file) {
        if (mTask != null) {
            return;
        }
        mTask = new ZipAsyncTask(zip_file);
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, single_file);
    }


    private class ZipAsyncTask extends AsyncTask<String,Integer,Void> {

        private String mZipFile;

        public ZipAsyncTask(String zip_file) {
            mZipFile = zip_file;
        }

        @Override
        protected Void doInBackground(String... params) {
            String dest = StorageManager.getInstance(FileManager.getAppContext()).getPrimaryExternalStorage();
            new ZipUtil().unZip(params[0], dest);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mInterface != null) {
                mInterface.onZipProgress(mZipFile, 0);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mTask = null;
            if (mInterface != null) {
                mInterface.onZipProgress(mZipFile, 100);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
