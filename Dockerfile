FROM eclipse-temurin:21

ARG JAR_FILE=build/libs/goalpanzi-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} goalpanzi-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","-Duser.timezone=Asia/Seoul","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","/goalpanzi-0.0.1-SNAPSHOT.jar"]