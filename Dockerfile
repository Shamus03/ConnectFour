FROM openjdk:8 AS build

COPY . /build
WORKDIR /build

RUN mkdir -p bin
RUN javac -classpath src -d bin src/server/ConnectFourServer.java

FROM openjdk:8-jre

COPY --from=build /build/bin /app
WORKDIR /app

EXPOSE 8000
CMD ["java", "server/ConnectFourServer", "8000"]
