package com.hufeng.filemanager.ui;

import android.content.Context;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.helper.BelugaProviderHelper;
import com.hufeng.filemanager.helper.MultiMediaStoreHelper;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaFavoriteAsyncTask extends BelugaActionAsyncTask {

    public BelugaFavoriteAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate bac) {
        super(context, bac);
    }

    @Override
    public boolean run() {
        boolean result = favoriteFileEntryOneByOne();
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getString(R.string.progress_favorite_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getString(R.string.progress_favorite_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean rst) {
        int toast_info_id;
        if (rst){
            toast_info_id =  R.string.file_favorite_finish;
        }
        else
        {
            toast_info_id = R.string.file_favorite_failed;
        }
        return toast_info_id == 0 ? "" : context.getString(toast_info_id);
    }

    private boolean favoriteFileEntryOneByOne() {
        boolean result = true;
        for (FileEntry entry : mFileEntries) {
            if (isCancelled()) {
                return false;
            }
            BelugaProviderHelper.setFavoriteInBelugaDatabase(mContext, entry.path);
            publishActionProgress(entry);
        }
        return result;
    }
}
