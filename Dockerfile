FROM ubuntu:22.04 AS build

RUN apt-get update && apt-get install openjdk-17-jdk -y

COPY . .

RUN apt-get install maven -y && mvn clean install

FROM openjdk:17-jdk-alpine

EXPOSE 8080

COPY --from=build /target/todolist-1.0.0.jar app.jar

ENTRYPOINT java -jar app.jar