#!/bin/sh

docker stop $(docker ps | grep tomee-jac | tail -n 1 | awk '{ print $1 }')
docker run -dit \
	-e JPDA_ADDRESS=8000 \
 	-e JPDA_TRANSPORT=dt_socket \
 	-p 8080:8080 \
 	-p 8000:8000 \
	tomee-jac
