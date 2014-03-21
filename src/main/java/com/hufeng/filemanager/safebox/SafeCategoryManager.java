package com.hufeng.filemanager.safebox;

import android.content.Context;
import android.content.res.Resources;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileUtils;

import java.util.ArrayList;

/**
 * Created by feng on 13-10-3.
 */
public class SafeCategoryManager {

    public static SafeCategoryEntry[] getAllSafeCategories(Context context){
        ArrayList<SafeCategoryEntry> safeCategoryArray = new ArrayList<SafeCategoryEntry>();
        Resources  res = context.getResources();
//        safeCategoryArray.add(new SafeCategoryEntry(res.getString(R.string.category_music), res.getDrawable(R.drawable.file_category_icon_audio), FileUtils.FILE_TYPE_AUDIO));
        safeCategoryArray.add(new SafeCategoryEntry(res.getString(R.string.category_video), res.getDrawable(R.drawable.file_category_icon_video), FileUtils.FILE_TYPE_VIDEO));
        safeCategoryArray.add(new SafeCategoryEntry(res.getString(R.string.category_picture), res.getDrawable(R.drawable.file_category_icon_image), FileUtils.FILE_TYPE_IMAGE));

        return safeCategoryArray.toArray(new SafeCategoryEntry[safeCategoryArray.size()]);

    }

}
