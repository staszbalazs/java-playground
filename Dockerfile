#Download the given gradle version and the project dependencies
FROM openjdk:17 as cache
WORKDIR /usr/app/

COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle/

#gradlew uses the xargs command, not in openjdk-17 by default
RUN microdnf install findutils
RUN ./gradlew dependencies -i --stacktrace

#Build the project
FROM cache as build

COPY . .
RUN ./gradlew build --no-daemon -i --stacktrace

#Create the final version
FROM openjdk:17 as final
WORKDIR /usr/app/

COPY --from=build /usr/app/build/libs/*.jar .

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "spring-boot-application.jar"]