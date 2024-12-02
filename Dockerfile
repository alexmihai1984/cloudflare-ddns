FROM amazoncorretto:21

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-Xmx512M", "-jar" ,"/app.jar"]