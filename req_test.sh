#!/bin/bash
sleep 2
count=0
for j in {1..5} ; do
  for i in {1..1000} ; do
      curl --location 'localhost:8080/log' \
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