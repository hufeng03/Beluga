package com.hufeng.filemanager;

import com.squareup.otto.Bus;

/**
 * Created by feng on 2014-06-26.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}