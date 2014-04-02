package com.hufeng.filemanager.kanbox;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.hufeng.filemanager.FileGridFragment;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.ui.FileViewHolder;
import com.kanbox.api.RequestListener;

import java.io.File;

/**
 * Created by feng on 3/10/2014.
 */
public class KanBoxUploadFragment extends FileGridFragment
        implements KanBoxApi.KanBoxApiListener, FileViewHolder.FileViewClicked {

    KanBoxUploadAdapter mAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getResources().getString(R.string.kanbox_uploading_empty));
        KanBoxApi.getInstance().registerKanBoxApiListener(this);
        mAdapter = new KanBoxUploadAdapter(getSherlockActivity(), this);
        setGridAdapter(mAdapter);
        reloadFiles();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KanBoxApi.getInstance().unRegisterKanBoxApiListener(this);
    }

    @Override
    public void reloadFiles() {
        mAdapter.setData(KanBoxApi.getInstance().getUploadingFiles());
    }

    @Override
    public void onKanBoxApiSuccess(int op_type, String path, String response) {
        if (op_type == RequestListener.OP_UPLOAD) {
            String tip = null;
            if("pause".equals(response)) {
                tip = getString(R.string.upload_paused, new File(path).getName());
            } else {
                tip = getString(R.string.upload_success, new File(path).getName());
            }
            Toast.makeText(getActivity(), tip, Toast.LENGTH_SHORT).show();
            reloadFiles();
        }
    }

    @Override
    public void onKanBoxApiFailed(int op_type, String path) {
        if (op_type == RequestListener.OP_UPLOAD) {
            String tip = getString(R.string.upload_failed, new File(path).getName());
            Toast.makeText(getActivity(), tip, Toast.LENGTH_SHORT).show();
            reloadFiles();
        }
    }

    @Override
    public void onKanBoxApiProgress(int op_type, String path, int progress) {
        if (op_type == RequestListener.OP_UPLOAD) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        clickGridItem(position);
    }

    @Override
    public void onFileStatusClicked(String path, long position) {
        clickGridItem(position);
    }

    private void clickGridItem(long position) {
        FileEntry entry = (FileEntry)getGridAdapter().getItem((int)position);
        if ( !TextUtils.isEmpty(entry.path) ) {
            KanBoxApi.getInstance().pauseUploadFile(entry.path);
        }
    }

    @Override
    public String[] getAllFiles() {
        return new String[0];
    }

    @Override
    public String getParentFile() {
        return null;
    }
}
