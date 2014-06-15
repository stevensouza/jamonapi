#!/bin/sh
# Author:  Steve Souza - http://www.jamonapi.com - (see jamon_readme.txt for more info)
# jadd help: 
# curl "http://localhost:8080/jamon/Mon?add=os.helloworld&units=bytes&value=125.9"


# Change jamonserver to point to your jamon instance.
jamoncommand=Mon?add
jamonerrormessage=

# if no arg value provided to script it is an error
if  [ -z "$1" ] && [ -z "$2" ] && [ -z "$3" ]
 then
   jamonerrormessage="Error: Incorrect calling syntax.  You must provide a label, units, and an integer or float value. Optionally you can provide a detail value: add.sh os.mylabel myunits 20.5 mydetailsisoptional"
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
 jamonaddlabel=`jencode.sh "${1}"`
 jamonunits=${2}
 jamonvalue=${3} 
 jamondetail=
 if [ -n "${4}" ] 
   then
    jamondetail=`jencode.sh "${4}"` 
   fi

 # curl http://localhost:8080/jamon/Mon?add=os.test&units=bytes&value=100
 executeprog=${httprequestprog}${jamoncommand}'='${jamonaddlabel}'&units='${jamonunits}'&value='${jamonvalue}
 
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