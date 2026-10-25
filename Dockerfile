FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY gradlew build.gradle settings.gradle gradle.properties ./
COPY gradle/ gradle/
COPY src/main/ src/main/
RUN sed -i 's/\r$//' gradlew \
    && sh ./gradlew --no-daemon bootJar

FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app
COPY --from=build --chown=app:app /workspace/build/libs/*.jar app.jar

USER app
EXPOSE 8080 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
