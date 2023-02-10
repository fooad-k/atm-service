FROM openjdk:17-alpine
WORKDIR /app/
COPY /target/*-SNAPSHOT.jar ./atm-service.jar
EXPOSE 8080
CMD ["java","-jar","atm-service.jar"]


