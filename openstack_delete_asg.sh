#!/usr/bin/env bash

# This bash script should kill your ASG running in the background.
echo $1 | nc 127.0.0.1 8888
