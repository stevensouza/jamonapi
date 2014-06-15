#!/bin/bash
# Author:  Steve Souza http://www.jamonapi.com
# httpprog help: returns curl, wget or empty string if there is neither
# Note wget encodes a url by default whereas curl does not so they are treated differently


# Change jamonserver to point to your jamon instance.
jamonserver=http://localhost:8080/jamon/


# determine if wget or curl is installed.
httprequestprog=`which wget`
# if wget was found then use it else if curl is found use it.
if [ -n "$httprequestprog" ] 
then 
# -q = quiet  -  needed because otherwise wget spews out a whole lot including a progressbar.
# -O - = Send output to stdout - needed because you want the file output directly (in stead of in a downloaded file).
 httprequestprog="wget -q -O - "
else
 httprequestprog=`which curl`
 if [ -n "${httprequestprog}" ] 
  then 
   httprequestprog=curl
 fi
fi

# if there is a program echo the command for jamon else echo nothing.
if [ -n "$httprequestprog" ]
 then
  echo  ${httprequestprog}" "${jamonserver}
 fi

