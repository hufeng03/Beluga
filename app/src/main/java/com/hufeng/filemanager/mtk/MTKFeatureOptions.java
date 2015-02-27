package com.hufeng.filemanager.mtk;

import java.lang.reflect.Field;

/**
 * Created by Feng Hu on 15-02-26.
 * <p/>
 * TODO: Add a class header comment.
 */
public class MtkFeatureOptions {

    public static boolean isMtkSDSwapSupported() {
        try {
            Class<?> featureOptionClass = Class.forName("com.mediatek.common.featureoption.FeatureOption");
            try {
                Field isSDSwapSupported = featureOptionClass.getField("MTK_2SDCARD_SWAP");
                try {
                    return isSDSwapSupported.getBoolean(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMtkHotKnotSupported() {
        try {
            Class<?> featureOptionClass = Class.forName("com.mediatek.common.featureoption.FeatureOption");
            try {
                Field isHotKnotSupported = featureOptionClass.getField("MTK_HOTKNOT_SUPPORT");
                try {
                    return isHotKnotSupported.getBoolean(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }



}
