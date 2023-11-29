FROM maven:3-eclipse-temurin-17-focal  as build

RUN apt-get update && apt-get install -y nodejs

WORKDIR /workspace/app
COPY pom.xml pom.xml
RUN mvn -e -B dependency:resolve
COPY . .
RUN mvn -e -B clean install
RUN mvn -e -B clean package -Pproduction
FROM eclipse-temurin:17-jdk
COPY --from=build /workspace/app/target/*.jar /tmp/app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar -Dvaadin.productionMode /tmp/app.jar"]
