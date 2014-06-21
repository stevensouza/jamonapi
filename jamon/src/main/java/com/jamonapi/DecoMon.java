
package com.jamonapi;

/**
 * This class is a wrapper for the internal data structure of a Monitor. It adds info specific to this monitor
 * too such as the details of the key.  However, it uses the data of the underlying monitor (such as hits/total/avg etc).
 * DecoMon stood for decorator though I suspec that this is no longer the decorator pattern.  It seems to be a useful
 * pattern of passing around hte internals and merging them with another classes differences though I am not sure what that
 * pattern is (similar to flyweight but with different intent).
 *
 * @author  ssouza
 * 
 */

class DecoMon extends MonitorImp {

    // The two following fields are unique to this instance.  MonInternals are shared
    // by all monitors with this same logical key.
    private MonKey decoKey;

    /** Creates a new instance of BaseMon.  It takes a reference to the enabled monitor it calls */
    public DecoMon(MonInternals monData) {
        super(monData);

    }

    public DecoMon(MonKey key, MonInternals monData) {
        super(monData);
        this.decoKey=key;
    }

    /** Get the key for this monitor.  Example:  MonKey could contain "pageHits", "ms." */
    @Override
    public MonKey getMonKey() {
        if (decoKey!=null)
            return decoKey;
        else
            return monData.key;
    }

}
