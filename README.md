# lighthouse-facilities

A [Spring Boot](https://spring.io/projects/spring-boot) application
that provides information about physical VA facilities, including
location, address, phone, hours of operation, and available services.

## Local Development

- `local-db/local-db.sh` launches a SQL Server instance of the Corporate Data Warehouse (for stop codes and mental health contact data)
- `make-configs.sh` generates `application-dev.properties` to run locally
- `facilities-mock-services` can be used to create a local server that 
  mimics ArcGIS, VA ArcGIS, Access to Care, and PSSG.

#### Mock Services
Mock services is a simple Spring Boot application that may be launched
directly with 
`java -jar facilities-mock-services/target/facilities-mock-services-${VERSION}.jar`
 or `run-local -m start`.

Mock services listen on port `8666` by default. 
Supported queries can be listed by invoking `http://localhost:8666/help`.
