FROM eclipse-temurin:21

ARG JAR_FILE=build/libs/goalpanzi-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} goalpanzi-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","-Duser.timezone=KST","/goalpanzi-0.0.1-SNAPSHOT.jar"]