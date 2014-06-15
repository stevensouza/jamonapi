#!/bin/sh
# Author:  Steve Souza - http://www.jamonapi.com - (see jamon_readme.txt for more info)
# start help: 
# curl "http://localhost:8080/jamon/Mon?get=mylabel&units=myunits&type=avg"
# for time monitors you must provide the units as follows:  (note mon returns a string with all values of the monitor)
# curl "http://localhost:8080/jamon/Mon?get=mylabel&units=ms.&type=mon"



# Change jamonserver to point to your jamon instance.
jamoncommand=Mon?get
jamonerrormessage=

# if no arg value provided to script it is an error
if  [ -z "$1" ] && [ -z "$2" ] && [ -z "$3" ]
 then
   jamonerrormessage="Error: Incorrect calling syntax.  You must provide a label, units, and a type (mon, avg, total, min, max, value, lastaccess, firstaccess, stddev, active). "
 fi
 

# determine if wget or curl is installed.
httprequestprog=`httpprog.sh`

# if their is no program (i.e. value is null) then create an error message
if [ -z "$httprequestprog" ]
 then
  jamonerrormessage="Error: wget or curl must be available."
 fi


# if the error message is empty then proceed
if [ -z "$jamonerrormessage" ]
then
 jamongetlabel=`jencode.sh "${1}"`
 jamonunits=${2}
 jamongettype=${3} 

 # curl http://localhost:8080/jamon/Mon?add=os.test&units=bytes&value=100
 executeprog=${httprequestprog}${jamoncommand}'='${jamongetlabel}'&units='${jamonunits}'&type='${jamongettype}

 ## execute command 
 ${executeprog}
else
 echo ${jamonerrormessage}
fi