FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/quarkus-app/ /app/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
