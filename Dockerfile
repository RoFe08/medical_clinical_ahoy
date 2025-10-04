FROM maven:3.9.9-eclipse-temurin-17 AS build

COPY pom.xml ./

RUN --mount=type=cache,target=/root/.m2 mvn -B -q -e -DskipTests dependency:go-offline

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn -B -q -e clean package -DskipTests

FROM quay.io/wildfly/wildfly:37.0.0.Final-jdk17

ENV JBOSS_HOME=/opt/jboss/wildfly

COPY --from=build /app/target/medical-clinical-app.war \
${JBOSS_HOME}/standalone/deployments/ROOT.war

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=5 \
  CMD bash -lc 'exec 3<>/dev/tcp/127.0.0.1/8080 && echo -e "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n" >&3 && head -n1 <&3 | grep -q "HTTP/"'

CMD ["bash", "-lc", "${JBOSS_HOME}/bin/standalone.sh -b 0.0.0.0"]

