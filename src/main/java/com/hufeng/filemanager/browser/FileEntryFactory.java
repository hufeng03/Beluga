package com.hufeng.filemanager.browser;

import java.io.File;

/**
 * Created by feng on 14-1-10.
 */
public class FileEntryFactory {
    public static FileEntry makeFileObject(String path) {
        switch (FileUtils.getFileType(new File(path))) {
            case FileUtils.FILE_TYPE_IMAGE:
                return new ImageEntry(path);
            default:
                return new FileEntry(path);
        }
    }
}
