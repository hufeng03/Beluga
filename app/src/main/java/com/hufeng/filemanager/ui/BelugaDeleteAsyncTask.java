package com.hufeng.filemanager.ui;

import android.content.Context;
import android.text.TextUtils;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;

import java.io.File;

/**
 * Created by feng on 14-2-15.
 */
public class BelugaDeleteAsyncTask extends BelugaActionAsyncTask {

    public BelugaDeleteAsyncTask(BelugaActionAsyncTaskCallbackDelegate bac) {
        super(bac);
    }

    @Override
    public boolean run(FileEntry[] params) {
        boolean result = delete(params);
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getString(R.string.progress_delete_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getString(R.string.progress_delete_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean rst) {
        int toast_info_id;
        if (rst){
            toast_info_id =  R.string.file_delete_finish;
        }
        else
        {
            toast_info_id = R.string.file_delete_failed;
        }
        return toast_info_id == 0 ? "" : context.getString(toast_info_id);
    }

    public boolean delete(FileEntry[] paths)
    {
        boolean result = true;
        for(FileEntry path:paths)
        {
            if(isCancelled())
                return false;
            if(!delete(path))
            {
                result = false;
            }
        }
        return result;
    }

    private boolean delete(FileEntry entry)
    {
        File delFile = new File(entry.path);
        if(delFile.isDirectory())
            return deleteDir(delFile);
        else
        {
            boolean result = true;
            if(delFile.exists() && !delFile.delete()) {
                result = false;
            } else {
                FileAction.deleteFileInDatabase(delFile);
            }
            return result;
        }
    }

    private boolean deleteDir(File delFile){
        if(!delFile.exists())
            return true;
        File[] f=delFile.listFiles();//取得文件夹里面的路径
        if(f==null || f.length==0){
            return delFile.delete();
        }
        else {
            boolean result = true;
            for(File nFile:f){
                if(isCancelled())
                    return false;
                if(nFile.isDirectory()){
                    if(!deleteDir(nFile))
                        result = false;
                }else {
                    if(!nFile.delete())
                        result = false;
                    else
                        FileAction.deleteFileInDatabase(nFile);
                }
            }
            result = delFile.delete();
            return result;
        }
    }

};
