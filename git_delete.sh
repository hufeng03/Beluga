#!/bin/bash
#usage: git status | ./git_delete.sh
export BY="by"
export DELETE="deleted:"
echo $DELETE
while read line1 line2 line3
do
if [ "$line2" = $DELETE ]; then
	echo `git rm  $line3`
fi
done
