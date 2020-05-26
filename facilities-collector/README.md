# facilities-collector

Internal application that consolidates facility data from various external sources.
Primary endpoint is `/collect/facilities`. In higher environments, the primary
Facilities application invokes this endpoint once per day to refresh its copy
of the facilities data.

## Data Sources

This application consolidates facilities data from the following sources.

### Access to Care
Data for
[wait times](https://www.accesstocare.va.gov/atcapis/v1.1/patientwaittimes)
and [satisfaction scores](https://www.accesstopwt.va.gov/Shep/getRawData?location=*)
for various services offered
by VA health facilities. Also used to indicate which
services VA health facilities provide.
(Not used for dental, nutrition, or podiatry services.)

### ArcGIS
Source for basic information for VA
[cemeteries](https://services3.arcgis.com/aqgBd3l68G8hEFFE/ArcGIS/rest/services/NCA_Facilities/FeatureServer),
[benefits centers](https://services3.arcgis.com/aqgBd3l68G8hEFFE/ArcGIS/rest/services/VBA_Facilities/FeatureServer),
and [vet centers](https://services3.arcgis.com/aqgBd3l68G8hEFFE/ArcGIS/rest/services/VHA_VetCenters/FeatureServer).

### Corporate Data Warehouse (SQL52)
Source for health facilities, stop codes, and mental health contact information.
Stop codes indicate services provided and wait times for VA health facilities.
(More granular than Access to Care. Used only for dental, nutrition, and podiatry services.)
VA Network only.

### State Cemetery XML
[XML document](https://www.cem.va.gov/cems/cems.xml)
for state cemeteries that are not managed by VA.

### Website CSV
[CSV document](src/main/resources/websites.csv)
of facility website URLs, maintained in this repository.

## Local Development

`../make-configs.sh`

Use `less config/application-dev.properties` to verify application properties for local development.

`java -Dspring.profiles.active=dev -jar target/facilities-collector-${VERSION}.jar`
