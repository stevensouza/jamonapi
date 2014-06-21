package com.jamonapi.aop.spring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * <p>It is good to have a class that has common aop pointcuts for your application.  These can be reused and built
 * upon elsewhere. Note cglib requires a noarg constructor.  It can be private.  You would also have to
 * have setters for any of the values that the actual constructor (say a 2 arg constructor takes).
 * These can also be private.</p>
 *
 * Created by stevesouza on 5/26/14.
 */
@Aspect
public class SystemAopPointcutDefinitions {

    /** Pointcut that would monitor any public methods */
    @Pointcut("execution(public * *(..))")
    public void anyPublicMethod() {}

    /** Some useful pointcut tips...
     *   com.stevesouza..* - .. means stevesouza and any of its subpackages.
     *   com.stevesouza.*  - means only in com.stevesouza and none of its subpackages
     * combining them expression="execution(* package1.*.*(..)) || execution(* package2.*.*(..))"
     */

    /** The pointcut below will find all methods in all types marked with @MonitorAnnotation */
    @Pointcut("within(@com.jamonapi.aop.spring.MonitorAnnotation *)")
    public void monitorAnnotatedClass() {}

    /** The pointcut below will find all methods marked with @MonitorAnnotation
     */
    @Pointcut("anyPublicMethod() && @annotation(com.jamonapi.aop.spring.MonitorAnnotation)")
    public void monitorAnnotatedMethod() {}
}
