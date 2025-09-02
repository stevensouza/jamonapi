package com.jamonapi.aop.spring;

import java.io.FileNotFoundException;

/**
 * Created by stevesouza on 6/16/14.
 */
public class MonitorMe {

   public void anotherMethodForMe() throws InterruptedException {
       Thread.sleep(10);
   }

   public void anotherMethod(String fileName) throws FileNotFoundException {
        throw new FileNotFoundException("file not found");
   }

   public void helloWorld() {

   }

}
