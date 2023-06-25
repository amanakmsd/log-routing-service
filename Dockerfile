FROM openjdk:11
EXPOSE 8080
WORKDIR /app
COPY target/log-routing-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT [ "java", "-jar", "log-routing-service-0.0.1-SNAPSHOT.jar" ]