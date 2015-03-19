package com.belugamobile.filemanager.helper;

import com.belugamobile.filemanager.Constants;

import java.io.File;

/**
 * Created by Feng Hu on 15-02-24.
 * <p/>
 * TODO: Add a class header comment.
 */
public class FilePropertyHelper {

    public static boolean checkIsWritable(String path) {
        boolean can_write = new File(path).canWrite();
        if (can_write && Constants.TRY_TO_TEST_WRITE) {
            if(new File(path, ".test_writable").mkdir()){
                new File(path, ".test_writable").delete();
            } else {
                can_write = false;
            }
        }
        return can_write;
    }
}
