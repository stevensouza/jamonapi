package com.jamonapi.aop.spring;

/**
 * This tag annotation is intended to be used on Spring classes and/or methods that should be monitored. If the class
 * is annotated then all methods would be available for monitoring.  Alternatively the class need not be annotated
 * and individual methods could be.  You would still have to specify to spring that the JamonAspect would need to monitor
 * classes with this annotation.  See the applicationContext.xml in the testing code for some examples (which are
 * commented out).
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface  MonitorAnnotation {

}
