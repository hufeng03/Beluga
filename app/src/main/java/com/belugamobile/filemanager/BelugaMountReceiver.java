package com.belugamobile.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import com.belugamobile.filemanager.mount.MountPointManager;
import com.belugamobile.filemanager.mtk.MTKFeatureOptions;

import java.util.ArrayList;

/**
 * Created by Feng Hu on 15-02-26.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaMountReceiver extends BroadcastReceiver {
    private static final String TAG = "MountReceiver";

    private final MountPointManager mMountPointManager;
    private final ArrayList<MountListener> mMountListenerList = new ArrayList<MountListener>();
    private static final String INTENT_SD_SWAP = "com.mediatek.SD_SWAP";

    public interface MountListener {
        /**
         * This method will be called to do things before MountPoint init.
         */
        // void prepareForMount(String mountPoint);

        /**
         * This method will be called when receive a mounted intent.
         */
        void onMounted(String mountPoint);

        /**
         * This method will be implemented by its class who implements this
         * interface, and called when receive a unMounted intent.
         *
         * @param unMountPoint the path of mount point
         */
        void onUnMounted(String unMountPoint);

        /**
         * This method cancel the current action on the SD card which will be
         * unmounted.
         */
        void onEjected(String unMountPoint);

        /**
         * This method re-load volume info when sd swap on/off
         *
         */
        void onSdSwap();
    }

    /**
     * This method gets MountPointManager's instance
     */
    public BelugaMountReceiver() {
        mMountPointManager = MountPointManager.getInstance();
    }

    /**
     * This method adds listener for activities
     *
     * @param listener listener of certain activity to respond mounted and
     *            unMounted intent
     */
    public void registerMountListener(MountListener listener) {
        mMountListenerList.add(listener);
    }

    public void unregisterMountListener(MountListener listener) {
        mMountListenerList.remove(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String mountPoint = null;
        Uri mountPointUri = intent.getData();
        if (mountPointUri != null) {
            mountPoint = mountPointUri.getPath();
        }
        Log.d(TAG, "onReceive: " + action + " mountPoint: " + mountPoint);
        if (INTENT_SD_SWAP.equals(action)) {
            mMountPointManager.init(context);
            for (MountListener listener : mMountListenerList) {
                Log.d(TAG, "onReceive, handle SD_SWAP ");
                listener.onSdSwap();
            }
            return;
        }

        if (mountPoint == null || mountPointUri == null) {
            return;
        }

        if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            // cancel the current operation before MountPointManager init
            // to avoid concurrently access to mount point array list.
            mMountPointManager.init(context);
            for (MountListener listener : mMountListenerList) {
                listener.onMounted(mountPoint);
            }
        } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
            if (mMountPointManager.changeMountState(mountPoint, false)) {
                for (MountListener listener : mMountListenerList) {
                    listener.onUnMounted(mountPoint);
                }
            }
        } else if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
            mMountPointManager.init(context);
            for (MountListener listener : mMountListenerList) {
                listener.onEjected(mountPoint);
            }
        }
    }

    /**
     * Register a MountReceiver for context.
     *
     * @param context Context to use
     * @return A mountReceiver
     */
    public static BelugaMountReceiver registerMountReceiver(Context context) {
        BelugaMountReceiver receiver = new BelugaMountReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        context.registerReceiver(receiver, intentFilter);

        if (MTKFeatureOptions.isMtkSDSwapSupported()) {
            IntentFilter intentFilterSDSwap = new IntentFilter();
            intentFilterSDSwap.addAction(INTENT_SD_SWAP);
            context.registerReceiver(receiver, intentFilterSDSwap);
        }
        return receiver;
    }
}
