package com.hufeng.filemanager.app;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import com.hufeng.filemanager.utils.LogUtil;

public class NameCache {
    private static final String TAG = NameCache.class.getSimpleName();
    
    private static Map<String, String> mCache = Collections
            .synchronizedMap(new HashMap<String, String>());

    public String get(String key) {
        return mCache.get(key);
    }

    public void set(String key, String value) {
//        String bm = mCache.get(key);
//        if (bm != null && bm != value)
//            bm.recycle();
        mCache.put(key, value);
    }

    public void clear() {
        Collection<String> values = mCache.values();
        LogUtil.d(TAG, values.size() + " bitmaps recycled.");
        for (String bm : values) {
//            if (bm != null)
//                bm.recycle();
        }
        mCache.clear();
    }
}


