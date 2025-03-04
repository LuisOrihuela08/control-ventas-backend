FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
EXPOSE 8080
COPY ./target/control-ventas-backend-0.0.1-SNAPSHOT.jar control-ventas.jar

ENTRYPOINT [ "java", "-jar", "control-ventas.jar"]