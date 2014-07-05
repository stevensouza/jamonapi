
package com.jamonapi;

import java.io.Serializable;

/**
 * Class used to track the number of active monitors (including global/primary/this).
 * It allows you to see how many monitors are concurrently running at any given time.
 *
 */

final class ActivityStats implements Serializable {

    private static final long serialVersionUID = 7556315032740339165L;

    final Counter allActive;  // the number of monitors that are now running
    final Counter primaryActive;  // the number of monitors marked primary that are now running
    final Counter thisActive; // the number of monitors of this type that are running

    /** Creates a new instance of Counters */
    ActivityStats(Counter thisActive, Counter primaryActive, Counter allActive) {
        this.thisActive=thisActive;
        this.primaryActive=primaryActive;
        this.allActive=allActive;
    }

    ActivityStats() {
        this(new Counter(),new Counter(),new Counter());
    }

    /** The number of all active monitors running */
    public double getGlobalActive() {
        return allActive.getCount();
    }

    /** The number of primary monitors running */
    public double getPrimaryActive() {
        return primaryActive.getCount();
    }

    /** The number monitors of this type that are running */
    public double getActive() {
        return thisActive.getCount();
    }

}
