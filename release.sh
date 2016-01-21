#!/bin/bash

mvn release:clean
mvn release:prepare -DignoreSnapshots=true -Darguments="-DskipTests"

rtn=$?
if [ ${rtn} -eq 0 ]; then
  mvn release:perform
else
  echo
  echo '*** RELEASE HALTED (build failed) ***'
  echo
fi


