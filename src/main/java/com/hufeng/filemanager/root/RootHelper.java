package com.hufeng.filemanager.root;

import java.io.DataOutputStream;
import java.io.File;

/**
 * Created by feng on 13-12-23.
 */
public class RootHelper {

    private final static int sSystemRootStateUnknow =-1;
    private final static int sSystemRootStateDisable =0;
    private final static int sSystemRootStateEnable =1;
    private static int systemRootState= sSystemRootStateUnknow;

    public static boolean isRootedPhone() {
        if(systemRootState== sSystemRootStateEnable) {
            return true;
        }
        else if(systemRootState== sSystemRootStateDisable) {
            return false;
        }
        File f=null;
        final String kSuSearchPaths[]={"/system/bin/","/system/xbin/","/system/sbin/","/sbin/","/vendor/bin/"};
        try{
            for(int i=0;i<kSuSearchPaths.length;i++) {
                f=new File(kSuSearchPaths[i]+"su");
                if(f!=null&&f.exists()) {
                    systemRootState= sSystemRootStateEnable;
                    return true;
                }
            }
        }catch(Exception e){
        }
        systemRootState= sSystemRootStateDisable;
        return false;
    }

    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
}
