FROM openjdk:8-jre-alpine
EXPOSE 8080
WORKDIR /app
COPY target/customer-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT [ "java", "-jar", "customer-service-0.0.1-SNAPSHOT.jar" ]