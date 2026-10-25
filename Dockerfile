FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY gradlew build.gradle settings.gradle gradle.properties ./
COPY gradle/ gradle/
COPY src/main/ src/main/
RUN sed -i 's/\r$//' gradlew \
    && sh ./gradlew --no-daemon bootJar
RUN cp build/libs/*.jar app.jar \
    && java -Djarmode=tools -jar app.jar extract --layers --destination extracted

FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app \
    && chown app:app /app
COPY --from=build --chown=app:app /workspace/extracted/dependencies/ ./
COPY --from=build --chown=app:app /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=app:app /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=app:app /workspace/extracted/application/ ./

USER app
# Train with the runtime JVM and the same paths used at startup.
RUN java -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh -jar app.jar

EXPOSE 8080 8081
ENTRYPOINT ["java", "-XX:AOTCache=app.aot", "-jar", "app.jar"]
