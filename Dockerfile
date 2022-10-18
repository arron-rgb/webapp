FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package


FROM gcr.io/distroless/java11-debian11
COPY --from=build /home/app/target/webapp-0.0.1-SNAPSHOT.jar /home/webapp.jar
WORKDIR /home/
CMD ["webapp.jar"]
