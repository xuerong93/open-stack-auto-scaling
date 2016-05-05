#!/bin/bash
# Our submitter will call this bash scripts to launch an ASG

# IMPORTANT:
#   This scripts reads parameters from ./configure using the source command
#   Reference: https://bash.cyberciti.biz/guide/Source_command
#   DO NOT CHANGE THE FOLLOWING LINE
source ./configure
# IMPORTANT:
#   Shell scripts is not enough for implementing an ASG. What you need to do is
#   to write a Java/Python program and call it by this shell scripts. Make sure
#   all parameters specified in ./configure is passed to your program.

# IMPORTANT:
#   Run your program in background
UID1=`echo  $[ 1 + $[ RANDOM % 100000 ]]-$[ 1 + $[ RANDOM % 100000 ]]-$[ 1 + $[ RANDOM % 100000 ]]`
#   Call your program here
java -jar ASG/target/ASG-0.0.1-SNAPSHOT-fat.jar /opt/stack/devstack/configure $UID1 > /opt/stack/output  &

#sleep 7m
echo $UID1
