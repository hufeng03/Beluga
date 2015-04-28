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
public class BelugaUndoFavoriteAsyncTask extends BelugaActionAsyncTask {

    public BelugaActionType mType = BelugaActionType.UNDO_FAVORITE;

    public BelugaUndoFavoriteAsyncTask(Context context, BelugaActionAsyncTask.BelugaActionAsyncTaskCallbackDelegate bac) {
        super(context, bac);
    }

    @Override
    public boolean run() {
        boolean result = unFavoriteFileEntryOneByOne();
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getString(R.string.progress_undo_favorite_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getString(R.string.progress_undo_favorite_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean rst) {
        int toast_info_id;
        if (rst) {
            toast_info_id =  R.string.file_undofavorite_finish;
        }
        else {
            toast_info_id = R.string.file_undofavorite_failed;
        }
        return toast_info_id == 0 ? "" : context.getString(toast_info_id);
    }

    private boolean unFavoriteFileEntryOneByOne() {
        boolean result = true;
        for (BelugaFileEntry entry : mOriginalEntries) {
            if (isCancelled()) {
                return false;
            }
            BelugaProviderHelper.setUndoFavoriteInBelugaDatabase(mContext, entry.path);
            entry.isFavorite = false;
            publishActionProgress(entry);
        }
        return result;
    }
}
