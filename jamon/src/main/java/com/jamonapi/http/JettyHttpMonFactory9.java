package com.jamonapi.http;

/**
 * Factory used in the JAMonJettyHandler.  Usually this need not be used directly, but via JAMonJettyHandler.
 *
 */
public class JettyHttpMonFactory9 extends HttpMonFactory {

    public JettyHttpMonFactory9(String labelPrefix) {
        super(labelPrefix);
    }

    @Override
    HttpMonItem createHttpMonItem(String label) {
        return new JettyHttpMonItem9(label, this);
    }

}
