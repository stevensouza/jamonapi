package com.jamonapi.http;

/**
 * Factory used in the JAMonJettyHandler.  Usually this need not be used directly, but via JAMonJettyHandler.
 *
 */
public class JettyHttpMonFactoryNew extends HttpMonFactory {

    public JettyHttpMonFactoryNew(String labelPrefix) {
        super(labelPrefix);
    }

    @Override
    HttpMonItem createHttpMonItem(String label) {
        return new JettyHttpMonItemNew(label, this);
    }

}
