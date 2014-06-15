package com.jamonapi.aop;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;
/** Class for monitoring ejb's via aop */
public class JAMonEJBInterceptor {
    private static final String EXCEPTION_STR="JAMonEJBInterceptor.EJBException";
    private static final int EXCEPTION=1;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        Object[] details=null;
        Monitor mon=null;
        String label=null;

        try {
            label=new StringBuffer("JAMonEJBInterceptor: ").append(ctx.getMethod()).toString();
            details=new Object[] {label, ""};
            mon=MonitorFactory.start(new MonKeyImp(label, details, "ms."));
            return ctx.proceed();
        } catch (Exception e) {
            details[EXCEPTION]=Misc.getExceptionTrace(e);
            MonitorFactory.add(new MonKeyImp(EXCEPTION_STR, details, "Exception"), 1);
            MonitorFactory.add(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, details, "Exception"), 1);
            throw e;
        } finally {
            mon.stop();
        }
    }

}
