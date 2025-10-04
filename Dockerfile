FROM maven:3.9.9-eclipse-temurin-17 AS build

COPY pom.xml ./

RUN --mount=type=cache,target=/root/.m2 mvn -B -q -e -DskipTests dependency:go-offline

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn -B -q -e clean package -DskipTests

FROM quay.io/wildfly/wildfly:37.0.0.Final-jdk17

ENV JBOSS_HOME=/opt/jboss/wildfly

USER root

RUN microdnf install -y curl && microdnf clean all

USER jboss

COPY --from=build /app/target/medical-clinical-app.war \
${JBOSS_HOME}/standalone/deployments/ROOT.war

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=5 \
 CMD curl -fsS http://localhost:8080/ || exit 1

CMD ["bash", "-lc", "${JBOSS_HOME}/bin/standalone.sh -b 0.0.0.0"]

