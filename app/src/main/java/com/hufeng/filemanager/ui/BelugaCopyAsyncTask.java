package com.hufeng.filemanager.ui;

import android.content.Context;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.storage.StorageUtil;

import java.io.File;

/**
 * Created by feng on 14-2-15.
 */
public class BelugaCopyAsyncTask extends BelugaActionAsyncTask {

    public BelugaCopyAsyncTask(BelugaActionAsyncTaskCallbackDelegate bac, String folder) {
        super(bac, folder);
    }

    @Override
    public boolean run(FileEntry[] params) {
        boolean result = copy(params, mFolderPath);
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getResources().getString(R.string.progress_paste_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getResources().getString(R.string.progress_paste_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean result) {
        int toast_info_id = 0;

        if (result) {
            toast_info_id = R.string.file_copy_finish;
        } else {
            toast_info_id = R.string.file_copy_failed;
        }

        return toast_info_id == 0 ? "" : context.getResources().getString(toast_info_id);
    }


    public boolean copy(FileEntry[] entries, String targetFolderPath)
    {
        boolean result = true;
        for(FileEntry entry:entries)
        {
            File file = new File(entry.path);
            String name = entry.name;
            File targetFile = new File(targetFolderPath,name);

            int i = 1;
            int pos = name.lastIndexOf(".");
            while(targetFile.exists())
            {
                if(pos>0)
                {
                    targetFile = new File(targetFolderPath, name.substring(0,pos)+"("+i+")"+name.substring(pos));
                }
                else
                {
                    targetFile = new File(targetFolderPath, name+"("+i+")");
                }
                i++;
            }
            if(isCancelled())
            {
                return false;
            }
            if(file.isDirectory())
            {
                if(!copyDir(file, targetFile))
                    result = false;
            }
            else
            {
                if(!FileAction.copyFile(file, targetFile))
                    result = false;
                else
                    FileAction.addFileInDatabase(targetFile);
            }
        }
        return result;
    }

    private boolean copyDir(File file, File plasPath){
        plasPath.mkdir();
        File[] f=file.listFiles();
        boolean result = true;
        if(f!=null)
        {
            for(File newFile:f){
                if(isCancelled())
                {
                    return false;
                }
                if(newFile.isDirectory()){
                    File files=new File(file.getPath()+"/"+newFile.getName()) ;
                    File plasPaths=new File(plasPath.getPath()+"/"+newFile.getName());
                    if(!copyDir(files, plasPaths))
                        result = false;
                }else {
                    String newPath=plasPath.getPath()+"/"+newFile.getName();
                    File newPlasFile=new File(newPath);
                    if(FileAction.copyFile(newFile, newPlasFile))
                        FileAction.addFileInDatabase(newPlasFile);
                    else
                        result = false;
                }
            }
        }
        return result;
    }
}
