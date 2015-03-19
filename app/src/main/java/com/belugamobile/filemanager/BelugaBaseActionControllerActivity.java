package com.belugamobile.filemanager;

import android.support.v7.view.ActionMode;

import com.belugamobile.filemanager.ui.BelugaActionController;

/**
 * Created by feng on 14-11-24.
 */
public abstract class BelugaBaseActionControllerActivity extends BelugaBaseActionBarActivity implements
        BelugaActionController.BelugaActionControllerHostInterface {

    private ActionMode mActionMode;

    protected ActionMode getActionMode() {
        return mActionMode;
    }

    protected abstract BelugaActionController getActionController();

    @Override
    public void invalidate() {
        int selectedNum = 0;
        if (getActionController() != null) {
            selectedNum = getActionController().getFileSelectedSize();
        }

        if (mActionMode == null && selectedNum > 0) {
            mActionMode = startSupportActionMode(getActionController());
        } else if (mActionMode !=null && selectedNum == 0) {
            mActionMode.finish();
            mActionMode = null;
        } else if (mActionMode !=null && selectedNum > 0) {
            mActionMode.invalidate();
        }
    }
}
