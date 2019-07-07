#!/bin/bash

docker exec -it $(docker ps | grep tomee-jac | tail -n 1 | awk '{ print $1 }') sh
