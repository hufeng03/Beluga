package com.hufeng.filemanager.ui;

import android.content.Context;
import android.text.TextUtils;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.storage.StorageUtil;

import java.io.File;

/**
 * Created by feng on 14-2-15.
 */
public class BelugaMoveAsyncTask extends BelugaActionAsyncTask {


    public BelugaMoveAsyncTask(BelugaActionAsyncTaskCallbackDelegate bac, String folder) {
        super(bac, folder);
    }

    @Override
    public boolean run(FileEntry[] params) {
        boolean result = move(params, mFolderPath);
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

        if (result)
            toast_info_id = R.string.file_move_finish;
        else {
            toast_info_id = R.string.file_move_failed;
        }
        return toast_info_id == 0 ? "" : context.getResources().getString(toast_info_id);
    }


    //文件复制
    public boolean move(FileEntry[] entries, String targetFolderPath)
    {
        //	File targetDir = new File(target);
        boolean result = true;
        for(FileEntry entry:entries)
        {
            File file = new File(entry.path);
            String name = file.getName();
            File targetFile = new File(targetFolderPath, name);
            boolean flag_copy_delete = false;
            if(file.getPath().equals(targetFile.getPath())) {
                continue;
            }
            StorageManager stor = StorageManager.getInstance(FileManager.getAppContext());
            String device0 = stor.getStorageForPath(entry.path);
            String device = stor.getStorageForPath(targetFolderPath);
            if(!TextUtils.isEmpty(device) && !TextUtils.isEmpty(device0) && !device0.equals(device))
            {
                //not in same sdcard
                flag_copy_delete = true;
            }

            String targetFile_path = FileUtils.getFilename(new File(targetFolderPath, name).getAbsolutePath());
            targetFile = new File(targetFile_path);
            if(isCancelled())
            {
                return false;
            }
            if(file.isDirectory())
            {
                if(!flag_copy_delete)
                {
                    if(!file.renameTo(targetFile))
                        result = false;
                    else
                        FileAction.moveDirInDatabase(file, targetFile);
                }
                else
                {
                    if(!moveDirByCopyAndDelete(file,targetFile))
                        result = false;
                }
            }
            else
            {
                if(!flag_copy_delete)
                {
                    if(!file.renameTo(targetFile))
                        result = false;
                    else
                        FileAction.updateFileInDatabase(file, targetFile);
                }
                else
                {
                    if(!FileAction.copyFile(file, targetFile))
                        result = false;
                    else
                    {
                        file.delete();
                        FileAction.updateFileInDatabase(file,targetFile);
                    }
                }
            }
        }
        return result;
    }


    //文件夹复制，包括文件夹里面的文件复制
    private boolean moveDirByCopyAndDelete(File file, File plasPath){
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

                    if(!FileAction.copyFile(newFile, newPlasFile))
                        result = false;
                    else
                    {
                        newFile.delete();
                        FileAction.updateFileInDatabase(newFile, newPlasFile);
                    }
                }
            }
            file.delete();
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
