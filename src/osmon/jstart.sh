#!/bin/sh
# Author:  Steve Souza - http://www.jamonapi.com - (see jamon_readme.txt for more info)
# start help: 
# curl "http://localhost:8080/jamon/Mon?start=os.hello%20world&detail=sitting%20at%20starbucks"


# Change jamonserver to point to your jamon instance.
jamoncommand=Mon?start
jamonerrormessage=

# if no arg value provided to script it is an error
if [ -z "$1" ]
 then
   jamonerrormessage="Error: Incorrect calling syntax.  You must provide a label: strt os.mylabel"
 fi
 

# determine if wget or curl is installed.
httprequestprog=`httpprog.sh`

# if their is no program (i.e. value is null) then create an error message
if [ -z "$httprequestprog" ]
 then
  jamonerrormessage="Error: wget or curl must be available."
 fi


# if the error message is empty then procee
if [ -z "$jamonerrormessage" ]
then
 jamonlabel=`jencode.sh "${1}"`
 jamondetail=`jencode.sh "${2}"`

 # curl http://localhost:8080/jamon/Mon?start=os.test
 executeprog=${httprequestprog}${jamoncommand}'='${jamonlabel}

 if [ -n "${jamondetail}" ] 
 then 
  # curl http://localhost:8080/jamon/Mon?start=os.test&detail=heysteve
  executeprog=${executeprog}'&detail='${jamondetail}
 fi

 ## echo "command is: "${executeprog}
 ## execute command 
 ${executeprog}
else
 echo ${jamonerrormessage}
fi