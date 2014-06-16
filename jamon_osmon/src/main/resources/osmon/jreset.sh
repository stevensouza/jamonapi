#!/bin/sh
# Author:  Steve Souza - http://www.jamonapi.com - (see jamon_readme.txt for more info)
# start help: 
# curl "http://localhost:8080/jamon/jamonadmin.jsp?outputTypeValue=xml&ArraySQL=hello%20world"
# Types for output are currently: html, xml (default if not provided), csv.
# ArraySQL lets you put in a string or query to filter by.
# note to remove empty rows from output simply pipe this script in the following manner:  getdata.sh |  sed -e '/^$/d'
# or getdata.sh csv myfilter | sed -e '/^$/d'

# Change jamonserver to point to your jamon instance.
jamoncommand=jamonadmin.jsp?action=Reset
jamondatatype=
jamonerrormessage=
 

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

 # curl "http://localhost:8080/jamon/jamonadmin.jsp?action=Reset"
 executeprog=${httprequestprog}${jamoncommand}


 ## echo "command is: "${executeprog}
 ## execute command 
 ${executeprog}
else
 echo ${jamonerrormessage}
fi