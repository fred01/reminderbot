FROM openjdk:8 AS BUILD_IMAGE
ENV APP_HOME=/root/reminder
RUN mkdir -p $APP_HOME/
WORKDIR $APP_HOME
COPY . .
RUN ./gradlew -Dorg.gradle.daemon=false -Dkotlin.compiler.execution.strategy="in-process" bootJar
FROM openjdk:8-jre
WORKDIR /root/
COPY --from=BUILD_IMAGE /root/reminder/build/libs/reminder-0.0.1-SNAPSHOT.jar ./reminder.jar
EXPOSE 8080
CMD ["java","-jar","reminder.jar"]