FROM maven:3-openjdk-17-slim as build
WORKDIR /opt/app
COPY . .
RUN mvn clean package -DskipTests=true

FROM openjdk:17-alpine
WORKDIR /opt/app
COPY --from=build /opt/app/target/discord-bot.jar /opt/app
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "discord-bot.jar" ]