#!/usr/bin/bash

clear

STAGE="test-postgres"

# mvn -P"$STAGE" -B -X -e -Dmaven.test.skip=true -DskipTests clean install -Dstyle.color=always
mvn -P"$STAGE" -B -Dmaven.test.skip=true -DskipTests clean install -Dstyle.color=always
cp -f ../bttf-app/target/bttf.war bttf.war
mvn -B -Dmaven.test.skip=true -DskipTests clean -Dstyle.color=always
