FROM openjdk

COPY src /src

javac server/ConnectFourServer.java

EXPOSE 8000
CMD ["java", "server/ConnectFourServer", "8000"]
