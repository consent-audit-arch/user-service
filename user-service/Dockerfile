FROM eclipse-temurin:21-jre-jammy
RUN groupadd -r appuser && useradd -r -g appuser -m appuser
WORKDIR /app
COPY user-service/user-service/target/*.jar app.jar
COPY user-service/user-service/opa/policies /opa/policies
COPY user-service/user-service/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
RUN chown appuser:appuser app.jar /entrypoint.sh
EXPOSE 8080
ENV JAVA_OPTS=
ENTRYPOINT ["/entrypoint.sh"]
