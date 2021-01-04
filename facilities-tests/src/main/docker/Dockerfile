FROM vasdvp/health-apis-dev-tools:mvn-3.6-jdk-14

COPY maven/*sh /sentinel/
RUN chmod 755 /sentinel/*sh
COPY maven/facilities-tests-*-tests.jar /sentinel/
COPY maven/facilities-tests-*.jar /sentinel/
COPY maven/junit-platform-console-standalone.jar /sentinel/
ENTRYPOINT ["/sentinel/entrypoint.sh"]
CMD []
