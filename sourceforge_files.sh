#!/bin/sh
mkdir /Users/stevesouza/Documents/sourceforge/jamonrelease/jamonall-2.85/
cp ./jamon/target/*.jar /Users/stevesouza/Documents/sourceforge/jamonrelease/jamonall-2.85/.
cp ./jamon_osmon/target/*.jar  /Users/stevesouza/Documents/sourceforge/jamonrelease/jamonall-2.85/.
cp ./jamon_war/target/*.war  /Users/stevesouza/Documents/sourceforge/jamonrelease/jamonall-2.85/.
# note only the destination file name has to change.
cp /Users/stevesouza/Documents/sourceforge/jamonrelease/jamonall-2.82/jamontomcat-2.82.jar /Users/stevesouza/Documents/sourceforge/jamonrelease/jamonall-2.85/jamontomcat-2.85.jar
