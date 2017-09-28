FROM openjdk:8 as build

COPY src /usr/connectfourserver/src
WORKDIR /usr/connectfourserver

RUN javac -classpath src -d bin src/server/ConnectFourServer.java

FROM openjdk:8-jre

COPY --from=build /usr/connectfourserver/bin /app
WORKDIR /app

EXPOSE 8000
CMD ["java", "server/ConnectFourServer", "8000"]
