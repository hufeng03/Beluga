package com.belugamobile.filemanager.root;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.belugamobile.filemanager.FileManager;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Feng Hu on 15-03-22.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaRootManager {

    private static final String TAG = BelugaRootManager.class.getSimpleName();

    private static BelugaRootManager sInstance = new BelugaRootManager();

    private BelugaRootManager() {

    }

    /**
     * This method gets instance of BelugaRootManager. Before calling its method, must call init().
     *
     * @return instance of MountPointManager
     */
    public static BelugaRootManager getInstance() {
        return sInstance;
    }


    // SuperSU
    private static Shell.Interactive rootSession;

    /**
     * This method initializes BelugaRootManager.
     *
     * @param context Context to use
     */
    public void init(final Context context) {
        if (rootSession == null) {
            // start the shell in the background and keep it alive as long as the app is running
            rootSession = new Shell.Builder().
                    useSU().
                    setWantSTDERR(true).
                    setWatchdogTimeout(5).
                    setMinimalLogging(true).
                    open(new Shell.OnCommandResultListener() {

                        // Callback to report whether the shell was successfully started up
                        @Override
                        public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                            // note: this will FC if you rotate the phone while the dialog is up
                            LogUtil.i(TAG, "audit RootShell return " + commandCode + ", " + exitCode);
//                            dialog.dismiss();
//
                            if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING) {
                                Toast.makeText(context, "Error opening root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
                            } else {
                                // Shell is up: send our first request
                                sendTestRootCommand();
                            }
                        }
                    });
        }
    }

    private void sendTestRootCommand() {
        rootSession.addCommand(new String[]{"id", "ls -al /", "mount -o remount,rw /", "rm -rf /ttt",}, 0, new Shell.OnCommandResultListener(){
            @Override
            public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                if (exitCode < 0) {
                    Toast.makeText(FileManager.getAppContext(), "Error opening root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FileManager.getAppContext(), "Success opening root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
                }
                for (String line : output) {
                    Log.i(TAG, line);
                }
            }
        });
    }


    public boolean waitForIdle() {
        if (rootSession == null) {
            return false;
        } else {
            return waitForIdle();
        }
    }

    public boolean renameFileAsRoot(String oldPath, String newPath) {
        if (rootSession == null) {
            return false;
        } else {
            rootSession.addCommand("mv '"+oldPath+"' '"+newPath+"'", 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                    if (exitCode < 0) {
                        Toast.makeText(FileManager.getAppContext(), "Error rename in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FileManager.getAppContext(), "Success rename in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
                    }
                    for (String line : output) {
                        Log.i(TAG, line);
                    }
                }
            });
            return true;
        }
    }

    public boolean createFolderAsRoot(String path) {
        if (rootSession == null) {
            return false;
        } else {
            rootSession.addCommand("mkdir '"+path+"'", 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                    if (exitCode < 0) {
                        Toast.makeText(FileManager.getAppContext(), "Error mkdir in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FileManager.getAppContext(), "Success mkdir in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
                    }
                    for (String line : output) {
                        Log.i(TAG, line);
                    }
                }
            });
            return true;
        }
    }

    public boolean deleteAsRoot(List<BelugaFileEntry> entries) {
        if (rootSession == null) {
            return false;
        } else {
            List<String> commands = new ArrayList<String>();
            for (BelugaFileEntry entry : entries) {
                commands.add("rm" + (entry.isDirectory?" -rf '":" '") + entry.path+"'");
            }
            rootSession.addCommand(commands, 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                    if (exitCode < 0) {
                        Toast.makeText(FileManager.getAppContext(), "Error deleting in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FileManager.getAppContext(), "Success deleting in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
                    }
                    for (String line : output) {
                        Log.i(TAG, line);
                    }
                }
            });
            return true;
        }
    }

}
