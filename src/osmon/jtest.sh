#!/bin/sh
# Author:  Steve Souza - http://www.jamonapi.com - (see jamon_readme.txt for more info)
# jtest.sh - shows sample usage of OS jamon.
# curl "http://localhost:8080/jamon/Mon?start=os.hello%20world&detail=sitting%20at%20starbucks"

jamontestprefix=os.

echo "1) Stopping most recent label passed to start (sleep 1 sec). Note calls are 'sourced'"
. jstart.sh ${jamontestprefix}mylabel
sleep 1
. jstop.sh 

echo 
echo 
echo "2) Stopping using explicit label (sleep 2 sec) (an explicit label doesn't require sourcing)"
jstart.sh ${jamontestprefix}mylabel
sleep 2
jstop.sh ${jamontestprefix}mylabel

echo 
echo 
echo "3) Stop timing monitor using explicit details (sleep 1 sec)"
jstart.sh ${jamontestprefix}mylabel "This is my more explict jamon label with context specific info"
sleep 1
jstop.sh ${jamontestprefix}mylabel

echo 
echo 
echo "4) Use label with white space "
. jstart.sh "${jamontestprefix} jamon testlabel with whitespace" "This is my more explict jamon label with context specific info"
# not doing anything this time
. jstop.sh 

echo 
echo 
echo "5) Call add several ways."
jadd.sh ${jamontestprefix}"backupsize" MB 127.5
jadd.sh ${jamontestprefix}"backupsize" MB 137.5
jadd.sh ${jamontestprefix}"backupsize" MB 150
jadd.sh ${jamontestprefix}"backupsize" MB 160 "with detail info like server name or time of day info"

echo 
echo 
echo "6) Call skip implicitly using last used start label."
. jstart.sh ${jamontestprefix}mylabel
sleep 1
. jskip.sh 

echo 
echo 
echo "7) Call skip with explicit label"
jstart.sh ${jamontestprefix}mylabel
sleep 1
jskip.sh  ${jamontestprefix}mylabel

echo 
echo 
echo "8) various calls to jgetmon"
jgetmon.sh ${jamontestprefix}mylabel ms. mon
jgetmon.sh ${jamontestprefix}mylabel ms. hits
jgetmon.sh ${jamontestprefix}mylabel ms. avg
jgetmon.sh ${jamontestprefix}mylabel ms. min
jgetmon.sh ${jamontestprefix}mylabel ms. max
jgetmon.sh ${jamontestprefix}mylabel ms. total
jgetmon.sh ${jamontestprefix}mylabel ms. active
jgetmon.sh ${jamontestprefix}mylabel ms. lastaccess
jgetmon.sh ${jamontestprefix}mylabel ms. badvalue

echo 
echo 
echo "9) various calls to jgetdata which return jamon reports."
. jgetdata.sh xml ${jamontestprefix} 
. jgetdata.sh csv ${jamontestprefix} 
# The following work too.  I commented them out as they both bringback a lot of data 
#. jgetdata.sh
#. jgetdata.sh html ${jamontestprefix} 

echo 
echo 
echo "10) The following all should return errors which always start with: Error"
jstart.sh
jstop.sh idonotexist
jskip.sh idonotexist
jgetmon.sh
jgetmon.sh idonnotexist
jgetmon.sh idonnotexist units
jadd.sh mylabel 
jadd.sh mylabel myunits 
jadd.sh mylabel myunits notnumber






