package gov.va.api.lighthouse.facilities.api;

import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import gov.va.api.lighthouse.facilities.api.v0.GenericError;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.math.BigDecimal;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface FacilitiesNearbyApi {
  @Operation(
      tags = {"facilities"},
      summary =
          "Retrieve all VA health facilities reachable by driving "
              + "within the specified time period",
      description =
          "Retrieve all VA health facilities that are located within a specified drive time from "
              + "a specified location\nbased on passing in a combination of `street_address`, "
              + "`city`, `state`, and `zip` or `lat` and `lng` required\nparameters along with "
              + "`drive_time`.\nAdditionally one can filter the facilities within the "
              + "drive time by available services.\nResults of this operation are paginated. "
              + "Responses include pagination information\nin the standard JSON API \"links\" and "
              + "\"meta\" elements.\nThe \"attributes\" element has information about the "
              + "drivetime band that the requested location \nfell within for each facility "
              + "included in the response. The values of min_time and \nmax_time are in minutes. "
              + "For example, a facility returned with a matched min_time \nof 10 and max_time of "
              + "20 is a 10 to 20 minute drive from the requested location.\n"
              + "This endpoint does not return facility information. "
              + "To retrieve details for facilities found by `/nearby`, \n"
              + "follow the relationship links and check documentation for `/facilities`.\n",
      security = @SecurityRequirement(name = "apikey"))
  @GET
  @Path("nearby")
  @ApiResponse(
      responseCode = "200",
      description = "Success",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = NearbyResponse.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Missing Required Or Ambiguous Parameters",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = GenericError.class)))
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
  NearbyResponse getNearbyFacilities(
      @Parameter(
              name = "street_address",
              in = ParameterIn.QUERY,
              description =
                  "Street address of the location from which drive time will be calculated.")
          String streetAddress,
      @Parameter(
              name = "city",
              in = ParameterIn.QUERY,
              description = "City of the location from which drive time will be calculated.")
          String city,
      @Parameter(
              name = "state",
              in = ParameterIn.QUERY,
              description =
                  "Two character state code of the location from which "
                      + "drive time will be calculated.")
          String state,
      @Parameter(
              name = "zip",
              in = ParameterIn.QUERY,
              description = "Zip code of the location from which drive time will be calculated.",
              schema = @Schema(description = "##### or #####-####"))
          String zip,
      @Parameter(
              name = "lat",
              in = ParameterIn.QUERY,
              description = "Latitude of point to search for nearest VA Facilities.",
              schema = @Schema(type = "number", format = "float"))
          BigDecimal lat,
      @Parameter(
              name = "lng",
              in = ParameterIn.QUERY,
              description = "Longitude of point to search for nearest VA Facilities.",
              style = ParameterStyle.FORM,
              explode = Explode.TRUE,
              schema = @Schema(type = "number", format = "float"))
          BigDecimal lng,
      @Parameter(
              name = "drive_time",
              description =
                  "Filter to only include facilities that are within the specified "
                      + "number of drive time minutes from the requested location.",
              in = ParameterIn.QUERY,
              schema =
                  @Schema(
                      defaultValue = "90",
                      allowableValues = {"10", "20", "30", "40", "50", "60", "70", "80", "90"}))
          Integer driveTime,
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
              schema = @Schema(defaultValue = "1"))
          Integer page,
      @Parameter(
              name = "per_page",
              description = "Number of results to return per paginated response.",
              in = ParameterIn.QUERY,
              schema = @Schema(defaultValue = "30"))
          Integer perPage);
}
