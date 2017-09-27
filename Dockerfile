FROM openjdk:8

COPY src /src

RUN javac server/ConnectFourServer.java

EXPOSE 8000
CMD ["java", "server/ConnectFourServer", "8000"]
