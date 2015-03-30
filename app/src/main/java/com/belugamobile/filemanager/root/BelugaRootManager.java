package com.belugamobile.filemanager.root;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.belugamobile.filemanager.FileManager;
import com.belugamobile.filemanager.PreferenceKeys;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.dialog.BelugaDialogFragment;
import com.belugamobile.filemanager.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

                            if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING) {
                                Toast.makeText(context, "Error opening root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
                                rootSession = null;
                                PreferenceManager.getDefaultSharedPreferences(FileManager.getAppContext()).edit().putBoolean(PreferenceKeys.ROOT_EXPLORER_ENABLE, false).commit();
                                BelugaDialogFragment.showRootFailureDialog((FragmentActivity) context);
                            } else {
                                // Shell is up: send our first request
                                sendRootShellInitialCommand();
                            }
                        }
                    });
        }
    }

    public void destory() {
        rootSession = null;
    }

    private void sendRootShellInitialCommand() {
        rootSession.addCommand(new String[]{"id", "mount -o remount,rw /"}, 0, new Shell.OnCommandResultListener(){
            @Override
            public void onCommandResult(int commandCode, int exitCode, List<String> output) {
//                if (exitCode < 0) {
//                    Toast.makeText(FileManager.getAppContext(), "Error opening root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(FileManager.getAppContext(), "Success opening root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                }
                for (String line : output) {
                    Log.i(TAG, line);
                }
            }
        });
    }


    // You should never, never call this function in UI Thread
    public boolean waitForIdle() {
        if (rootSession == null) {
            return false;
        } else {
            boolean result =  rootSession.waitForIdle();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public String[] listSync(String path) {
        if (rootSession == null) {
            return null;
        }  else {
            final List<String> result = new ArrayList<String>();
            rootSession.addCommand("ls -a '"+path+"'", 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
//                    if (exitCode < 0) {
//                        Toast.makeText(FileManager.getAppContext(), "Error copy in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(FileManager.getAppContext(), "Success copy in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    }
                    for (String line : output) {
                        Log.i(TAG, line);
                        result.add(line);
                    }
                }
            });
            rootSession.waitForIdle();
            return result.toArray(new String[result.size()]);
        }
    }

    public String[] infoListSync(String path) {
        if (rootSession == null) {
            return null;
        }  else {
            final List<String> result = new ArrayList<String>();
            rootSession.addCommand("ls -al '"+path+"'", 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
//                    if (exitCode < 0) {
//                        Toast.makeText(FileManager.getAppContext(), "Error list in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(FileManager.getAppContext(), "Success copy in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    }
                    for (String line : output) {
                        Log.i(TAG, line);
                        result.add(line);
                    }
                }
            });
            rootSession.waitForIdle();
            return result.toArray(new String[result.size()]);
        }
    }

    public boolean copyDeleteFileAsRoot(List<BelugaFileEntry> entries, List<String> newPaths) {
        if (rootSession == null) {
            return false;
        } else if (entries.size() != newPaths.size()) {
            return false;
        } else {
            List<String> commands = new ArrayList<String>();
            int size = entries.size();
            for (int i = 0; i < size; i++) {
                BelugaFileEntry entry = entries.get(i);
                String newPath = newPaths.get(i);
                commands.add(entry.isDirectory?BelugaRootHelper.commandForCopyDeleteFolder(entry.path, newPath) : BelugaRootHelper.commandForCopyDeleteFile(entry.path, newPath));
            }
            for (String command : commands) {
                Log.i(TAG, command);
            }
            rootSession.addCommand(commands, 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
//                    if (exitCode < 0) {
//                        Toast.makeText(FileManager.getAppContext(), "Error copy in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(FileManager.getAppContext(), "Success copy in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    }
                    for (String line : output) {
                        Log.i(TAG, line);
                    }
                }
            });
            return true;
        }
    }

    public boolean copyFileAsRoot(List<BelugaFileEntry> entries, List<String> newPaths) {
        if (rootSession == null) {
            return false;
        } else if (entries.size() != newPaths.size()) {
            return false;
        } else {
            List<String> commands = new ArrayList<String>();
            int size = entries.size();
            for (int i = 0; i < size; i++) {
                BelugaFileEntry entry = entries.get(i);
                String newPath = newPaths.get(i);
                commands.add(entry.isDirectory ? BelugaRootHelper.commandForCopyFolder(entry.path, newPath) : BelugaRootHelper.commandForCopyFile(entry.path, newPath));
            }
            for (String command : commands) {
                Log.i(TAG, command);
            }
            rootSession.addCommand(commands, 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
//                    if (exitCode < 0) {
//                        Toast.makeText(FileManager.getAppContext(), "Error copy in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(FileManager.getAppContext(), "Success copy in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    }
                    for (String line : output) {
                        Log.i(TAG, line);
                    }
                }
            });
            return true;
        }
    }

    public boolean moveFileAsRoot(List<BelugaFileEntry> entries, List<String> newPaths) {
        if (rootSession == null) {
            return false;
        } else if (entries.size() != newPaths.size()) {
            return false;
        } else {
            List<String> commands = new ArrayList<String>();
            int size = entries.size();
            for (int i = 0; i < size; i++) {
                BelugaFileEntry entry = entries.get(i);
                String newPath = newPaths.get(i);
                commands.add(BelugaRootHelper.commandForMove(entry.path, newPath));
            }
            for (String command : commands) {
                Log.i(TAG, command);
            }
            rootSession.addCommand(commands, 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
//                    if (exitCode < 0) {
//                        Toast.makeText(FileManager.getAppContext(), "Error copy in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(FileManager.getAppContext(), "Success copy in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    }
                    for (String line : output) {
                        Log.i(TAG, line);
                    }
                }
            });
            return true;
        }
    }

    public boolean renameFileAsRoot(BelugaFileEntry entry, String newPath) {
        if (rootSession == null) {
            return false;
        } else {
            List<String> commands = new ArrayList<String>();
            int idx = newPath.lastIndexOf("/");
            if (idx > 0) {
                String newFolder = newPath.substring(0, idx);
                if (!new File(newFolder).exists()) {
                    commands.add(BelugaRootHelper.commandForCreateFolderRecusively(newFolder));
                }
            }
            commands.add(BelugaRootHelper.commandForMove(entry.path, newPath));

            rootSession.addCommand(commands, 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
//                    if (exitCode < 0) {
//                        Toast.makeText(FileManager.getAppContext(), "Error rename in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(FileManager.getAppContext(), "Success rename in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    }
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
            rootSession.addCommand(BelugaRootHelper.commandForCreateFolder(path), 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
//                    if (exitCode < 0) {
//                        Toast.makeText(FileManager.getAppContext(), "Error mkdir in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(FileManager.getAppContext(), "Success mkdir in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    }
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
                commands.add(entry.isDirectory?BelugaRootHelper.commandForDeleteFolder(entry.path) : BelugaRootHelper.commandForDeleteFile(entry.path));
            }
            rootSession.addCommand(commands, 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
//                    if (exitCode < 0) {
//                        Toast.makeText(FileManager.getAppContext(), "Error deleting in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(FileManager.getAppContext(), "Success deleting in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    }
                    for (String line : output) {
                        Log.i(TAG, line);
                    }
                }
            });
            return true;
        }
    }

    public boolean executeCommands(List<String> commands) {
        if (rootSession == null) {
            return false;
        } else {
            rootSession.addCommand(commands, 0, new Shell.OnCommandResultListener() {
                @Override
                public void onCommandResult(int commandCode, int exitCode, List<String> output) {
//                    if (exitCode < 0) {
//                        Toast.makeText(FileManager.getAppContext(), "Error executing in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(FileManager.getAppContext(), "Success executing in root shell: exitCode " + exitCode, Toast.LENGTH_SHORT).show();
//                    }
                    for (String line : output) {
                        Log.i(TAG, line);
                    }
                }
            });
            return true;
        }
    }
}
