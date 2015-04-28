package com.belugamobile.filemanager.ui;

import android.content.Context;

import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaProviderHelper;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaFavoriteAsyncTask extends BelugaActionAsyncTask {

    public BelugaActionType mType = BelugaActionType.FAVORITE;

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
        for (BelugaFileEntry entry : mOriginalEntries) {
            if (isCancelled()) {
                return false;
            }
            BelugaProviderHelper.setFavoriteInBelugaDatabase(mContext, entry.path);
            entry.isFavorite = true;
            publishActionProgress(entry);
        }
        return result;
    }
}
