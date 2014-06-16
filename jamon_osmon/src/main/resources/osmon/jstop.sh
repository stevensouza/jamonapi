#!/bin/sh
# Author:  Steve Souza - http://www.jamonapi.com - (see jamon_readme.txt for more info)
# stop help: 
# curl "http://localhost:8080/jamon/Mon?skip=os.hello%20world"


jamoncommand=Mon?stop
jamonsstoplabel=
jamonerrormessage=


# if no arg value provided to script it is an error
if [ -n "${1}" ]
 then
   jamonsstoplabel=`jencode.sh "${1}"`
 elif [ -n "${jamonlabel}" ]
   then
    jamonsstoplabel=${jamonlabel}
 else
	jamonerrormessage="Error: Incorrect calling syntax.  A stop label must be provided: jstop.sh os.mylabel"
fi
 
# determine if wget or curl is installed.
httprequestprog=`httpprog.sh`

# if their is no program (i.e. value is null) then create an error message
if [ -z "${httprequestprog}" ]
 then
  jamonerrormessage="Error: wget or curl must be available."
 fi
 

# if the error message is empty then proceed
if [ -z "${jamonerrormessage}" ]
then

 # curl http://localhost:8080/jamon/Mon?stop=os.test
 executeprog=${httprequestprog}${jamoncommand}'='${jamonsstoplabel}

 # echo "command is: "${executeprog}
 ## execute command 
 ${executeprog}
else
 echo ${jamonerrormessage}
fi