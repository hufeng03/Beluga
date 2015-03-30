package com.belugamobile.filemanager.root;

/**
 * Created by Feng Hu on 15-03-28.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaRootHelper {

    public static String commandForCopyFile(String oldPath, String newPath) {
        return "cp '"+oldPath+"' '"+newPath+"'";
    }

    public static String commandForCopyFolder(String oldPath, String newPath) {
        return "cp -r '"+oldPath+"' '"+newPath+"'";
    }

    public static String commandForMove(String oldPath, String newPath) {
        return "mv '"+oldPath+"' '"+newPath+"'";
    }

    public static String commandForDeleteFile(String path) {
        return "rm '"+path+"'";
    }

    public static String commandForDeleteFolder(String path) {
        return "rm -rf '"+path+"'";
    }

    public static String commandForCreateFolder(String path) {
        return "mkdir "+path;
    }

    public static String commandForCreateFolderRecusively(String path) {
        return "mkdir -p "+path;
    }

    public static String commandForCopyDeleteFile(String oldPath, String newPath) {
        return "cp '" + oldPath + "' '" + newPath + "' && rm '" + oldPath + "'";
    }

    public static String commandForCopyDeleteFolder(String oldPath, String newPath) {
        return "cp -r '" + oldPath + "' '" + newPath + "' && rm -rf '" + oldPath + "'";
    }

}
