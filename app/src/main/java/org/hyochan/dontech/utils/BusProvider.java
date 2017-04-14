package org.hyochan.dontech.utils;

import com.squareup.otto.Bus;

/**
 * Created by hyochan on 2016. 10. 7..
 */

public final class BusProvider {

    private static final Bus BUS = new Bus();

    private BusProvider() {
        // No instances.
    }

    /**
     * Lazy load the event bus
     */
    public static synchronized Bus getInstance()
    {
        return BUS;
    }
}