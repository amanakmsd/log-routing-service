#!/bin/bash
sleep 2
count=0
while true ; do
  for i in {1..1000} ; do
      curl --location 'http://log-routing-service-spring-app-1:8080/log' \
      --header 'Content-Type: application/json' \
      --data '{
          "eventName" : "user.login",
          "timeStamp" : 123456,
          "source" : "user",
          "sourceId" : "user1234",
          "logMessage" : "User logged successfully"
      }' &
      ((count=count+1))
      echo "Count $count"
  done
  sleep 1
done