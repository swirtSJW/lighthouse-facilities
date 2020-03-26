package gov.va.api.lighthouse.facilities.api;

import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GenericError;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.math.BigDecimal;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface FacilitiesSearchApi {

  @Operation(
      summary =
          "Query facilities based on a geographic bounding box and optional attribute filters",
      description =
          "Retrieve all facilities contained within the specified bounding box. Bounding box is\n"
              + "specified as four parameters long1, lat1, long2, lat2. "
              + "Relative ordering of longitude and\nlatitude parameters is unimportant.\n\n"
              + "Additionally one can filter the facilities within the bounding box by type and "
              + "available\nservices. Only facilities of type \"health\" and \"benefits\" may be "
              + "filtered by available\nservices.\n\n"
              + "Alternatively, one can retrieve multiple facilities by id using this endpoint "
              + "by making a\nrequest with a comma-separated list of ids like `?ids=id1,id2`. "
              + "When requesting facilities\nin bulk by `id`, the API will return as many ids as "
              + "it can find matches for and omit any\nids where there is no match. "
              + "It will not return an HTTP error code if it is unable to match\na requested `id`. "
              + "Clients may supply ids up to the limit their HTTP client enforces for\n"
              + "URI path lengths -- usually 2,048 characters.\n\nOne of the `ids` parameter, "
              + "the `bbox` parameter, or the `lat` and `long` parameters are\n*required* to "
              + "query this API. Requests without one or the other will return `400 Bad Request`."
              + "\n\nResults of this operation are paginated. "
              + "JSON responses include pagination information\nin the standard JSON API "
              + "\"links\" and \"meta\" elements. GeoJSON responses include pagination\n"
              + "information in the \"Link\" header.\n",
      tags = {"facilities"},
      operationId = "getFacilitiesByLocation",
      security = @SecurityRequirement(name = "apikey"))
  @GET
  @Path("/facilities")
  @ApiResponse(
      responseCode = "200",
      description = "Success",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = FacilitiesResponse.class)),
        @Content(
            mediaType = "application/vnd.geo+json",
            schema = @Schema(implementation = GeoFacilitiesResponse.class))
      },
      headers = {
        @Header(
            name = "Link",
            description =
                "GitHub-style pagination information. Only present for GeoJSON-format responses.",
            schema =
                @Schema(
                    type = "string",
                    example =
                        "<https://sandbox-api.va.gov/services/va_facilities/v0/facilities?bbox%5B%5D=-120&bbox%5B%5D=40&bbox%5B%5D=-125&bbox%5B%5D=50&page=2&per_page=20>; rel=\"self\", <https://sandbox-api.va.gov/services/va_facilities/v0/facilities?bbox%5B%5D=-120&bbox%5B%5D=40&bbox%5B%5D=-125&bbox%5B%5D=50&page=1&per_page=20>; rel=\"first\", <https://sandbox-api.va.gov/services/va_facilities/v0/facilities?bbox%5B%5D=-120&bbox%5B%5D=40&bbox%5B%5D=-125&bbox%5B%5D=50&page=1&per_page=20>; rel=\"prev\", <https://sandbox-api.va.gov/services/va_facilities/v0/facilities?bbox%5B%5D=-120&bbox%5B%5D=40&bbox%5B%5D=-125&bbox%5B%5D=50&page=3&per_page=20>; rel=\"next\", <https://sandbox-api.va.gov/services/va_facilities/v0/facilities?bbox%5B%5D=-120&bbox%5B%5D=40&bbox%5B%5D=-125&bbox%5B%5D=50&page=5&per_page=20>; rel=\"last\""))
      })
  @ApiResponse(
      responseCode = "401",
      description = "Missing API token",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = GenericError.class)))
  @ApiResponse(
      responseCode = "403",
      description = "Invalid API token",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = GenericError.class)))
  @ApiResponse(
      responseCode = "406",
      description = "Requested format unacceptable",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiError.class)))
  FacilitiesResponse getFacilitiesByLocation(
      @Parameter(
              name = "ids",
              description =
                  "List of comma separated ids of facilities to retrieve in a single request.\n"
                      + "Can be combined with lat and long parameters to retrieve the facilities "
                      + "sorted by distance from a location.\n",
              in = ParameterIn.QUERY,
              style = ParameterStyle.FORM,
              explode = Explode.FALSE)
          List<String> id,
      @Parameter(
              name = "zip",
              in = ParameterIn.QUERY,
              description =
                  "Zip code to search for VA Facilities.\n"
                      + "More detailed zip codes can be passed in, but only the first five "
                      + "digits are used to determine facilities to return.\n",
              schema = @Schema(format = "##### or #####-####"))
          String zip,
      @Parameter(
              name = "state",
              in = ParameterIn.QUERY,
              description =
                  "State code to search for VA Facilities. Except in rare cases, "
                      + "this is two characters.\n",
              schema = @Schema(format = "XX"))
          String state,
      @Parameter(
              name = "lat",
              in = ParameterIn.QUERY,
              description =
                  "Latitude of point to search for nearest VA Facilities.\n"
                      + "Latitude and Longitude parameters should be specified in "
                      + "WGS84 coordinate reference system.\n",
              schema = @Schema(type = "number", format = "float"))
          BigDecimal lat,
      @Parameter(
              name = "long",
              in = ParameterIn.QUERY,
              description =
                  "Longitude of point to search for nearest VA Facilities.\n"
                      + "Latitude and Longitude parameters should be specified in "
                      + "WGS84 coordinate reference system.\n",
              style = ParameterStyle.FORM,
              explode = Explode.TRUE,
              schema = @Schema(type = "number", format = "float"))
          BigDecimal lng,
      @Parameter(
              name = "bbox[]",
              in = ParameterIn.QUERY,
              description =
                  "Bounding longitude/latitude/longitude/latitude within "
                      + "which facilities will be returned.\n"
                      + "Bounding box parameters should be specified in "
                      + "WGS84 coordinate reference system.\n",
              style = ParameterStyle.FORM,
              explode = Explode.TRUE,
              array =
                  @ArraySchema(
                      minItems = 4,
                      maxItems = 4,
                      schema = @Schema(type = "number", format = "float")))
          List<BigDecimal> bbox,
      @Parameter(
              name = "type",
              description = "Optional facility type search filter",
              in = ParameterIn.QUERY,
              schema =
                  @Schema(
                      type = "string",
                      allowableValues = {"health", "cemetery", "benefits", "vet_center"}))
          String type,
      @Parameter(
              name = "services[]",
              description = "Optional facility service search filter",
              in = ParameterIn.QUERY,
              style = ParameterStyle.FORM,
              explode = Explode.TRUE)
          List<String> services,
      @Parameter(
              name = "page",
              description = "Page of results to return per paginated response.",
              in = ParameterIn.QUERY,
              schema = @Schema(type = "integer", defaultValue = "1"))
          Integer page,
      @Parameter(
              name = "per_page",
              description = "Number of results to return per paginated response.",
              in = ParameterIn.QUERY,
              schema = @Schema(type = "integer", defaultValue = "30"))
          Integer perPage);
}
