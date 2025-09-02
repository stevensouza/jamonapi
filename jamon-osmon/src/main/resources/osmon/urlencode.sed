# Author:  Steve Souza http://www.jamonapi.com
# sed script that encodes a string.  For example if passed: 'hello world ' it would return 'hello%20world%20'. The /g replaces
# all occurences.  This code was taken from a unix forum by user V310 at the following link (Thanks!):
#  http://www.unix.com/shell-programming-scripting/59936-url-encoding.html
# See this link for a list of characters that must be enoded: http://www.blooberry.com/indexdot/html/topics/urlencoding.htm
#  Note '%' must go first as it is in every replacement string that follows

s/%/%25/g
s/ /%20/g
s/ /%09/g
s/!/%21/g
s/"/%22/g
s/#/%23/g
s/\$/%24/g
s/\&/%26/g
s/'\''/%27/g
s/(/%28/g
s/)/%29/g
s/\*/%2a/g
s/+/%2b/g
s/,/%2c/g
s/-/%2d/g
s/\./%2e/g
s/\//%2f/g
s/:/%3a/g
s/;/%3b/g
s//%3e/g
s/?/%3f/g
s/@/%40/g
s/\[/%5b/g
s/\\/%5c/g
s/\]/%5d/g
s/\^/%5e/g
s/_/%5f/g
s/`/%60/g
s/{/%7b/g
s/|/%7c/g
s/}/%7d/g
s/~/%7e/g