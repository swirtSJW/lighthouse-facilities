package gov.va.api.lighthouse.facilities.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface DetailedServiceApi {
  @Operation(
      summary = "Retrieve a specific service given an ID",
      operationId = "getServiceById",
      tags = {"facilities"},
      security = @SecurityRequirement(name = "apikey"))
  @GET
  @Path("/facilities/{facilityId}/services/{serviceId}")
  @ApiResponse(
      responseCode = "200",
      description = "Success",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = DetailedServiceResponse.class))
      })
  @ApiResponse(
      responseCode = "400",
      description = "Bad request - invalid or missing query parameters",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiError.class)))
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
      responseCode = "404",
      description = "Facility not found",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "406",
      description = "Requested format unacceptable",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "429",
      description = "API rate limit exceeded",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiError.class)))
  FacilityReadResponse getServiceById(
      @Parameter(
              in = ParameterIn.PATH,
              name = "facilityId",
              description =
                  "Facility ID, in the form `<prefix>_<station>`, where prefix is one of "
                      + "\"vha\", \"vba\", \"nca\", or \"vc\", "
                      + "for health facility, benefits, cemetery, "
                      + "or vet center, respectively.",
              required = true,
              example = "vha_688")
          String id,
      @Parameter(
              in = ParameterIn.PATH,
              name = "serviceId",
              description = "Service ID",
              required = false,
              example = "covid19Vaccine")
          String service);
}
