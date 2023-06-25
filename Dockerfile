WORKDIR /app
#/FROM maven:3.6.3-jdk-11-slim as builder

# Copy local code to the container image.
COPY pom.xml .
COPY src ./src
#COPY etc ./etc
EXPOSE 8080

# Build a release artifact.
RUN mvn clean install

COPY target/log-routing-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT [ "java", "-jar", "log-routing-service-0.0.1-SNAPSHOT.jar" ]