FROM tomcat:10.1-jdk17-temurin

# Quitar las aplicaciones de ejemplo de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Render usa el puerto 10000 por defecto. Tomcat escuchara en ese puerto.
RUN sed -i 's/port="8080"/port="10000"/' /usr/local/tomcat/conf/server.xml

# Limitar memoria para el plan Free de Render (512 MB)
ENV CATALINA_OPTS="-Xms32m -Xmx256m -XX:MaxMetaspaceSize=96m -XX:+UseSerialGC -Dfile.encoding=UTF-8"

# La aplicacion se publica en la raiz del dominio
COPY dist/API_HealthBlood_512.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 10000

CMD ["catalina.sh", "run"]
