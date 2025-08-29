#!/bin/sh
cp jamon/target/jamon-2.85.jar /Applications/myapps/jetty/jetty-distribution-9.2.6.v20141205/lib/jamon/
cp jamon/target/jamon-2.85.jar /Applications/myapps/jetty/jetty-distribution-9.2.1.v20140609/lib/ext/.
cp jamon/target/jamon-2.85.jar /Applications/myapps/vagrant/dev1/jetty-distribution-9.2.1.v20140609/lib/ext/.
cp jamon/target/jamon-2.85.jar /Applications/myapps/tomcat/apache-tomcat-8.0.8/lib/.
cp jamon/target/jamon-2.85.jar /Applications/myapps/tomcat/apache-tomcat-7.0.54/lib/.

cp jamon_war/target/jamon.war /Applications/myapps/jetty/jetty-distribution-9.2.6.v20141205/lib/jamon/jamon.war
cp jamon_war/target/jamon.war /Applications/myapps/jetty/jetty-distribution-9.2.1.v20140609/webapps/.
cp jamon_war/target/jamon.war /Applications/myapps/vagrant/dev1/jetty-distribution-9.2.1.v20140609/webapps/.
cp jamon_war/target/jamon.war /Applications/myapps/tomcat/apache-tomcat-8.0.8/webapps/.
cp jamon_war/target/jamon.war /Applications/myapps/tomcat/apache-tomcat-7.0.54/webapps/.
