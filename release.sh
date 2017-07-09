#!/bin/bash

mvn release:clean
mvn release:prepare -DignoreSnapshots=true -Darguments="-DskipTests"

rtn=$?
if [ ${rtn} -eq 0 ]; then
    mvn release:perform -Darguments="-DskipTests"
else
    echo
    echo '*** RELEASE FAILED, ROLLING BACK ***'
    echo
    mvn release:rollback
fi



