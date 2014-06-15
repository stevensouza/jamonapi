#!/bin/bash
# Author:  Steve Souza - http://www.jamonapi.com - (see jamon_readme.txt for more info)
# jendode help: jencode.sh "my long string to encode"
#  The check below finds the sed file even if the script is not executed from the directory where the script exists.
#
# Note if you don't want to run this script as bash or have an older version of bash that doesn't properly set jamondir
# you can simply run it is an sh script and hardcode the directory where the jamon OS mon scripts are located (minus the trailing '\'
#  for example: /myscripts/osmon ) 
jamondir=`dirname ${BASH_SOURCE[0]}`
###jamonencode=`httpprog.sh`

# wget already encodes
# else if current directory then no need for path
###if [ "${jamonencode:0:4}" = "wget" ]
### then
###   echo "$1"
###elif [ "${jamondir}" = "." ]
if [ "${jamondir}" = "." ]
 then
   echo "$1" |  sed -f urlencode.sed
else
   echo "$1" |  sed -f $jamondir/urlencode.sed
fi
