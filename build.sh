#!/bin/sh

rm -f src/adrobot/*.class
javac -cp "libs/robocode.jar;libs/jsr94-1.1.jar;libs/jruleenginesrc.jar;libs/jrules.jar;" src/adrobot/Turn.java src/adrobot/ADRobot.java
cp -r src/adrobot C:/robocode/robots
