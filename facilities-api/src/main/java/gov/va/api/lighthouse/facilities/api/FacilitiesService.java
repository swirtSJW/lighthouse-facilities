package gov.va.api.lighthouse.facilities.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.Path;

@OpenAPIDefinition(
    info =
        @Info(
            version = "0.0.1",
            title = "VA Facilities",
            description =
                "\n## Background\n\n"
                    + "This RESTful API provides information about physical VA facilities. "
                    + "Information available includes\ngeographic location, address, phone,"
                    + " hours of operation, and available services.\n\n"
                    + "VA operates several different types of facilities, "
                    + "the types represented in this API include:\n"
                    + "- Health facilities\n"
                    + "- Benefits facilities\n"
                    + "- Cemeteries\n"
                    + "- Vet Centers\n\n"
                    + "To read an FAQ on how wait times are calculated, "
                    + "click the \"For more information\" link on [this page]"
                    + "(https://www.accesstocare.va.gov/PWT/SearchWaitTimes)."
                    + "\n\n## Getting Started\n\n### Base URLs\n\n"
                    + "The base URLs for the VA Facilities API in the various environments are:\n"
                    + "- Sandbox: `https://sandbox-api.va.gov/services/va_facilities/v0`\n"
                    + "- Production: `https://api.va.gov/services/va_facilities/v0`"
                    + "\n\n### Authorization\n\n"
                    + "API requests are authorized through a symmetric API token, "
                    + "provided in an HTTP header with name `apikey`."
                    + "\n\n### Response Formats\n\n"
                    + "Clients may request several response formats "
                    + "by setting the `Accept` header.\n"
                    + "- application/json "
                    + "- The default JSON response format complies with JSON API. "
                    + "This media type is *not* available for bulk requests using the "
                    + "`/facilities/all` endpoint. It will return `406 Not Acceptable`. .\n"
                    + "- application/vnd.geo+json "
                    + "- GeoJSON-compliant format, representing each facility as a "
                    + "Feature with a Point geometry.\n"
                    + "- text/csv "
                    + "- Available for the bulk download operation only. "
                    + "Some structured fields are omitted from the CSV response."
                    + "\n\n### Response Elements\n\n"
                    + "Some data elements within the response are only "
                    + "present for facilities of a given type:\n"
                    + "- The patient satisfaction scores contained in the `satisfaction` element "
                    + "are only applicable\n  to VA health facilities.\n"
                    + "- The patient wait time values contained in the `wait_times` element "
                    + "are only applicable to\n  VA health facilities.\n"
                    + "- The list of available services in the `services` element is only "
                    + "applicable to VA health and\n  benefits facilities."
                    + "\n\n### Active Status\n\n"
                    + "Facilities can be temporarily deactivated for a variety of reasons. "
                    + "If a facility comes back from this API with `\"active_status\": \"T\"`, "
                    + "it is not currently operating. `\"active_status\": \"A\"` "
                    + "indicates normal operations at a facility."
                    + "\n\n### Mobile Facilities\n\n"
                    + "The mobile health facilities move regularly within a region. "
                    + "If a facility comes back from this API with `\"mobile\": \"true\"`, "
                    + "the latitude/longitude and address could be inaccurate. "
                    + "To get the exact current location, please call the number listed."
                    + "\n\n## Reference\n\n"
                    + "- [Raw VA Facilities Open API Spec]"
                    + "(https://api.va.gov/services/va_facilities/docs/v0/api)\n"
                    + "- [GeoJSON Format](https://tools.ietf.org/html/rfc7946)\n"
                    + "- [JSON API Format](https://jsonapi.org/format/)\n",
            contact = @Contact(name = "developer.va.gov")),
    tags = @Tag(name = "facilities", description = "VA Facilities API"),
    servers = {
      @Server(
          url = "https://sandbox-api.va.gov/services/va_facilities/{version}",
          description = "Sandbox",
          variables = @ServerVariable(name = "version", defaultValue = "v0")),
      @Server(
          url = "https://api.va.gov/services/va_facilities/{version}",
          description = "Production",
          variables = @ServerVariable(name = "version", defaultValue = "v0"))
    })
@SecurityScheme(
    paramName = "apikey",
    type = SecuritySchemeType.APIKEY,
    name = "apikey",
    in = SecuritySchemeIn.HEADER)
@Path("/")
public interface FacilitiesService
    extends FacilitiesSearchApi, FacilitiesReadApi, FacilitiesAllApi, FacilitiesNearbyApi {}
