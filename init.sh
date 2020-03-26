#!/bin/sh

cp libs/j*.jar C:/robocode/libs
echo "java -Xmx512M -DNOSECURITY=true -cp libs/robocode.jar;libs/jsr94-1.1.jar;libs/jruleenginesrc.jar;libs/jrules.jar robocode.Robocode %*" > C:/robocode/robocode.bat