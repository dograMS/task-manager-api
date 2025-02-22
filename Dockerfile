FROM openjdk:17-jdk-slim
WORKDIR /app
ADD ./target/todo*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
