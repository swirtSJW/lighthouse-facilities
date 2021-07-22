package gov.va.api.lighthouse.facilities.api;

import gov.va.api.lighthouse.facilities.api.urgentcontact.UrgentContact;
import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import gov.va.api.lighthouse.facilities.api.v0.GenericError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface UrgentContactReadApi {
  @Operation(
      summary = "Read urgent contact phone numbers for a clinic",
      operationId = "getUrgentContactById",
      security = @SecurityRequirement(name = "apikey"))
  @GET
  @Path("/urgent-contact/{id}")
  @ApiResponse(
      responseCode = "200",
      description = "Success",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UrgentContact.class))
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
      responseCode = "404",
      description = "Urgent contact not found",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiError.class)))
  UrgentContact getUrgentContactById(
      @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Urgent-contact ID",
              required = true)
          String id);
}
