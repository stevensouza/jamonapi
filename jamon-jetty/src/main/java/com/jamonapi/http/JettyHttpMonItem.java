package com.jamonapi.http;


import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.eclipse.jetty.server.Request;

/**
 *  Used to monitor jetty requests via the JAMonJettyHandler.
 */
class JettyHttpMonItem extends HttpMonItem {

    JettyHttpMonItem() {
    }

    JettyHttpMonItem(String label, HttpMonFactory httpMonFactory) {
        super(label, httpMonFactory);
    }


    /**
     * Note.  I am no longer sure that the following statements still hold true for jetty. 2/15/15
     *
     * Jetty Handlers does not let jamon start/stop time them.  It seems the request is done by the time jamon gets it.
     * To overcome this use the jetty api to get the time of a request for a page.  If it isn't a jetty request then call
     * the parent.  Note although start is called because the timing for jetty is done after the request is finished
     * 'active' statistics will not be accurate.
     */
    @Override
    // Only called if this is a time monitor i.e units are 'ms.'
    Monitor startTimeMon(HttpMonRequest httpMonBase) {
        if (httpMonBase.getRequest() instanceof Request)
            return MonitorFactory.getMonitor(getMonKey(httpMonBase)).start();
        else
            return super.startTimeMon(httpMonBase);
    }

    // Only called if this is a time monitor i.e units are 'ms.'
    @Override
    void stopTimeMon(HttpMonRequest httpMonBase) {
        if (httpMonBase.getRequest() instanceof Request)  {
            Request request=(Request)httpMonBase.getRequest();
            Monitor mon=httpMonBase.getNextTimeMon();
            if (mon!=null) {
                long startTime = request.getBeginNanoTime() / 1_000_000; // Convert nanoseconds to milliseconds
                mon.add(System.currentTimeMillis()-startTime).stop();// figure elapsed time and then decrement active.
            }
        } else
            super.stopTimeMon(httpMonBase);
    }

}
