FROM gradle:8.13.0-jdk17-corretto AS build
WORKDIR /src
COPY . .
ARG PROJECT_VERSION
RUN ./gradlew build -Pproject.version=${PROJECT_VERSION}

FROM openjdk:17-jdk-slim AS publish
ARG DD_GIT_REPOSITORY_URL
ARG DD_GIT_COMMIT_SHA
ARG DD_VERSION
ENV DD_GIT_REPOSITORY_URL=${DD_GIT_REPOSITORY_URL}
ENV DD_GIT_COMMIT_SHA=${DD_GIT_COMMIT_SHA}
ENV DD_VERSION=${DD_VERSION}
ENV DD_ENV="prod"
ENV DD_SERVICE="curryware-java-api-server"

LABEL authors="scot.curry"
WORKDIR /app
EXPOSE 8080
COPY --from=build /src/build/libs/curryware-java-api-server.jar /app/curryware-java-api-server.jar

ENTRYPOINT ["java", "-jar", "/app/curryware-java-api-server.jar"]
