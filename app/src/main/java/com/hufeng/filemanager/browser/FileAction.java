package com.hufeng.filemanager.browser;

import android.database.Cursor;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.provider.DataStructures.FavoriteColumns;

public class FileAction {
	
	private static final String LOG_TAG = "FileAction";


    public static boolean isFavorite(String path) {
        Cursor cursor = null;
        try {
            cursor = FileManager.getAppContext().getContentResolver().query(FavoriteColumns.CONTENT_URI,
                    null,
                    FavoriteColumns.FILE_PATH_FIELD+"=?",
                    new String[]{path},
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public static boolean isAllFavorite(String[] paths) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FavoriteColumns.FILE_PATH_FIELD + " IN(");
        for (int i = 0; i < paths.length; i++) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append('?');
        }
        stringBuilder.append(')');

        Cursor cursor = null;
        try {
            cursor = FileManager.getAppContext().getContentResolver().query(FavoriteColumns.CONTENT_URI, new String[]{"count(*) as count"}, stringBuilder.toString(), paths, null);
            if (cursor != null && cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count == paths.length) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }


    
}
