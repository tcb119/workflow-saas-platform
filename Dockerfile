FROM eclipse-temurin:17
COPY target/workflow-saas-platform-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]