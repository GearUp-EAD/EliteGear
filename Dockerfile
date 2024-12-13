FROM eclipse-temurin:20
LABEL maintainer="chamika31.me"
ADD target/EliteGear-0.0.1-SNAPSHOT.jar elite-gear.jar
ENTRYPOINT ["java","-jar","elite-gear.jar"]