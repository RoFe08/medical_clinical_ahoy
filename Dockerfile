# versao do wildfly
FROM quay.io/wildfly/wildfly:37.0.0.Final-jdk17

# Copia o WAR para a pasta de deployments do WildFly
COPY target/medical-clinical-app.war /opt/jboss/wildfly/standalone/deployments/medical-clinical-app.war

#porta
EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]