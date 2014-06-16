# Author:  Steve Souza - http://www.jamonapi.com 

# Description: 
#   - This file contains a description of hte JAMon OS monitoring capabilities
#   - It includes scripts that allow you to make use of JAMon outside of the typical java environment and monitor your OS scripts and activities such as backups,
#     nightly processing etc.
#
# Requirements: 
#   - JAMon must be installed on a java web container.  
#   - You need the ability to access this web container from the OS (http calls)
#   - JAMon scripts should be installed on OS and this directory should be added to your path. 
#   - Change the following line in httpprog.sh to point to your server:  jamonserver=http://localhost:8080/jamon/
#   - curl or wget must be installed on the OS.
#   - sed must be installed on the OS  
# 
# The scripts in this directory are part of JAMon and allows scripts to take advantage of jamon to track statistics.  
# This is called OS Monitoring. You need to have a running java web container with JAMon installed.  See the jamon web site for 
# more info.  The basic concept of JAMon is take a primary key string and every time this is called the past in statistics will 
# be aggregated and available both from the jamonadmin.jsp web page or via the jgetdata and jgetmon scripts.  Note because this primary key
# must be relatively unique.  For example don't attach a timestamp to it (os.server backup: 7/24/11 4:00:04). You must also have 
# curl or wget installed on your computer and the ability to connect to the web server that has JAMon installed to use OS Monitoring. 
#
# Notes: 
#  - Although not a requirement it is best to prefix all OS monitoring labels with an easily recognizable string.  For example 'os.'.
#  - All scripts the user executes have the format j*.sh.  The 'j' stands for JAMon.
#  - If an error occurs in the servlet call then the returned output will begin with: Error
#  - Run jtest.sh to see an example of how JAMon OS monitoring works.  Also look in http://host:port/jamon to see the data.
#
# Once this is done you can call the following scripts:
#  jstart.sh - Tracks time between calls to jstart.sh and jstop.sh that use the same label.  This works like a stopwatch.  Note if you want to run jstop.sh or jskip.sh
#   without passing explicity lables you need to 'source' jstart.sh (with 'source' or '.'.  jtest.sh has examples of this.) Examples:
#     - Basic call: . jstart.sh mylabel 
#     - Basic call with details and whitespace: . jstart.sh os.nightlyprocessng "This is my detail information that contains some context of interest"
#
#  jstop.sh - Looks for the most recent timer started with the same label passed into jstart and stops the timer. If an argument is not provided then the
#    last value passed to jstart.sh is used if jstart.sh and jstop.sh are sourced. Examples:
#     - Default label to last passed to jstart.sh: . jstop.sh
#     - Basic call: jstop.sh mylabel
#     - Basic call with whitespace: jstop.sh "os.my longer label"
#
#  jskip.sh - Looks for the most recent timer started with the same label passed into jstart, but unlike jstop which saves the information jskip aborts the call and does not 
#    save the time.  This might be done if something happens in the process you are timing such as an error that makes you not want to save the time.  The syntax of the call is
#    the same as jstop. Examples:
#     - Default label to last passed to jstart.sh : . jskip.sh
#     - Basic call: jskip.sh mylabel
#     - Basic call with whitespace: jskip.sh "os.my longer label"
#
#  jadd.sh - Tracks an arbitrary number such as number of errors, bytes sent down a pipe or the like. You provide the summary label, a units label, 
#    a number and optionally a detail string that can be viewed within the jamonadmin page.  Look on the website about JAMon Listeners for more info
#    on 'details'.  Examples:
#     - Basic call:  jadd.sh mylabel myunits 20.5 
#     - Basic counter (add 1 each time): jadd.sh os.backup.errors error 1 
#     - Basic counter (add files processed each time): jadd.sh os.backup.filesprocessed count 155000 
#     - Use quotes when the label has white space in it: jadd.sh "os.my souza label" myunits 20.5 
#     - Passing in details with white space: jadd.sh "os.my souza label" myunits 20.5 "my detail description.  Put in any context details that are interesting.  This need not be unique"
#     - Passing in details with white space: jadd.sh "os.my souza label" myunits 20.5 "my detail description.  Put in any context details that are interesting.  This need not be unique"
#
#  jgetdata.sh - Retrieve a report of JAMon statistics in various formats such as xml, csv, html. This simply calls jamonadmin.jsp and so you can look at that web page to get a sense
#    of capabilities.  Examples:
#     - Defaults to xml: jgetdata.sh
#     - Can use xml, csv, or html: jgetdata.sh csv
#     - You can piple to egrep or use the ArraySQL argument to filter.  This example shows egrep:  jgetdata.sh csv | egrep "os."
#     - The report returns with extra lines at beginning and end of report.  To clean them up do this:  jgetdata.sh | sed -e '/^$/d'
#
#  jgetmon.sh - Pull back values in an individual monitor.  Valid values are mon (returns a string of monitor details), hits, avg, min, max, stddev, lastvalue, lastaccess, 
#   firstaccess, active, maxactive and more.  This can be used to get values from JAMon to drive what your scripts do next.  For example if your nightly process only took half the time
#   it normally does you could perform other processing.  Examples:
#     - Defaults to mon:  jgetmon.sh os.mylabel
#     - jgetmon.sh os.mylabel avg
#     - jgetmon.sh "os.this is my label" max
#
#  jreset.sh - Clears JAMon stats by passing 'reset' to jamonadmin.jsp page.