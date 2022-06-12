FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9000
ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
ENTRYPOINT ["java","-jar","app.jar"]