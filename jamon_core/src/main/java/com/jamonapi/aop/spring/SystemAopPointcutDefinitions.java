package com.jamonapi.aop.spring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * It is good to have a class that has common aop pointcuts for your application.  These can be reused and built
 * upon elsewhere.
 *
 * note cglib requires a noarg constructor.  It can be private.  You would also have to have setters for any of the
 * values that the actual constructor (say a 2 arg constructor takes).  These can also be private.
 *
 * Created by stevesouza on 5/26/14.
 */
@Aspect
public class SystemAopPointcutDefinitions {

    @Pointcut("execution(public * *(..))")
    public void anyPublicMethod() {}

    // com.stevesouza..* - .. means stevesouza and any of its subpackages.
    // com.stevesouza.*  - means only in com.stevesouza and none of its subpackages
    // combining them expression="execution(* package1.*.*(..)) || execution(* package2.*.*(..))"

    // The pointcut below will find all methods in all types marked with @MonitorAnnotation
    @Pointcut("within(@com.jamonapi.aop.spring.MonitorAnnotation *)")
    public void monitorAnnotatedClass() {}

    // The pointcut below will find all methods marked in with @MonitorAnnotation regardless of whether or not
    // class is marked that way or not.
    @Pointcut("anyPublicMethod() && @annotation(com.jamonapi.aop.spring.MonitorAnnotation)")
    public void monitorAnnotatedMethod() {}
}
