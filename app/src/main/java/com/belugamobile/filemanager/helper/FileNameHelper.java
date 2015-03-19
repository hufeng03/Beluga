package com.belugamobile.filemanager.helper;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by Feng Hu on 15-02-24.
 * <p/>
 * TODO: Add a class header comment.
 */
public class FileNameHelper {

    private static final String TAG = "FileNameHelper";

    /** File name's max length */
    public static final int FILENAME_MAX_LENGTH = 255;

    public static final int ERROR_CODE_NAME_EMPTY = -2;
    public static final int ERROR_CODE_NAME_TOO_LONG = -3;
    public static final int ERROR_CODE_NAME_VALID = 100;

    /**
     * This method check the file name is valid.
     *
     * @param fileName the input file name
     * @return valid or the invalid type
     */
    public static int checkFileName(String fileName) {
        if (TextUtils.isEmpty(fileName) || fileName.trim().length() == 0) {
            return ERROR_CODE_NAME_EMPTY;
        } else {
            try {
                int length = fileName.getBytes("UTF-8").length;
                // int length = fileName.length();
                Log.d(TAG, "checkFileName: " + fileName + ",length= " + length);
                if (length > FILENAME_MAX_LENGTH) {
                    Log.d(TAG, "checkFileName,fileName is too long,len=" + length);
                    return ERROR_CODE_NAME_TOO_LONG;
                } else {
                    return ERROR_CODE_NAME_VALID;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return ERROR_CODE_NAME_EMPTY;
            }
        }
    }

    /**
     * This method generates a new suffix if a name conflict occurs, ex: paste a file named
     * "stars.txt", the target file name would be "stars(1).txt"
     *
     * @param file the conflict file
     * @return a new name for the conflict file
     */

    public static File generateNextNewName(File file) {
        String parentDir = file.getParent();
        String fileName = file.getName();
        String ext = "";
        int newNumber = 0;
        if (file.isFile()) {
            int extIndex = fileName.lastIndexOf(".");
            if (extIndex != -1) {
                ext = fileName.substring(extIndex);
                fileName = fileName.substring(0, extIndex);
            }
        }

        if (fileName.endsWith(")")) {
            int leftBracketIndex = fileName.lastIndexOf("(");
            if (leftBracketIndex != -1) {
                String numeric = fileName.substring(leftBracketIndex + 1, fileName.length() - 1);
                if (numeric.matches("[0-9]+")) {
                    Log.v(TAG, "Conflict folder name already contains (): " + fileName
                            + "thread id: " + Thread.currentThread().getId());
                    try {
                        newNumber = Integer.parseInt(numeric);
                        newNumber++;
                        fileName = fileName.substring(0, leftBracketIndex);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Fn-findSuffixNumber(): " + e.toString());
                    }
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append(fileName).append("(").append(newNumber).append(")").append(ext);
        if (FileNameHelper.checkFileName(sb.toString()) < 0) {
            return null;
        }
        return new File(parentDir, sb.toString());
    }
}
