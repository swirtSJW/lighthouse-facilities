# facilities-local-db

This project builds a SQL Server Docker image
for local development. This is the operational database for the Facilities
application, and also a mock instance of the Corporate Data Warehouse.
An H2 database instance is also built for use by integration tests.

## Local Development

`local-db.sh`

Use `docker container ls` to verify that the `facilities-cdw-db` image is running.

The resulting SQL Server instance is available at `localhost:1533`,
username `SA` and password `<YourStrong!Passw0rd>`.
