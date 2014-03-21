package com.hufeng.filemanager.apprate;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.hufeng.filemanager.R;

import java.lang.Thread.UncaughtExceptionHandler;

public class AppRate implements android.content.DialogInterface.OnClickListener, OnCancelListener {

    private static final String TAG = "AppRater";

    private Activity hostActivity;
    private OnClickListener clickListener;
    private SharedPreferences preferences;

    private long minLaunchesUntilPrompt = 5;
    private long minDaysUntilPrompt = 1;

    private boolean showIfHasCrashed = false;


    public AppRate(Activity hostActivity) {
        this.hostActivity = hostActivity;
        preferences = hostActivity.getSharedPreferences(PrefsContract.SHARED_PREFS_NAME, 0);
    }

    /**
     * Reset all the data collected about number of launches and days until first launch.
     * @param context A context.
     */
    public static void reset(Context context) {
        context.getSharedPreferences(PrefsContract.SHARED_PREFS_NAME, 0).edit().clear().commit();
        Log.d(TAG, "Cleared AppRate shared preferences.");
    }

    public void init() {
        if (!showIfHasCrashed) {
            initExceptionHandler();
        }
    }

    /**
     * Display the rate dialog if needed.
     */
    public boolean showIfNeeded() {

        Log.d(TAG, "Init AppRate");

        if (preferences.getBoolean(PrefsContract.PREF_DONT_SHOW_AGAIN, false) || (
                preferences.getBoolean(PrefsContract.PREF_APP_HAS_CRASHED, false) && !showIfHasCrashed)) {
                return false;
        }

        Editor editor = preferences.edit();

        // Get and increment launch counter.
        long launch_count = preferences.getLong(PrefsContract.PREF_LAUNCH_COUNT, 0) + 1;
        editor.putLong(PrefsContract.PREF_LAUNCH_COUNT, launch_count);

        // Get date of first launch.
        Long date_firstLaunch = preferences.getLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, date_firstLaunch);
        }

        editor.commit();

        // Show the rate dialog if needed.
        if (launch_count >= minLaunchesUntilPrompt) {
            if (System.currentTimeMillis() >= date_firstLaunch + (minDaysUntilPrompt * DateUtils.DAY_IN_MILLIS)) {
                showDefaultDialog();
                return true;
            }
        }
        return false;
    }

    /**
     * Initialize the {@link ExceptionHandler}.
     */
    private void initExceptionHandler() {

        Log.d(TAG, "Init AppRate ExceptionHandler");

        UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();

        // Don't register again if already registered.
        if (!(currentHandler instanceof ExceptionHandler)) {

            // Register default exceptions handler.
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(currentHandler, hostActivity));
        }
    }

    /**
     * Shows the default rate dialog.
     * @return
     */
    private void showDefaultDialog() {

        Log.d(TAG, "Create default dialog.");

//        String title = "Rate " + getApplicationName(hostActivity.getApplicationContext());
        String title = hostActivity.getString(R.string.app_rate_title);
//        String message = "If you enjoy using " + getApplicationName(hostActivity.getApplicationContext()) + ", please take a moment to rate it. Thanks for your support!";
        String message = hostActivity.getString(R.string.app_rate_content);
        String rate = hostActivity.getString(R.string.app_rate_rate_it);
        String remindLater = hostActivity.getString(R.string.app_rate_ask_again);
        String dismiss = hostActivity.getString(R.string.app_rate_no_thanks);

        new AlertDialog.Builder(hostActivity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(rate, this)
                .setNegativeButton(dismiss, this)
                .setNeutralButton(remindLater, this)
                .setOnCancelListener(this)
                .create().show();
    }


    @Override
    public void onCancel(DialogInterface dialog) {

        Editor editor = preferences.edit();
        editor.putLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, System.currentTimeMillis());
        editor.putLong(PrefsContract.PREF_LAUNCH_COUNT, 0);
        editor.commit();
    }

    /**
     * @param onClickListener A listener to be called back on.
     * @return This {@link AppRate} object to allow chaining.
     */
    public AppRate setOnClickListener(OnClickListener onClickListener){
        clickListener = onClickListener;
        return this;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        Editor editor = preferences.edit();

        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                try
                {
                    hostActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + hostActivity.getPackageName())));
                }catch (ActivityNotFoundException e) {
                    Toast.makeText(hostActivity, R.string.app_rate_no_market, Toast.LENGTH_SHORT).show();
                }
                editor.putBoolean(PrefsContract.PREF_DONT_SHOW_AGAIN, true);
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                editor.putBoolean(PrefsContract.PREF_DONT_SHOW_AGAIN, true);
                break;

            case DialogInterface.BUTTON_NEUTRAL:
                editor.putLong(PrefsContract.PREF_DATE_FIRST_LAUNCH, System.currentTimeMillis());
                editor.putLong(PrefsContract.PREF_LAUNCH_COUNT, 0);
                break;

            default:
                break;
        }

        editor.commit();
        dialog.dismiss();

        if(clickListener != null){
            clickListener.onClick(dialog, which);
        }
    }

    /**
     * @param context A context of the current application.
     * @return The application name of the current application.
     */
    private static final String getApplicationName(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (final NameNotFoundException e) {
            applicationInfo = null;
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");
    }
}
