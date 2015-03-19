package com.belugamobile.filemanager;

import com.hufeng.MainThreadBus;
import com.squareup.otto.Bus;

/**
 * Created by feng on 2014-06-26.
 */
public final class BusProvider {
    private static final Bus BUS = new MainThreadBus(new Bus());

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
