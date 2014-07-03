package com.jamonapi.http;

/**
 * Factory used in the JAMonJettyHandler.  Usually this need not be used directly, but via JAMonJettyHandler.
 *
 */
public class JettyHttpMonFactory extends HttpMonFactory {

    private static final long serialVersionUID = -728341901466268550L;

    public JettyHttpMonFactory(String labelPrefix) {
        super(labelPrefix);
    }

    @Override
    HttpMonItem createHttpMonItem(String label) {
        return new JettyHttpMonItem(label, this);
    }

}
