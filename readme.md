# Log-Routing-Service

Log-Routing-Service is used to log request to database



## Steps to run the service (docker)
1. Run ```docker-compose build```
2. Run service ```docker-compose up```
3. Run tests : uncomment test service in docker compose and run  ```docker-compose --build test```

## System diagram:
![Alt text](doc/log-routing-service.png?raw=true "Log-Routing-Service")


### API Curl for reference:
```
curl --location 'localhost:8080/log' \
--header 'Content-Type: application/json' \
--data '{
    "eventName" : "user.login",
    "timeStamp" : 123456,
    "source" : "user",
    "sourceId" : "user1234",
    "logMessage" : "User logged successfully"
}'
```

## Note:
Bunch of things can be improved in this service:
1. Logging
2. Using JPA
3. Using Kinesis/SQS for retry or buffer
etc...