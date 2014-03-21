package com.hufeng.filemanager.app;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;

import com.hufeng.filemanager.utils.LogUtil;

public class IconCache {
    private static final String TAG = IconCache.class.getSimpleName();
    
    private Map<String, Drawable> mCache = Collections
            .synchronizedMap(new HashMap<String, Drawable>());

    public Drawable get(String key) {
        return mCache.get(key);
    }

    public void set(String key, Drawable value) {
        Drawable bm = mCache.get(key);
//        if (bm != null && bm != value)
//            bm.recycle();
        mCache.put(key, value);
    }

    public void clear() {
        Collection<Drawable> values = mCache.values();
        LogUtil.d(TAG, values.size() + " bitmaps recycled.");
        for (Drawable bm : values) {
//            if (bm != null)
//                bm.recycle();
        }
        mCache.clear();
    }
}

