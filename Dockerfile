FROM gradle:7.4-jdk-alpine as builder
LABEL authors="woowatech25"
WORKDIR /target

COPY . /target
RUN gradle build

FROM openjdk:17.0-slim
WORKDIR /app

COPY --from=builder /target/build/libs/*.jar .

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar *.jar"]
