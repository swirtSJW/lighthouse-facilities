package gov.va.api.lighthouse.facilities.api;

import gov.va.api.lighthouse.facilities.api.telehealth.TelehealthResponse;
import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import gov.va.api.lighthouse.facilities.api.v0.GenericError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface FacilitiesTelehealthApi {
  @Operation(
      tags = {"facilities"},
      summary = "Query telehealth phone numbers given a facility ID.",
      description =
          "Retrieves all available telehealth phone numbers associated to a "
              + "given facility ID.",
      operationId = "getTelehealthById",
      security = @SecurityRequirement(name = "apikey"))
  @GET // todo change path
  @Path("telehealth")
  @ApiResponse(
      responseCode = "200",
      description = "Success",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = TelehealthResponse.class))
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
  @ApiResponse(
      responseCode = "429",
      description = "API rate limit exceeded",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiError.class)))
  TelehealthResponse getTelehealthById(
      @Parameter(
              name = "id",
              description = "Facility ID to retrieve telehealth numbers from",
              examples = @ExampleObject(name = "id", value = "vha_437"),
              in = ParameterIn.QUERY)
          String type);
}
