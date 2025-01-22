/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package nl.tudelft.sem.template.user.api;

import java.util.UUID;
import nl.tudelft.sem.template.user.model.UserAnalytics;
import nl.tudelft.sem.template.user.model.UserProfile;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-01-22T18:09:18.751+01:00[Europe/Berlin]")
@Validated
@Tag(name = "Admin", description = "Everything related to admins")
public interface AdminApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * PUT /admin/activate/{user_to_activate} : Activate or unban a user.
     * Activate a user as admin, which in addition represents the &#39;unban&#39; action in our application too.
     *
     * @param userId Your User ID (required)
     * @param userToActivate ID of user (required)
     * @return Successful operation (status code 200)
     *         or Invalid user_id value (status code 400)
     *         or User is not an admin (status code 401)
     *         or Invalid input (status code 405)
     */
    @Operation(
        operationId = "activateUser",
        summary = "Activate or unban a user.",
        description = "Activate a user as admin, which in addition represents the 'unban' action in our application too.",
        tags = { "Admin" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfile.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid user_id value"),
            @ApiResponse(responseCode = "401", description = "User is not an admin"),
            @ApiResponse(responseCode = "405", description = "Invalid input")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/admin/activate/{user_to_activate}",
        produces = { "application/json" }
    )
    default ResponseEntity<UserProfile> activateUser(
        @NotNull @Parameter(name = "user_id", description = "Your User ID", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "user_id", required = true) UUID userId,
        @Parameter(name = "user_to_activate", description = "ID of user", required = true, in = ParameterIn.PATH) @PathVariable("user_to_activate") UUID userToActivate
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"role\" : 6, \"privacy\" : 0, \"bio\" : \"bio\", \"bookshelves\" : [ \"bookshelves\", \"bookshelves\" ], \"avatar\" : \"avatar\", \"favoriteBook\" : \"favoriteBook\", \"friends\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"favoriteGenres\" : [ \"favoriteGenres\", \"favoriteGenres\" ], \"followers\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"user_id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"following\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"name\" : \"name\", \"location\" : \"location\", \"state\" : 1, \"username\" : \"username\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /admin/delete/{user_id_other} : Delete User as Admin.
     * Delete a user as Admin, specified by user_id.
     *
     * @param userId Your User ID (required)
     * @param userIdOther ID of user (required)
     * @return Successful operation (status code 200)
     *         or Invalid user_id value (status code 400)
     *         or User is not an admin (status code 401)
     *         or Invalid input (status code 405)
     */
    @Operation(
        operationId = "adminDeleteUser",
        summary = "Delete User as Admin.",
        description = "Delete a user as Admin, specified by user_id.",
        tags = { "Admin" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid user_id value"),
            @ApiResponse(responseCode = "401", description = "User is not an admin"),
            @ApiResponse(responseCode = "405", description = "Invalid input")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/admin/delete/{user_id_other}"
    )
    default ResponseEntity<Void> adminDeleteUser(
        @NotNull @Parameter(name = "user_id", description = "Your User ID", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "user_id", required = true) UUID userId,
        @Parameter(name = "user_id_other", description = "ID of user", required = true, in = ParameterIn.PATH) @PathVariable("user_id_other") UUID userIdOther
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /admin/modify/{user_id_other} : Edit User as Admin.
     * Edit a user&#39;s profile as Admin, specified by user_id.
     *
     * @param userId Your User ID (required)
     * @param userIdOther ID of user (required)
     * @param userProfile  (optional)
     * @return Successful operation (status code 200)
     *         or Invalid user_id value (status code 400)
     *         or User is not an admin (status code 401)
     *         or Invalid input (status code 405)
     */
    @Operation(
        operationId = "adminModifyUser",
        summary = "Edit User as Admin.",
        description = "Edit a user's profile as Admin, specified by user_id.",
        tags = { "Admin" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfile.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid user_id value"),
            @ApiResponse(responseCode = "401", description = "User is not an admin"),
            @ApiResponse(responseCode = "405", description = "Invalid input")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/admin/modify/{user_id_other}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<UserProfile> adminModifyUser(
        @NotNull @Parameter(name = "user_id", description = "Your User ID", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "user_id", required = true) UUID userId,
        @Parameter(name = "user_id_other", description = "ID of user", required = true, in = ParameterIn.PATH) @PathVariable("user_id_other") UUID userIdOther,
        @Parameter(name = "UserProfile", description = "") @Valid @RequestBody(required = false) UserProfile userProfile
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"role\" : 6, \"privacy\" : 0, \"bio\" : \"bio\", \"bookshelves\" : [ \"bookshelves\", \"bookshelves\" ], \"avatar\" : \"avatar\", \"favoriteBook\" : \"favoriteBook\", \"friends\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"favoriteGenres\" : [ \"favoriteGenres\", \"favoriteGenres\" ], \"followers\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"user_id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"following\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"name\" : \"name\", \"location\" : \"location\", \"state\" : 1, \"username\" : \"username\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /admin/view/{user_id_other} : View User as Admin.
     * View a user&#39;s profile as Admin, specified by user_id.
     *
     * @param userId Your User ID (required)
     * @param userIdOther ID of user (required)
     * @return Successful operation (status code 200)
     *         or Invalid user_id value (status code 400)
     *         or User is not an admin (status code 401)
     *         or Invalid input (status code 405)
     */
    @Operation(
        operationId = "adminViewUser",
        summary = "View User as Admin.",
        description = "View a user's profile as Admin, specified by user_id.",
        tags = { "Admin" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfile.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid user_id value"),
            @ApiResponse(responseCode = "401", description = "User is not an admin"),
            @ApiResponse(responseCode = "405", description = "Invalid input")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/admin/view/{user_id_other}",
        produces = { "application/json" }
    )
    default ResponseEntity<UserProfile> adminViewUser(
        @NotNull @Parameter(name = "user_id", description = "Your User ID", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "user_id", required = true) UUID userId,
        @Parameter(name = "user_id_other", description = "ID of user", required = true, in = ParameterIn.PATH) @PathVariable("user_id_other") UUID userIdOther
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"role\" : 6, \"privacy\" : 0, \"bio\" : \"bio\", \"bookshelves\" : [ \"bookshelves\", \"bookshelves\" ], \"avatar\" : \"avatar\", \"favoriteBook\" : \"favoriteBook\", \"friends\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"favoriteGenres\" : [ \"favoriteGenres\", \"favoriteGenres\" ], \"followers\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"user_id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"following\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"name\" : \"name\", \"location\" : \"location\", \"state\" : 1, \"username\" : \"username\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /admin/ban/{user_to_ban} : Ban a user.
     * Ban a user as admin.
     *
     * @param userId Your User ID (required)
     * @param userToBan ID of user (required)
     * @return Successful operation (status code 200)
     *         or Invalid user_id value (status code 400)
     *         or User is not an admin (status code 401)
     *         or Invalid input (status code 405)
     */
    @Operation(
        operationId = "banUser",
        summary = "Ban a user.",
        description = "Ban a user as admin.",
        tags = { "Admin" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfile.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid user_id value"),
            @ApiResponse(responseCode = "401", description = "User is not an admin"),
            @ApiResponse(responseCode = "405", description = "Invalid input")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/admin/ban/{user_to_ban}",
        produces = { "application/json" }
    )
    default ResponseEntity<UserProfile> banUser(
        @NotNull @Parameter(name = "user_id", description = "Your User ID", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "user_id", required = true) UUID userId,
        @Parameter(name = "user_to_ban", description = "ID of user", required = true, in = ParameterIn.PATH) @PathVariable("user_to_ban") UUID userToBan
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"role\" : 6, \"privacy\" : 0, \"bio\" : \"bio\", \"bookshelves\" : [ \"bookshelves\", \"bookshelves\" ], \"avatar\" : \"avatar\", \"favoriteBook\" : \"favoriteBook\", \"friends\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"favoriteGenres\" : [ \"favoriteGenres\", \"favoriteGenres\" ], \"followers\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"user_id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"following\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"name\" : \"name\", \"location\" : \"location\", \"state\" : 1, \"username\" : \"username\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /admin/deactivate/{user_to_deactivate} : Deactivate a user.
     * Deactivate a user as admin.
     *
     * @param userId Your User ID (required)
     * @param userToDeactivate ID of user (required)
     * @return Successful operation (status code 200)
     *         or Invalid user_id value (status code 400)
     *         or User is not an admin (status code 401)
     *         or Invalid input (status code 405)
     */
    @Operation(
        operationId = "deactivateUser",
        summary = "Deactivate a user.",
        description = "Deactivate a user as admin.",
        tags = { "Admin" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfile.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid user_id value"),
            @ApiResponse(responseCode = "401", description = "User is not an admin"),
            @ApiResponse(responseCode = "405", description = "Invalid input")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/admin/deactivate/{user_to_deactivate}",
        produces = { "application/json" }
    )
    default ResponseEntity<UserProfile> deactivateUser(
        @NotNull @Parameter(name = "user_id", description = "Your User ID", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "user_id", required = true) UUID userId,
        @Parameter(name = "user_to_deactivate", description = "ID of user", required = true, in = ParameterIn.PATH) @PathVariable("user_to_deactivate") UUID userToDeactivate
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"role\" : 6, \"privacy\" : 0, \"bio\" : \"bio\", \"bookshelves\" : [ \"bookshelves\", \"bookshelves\" ], \"avatar\" : \"avatar\", \"favoriteBook\" : \"favoriteBook\", \"friends\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"favoriteGenres\" : [ \"favoriteGenres\", \"favoriteGenres\" ], \"followers\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"user_id\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"following\" : [ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ], \"name\" : \"name\", \"location\" : \"location\", \"state\" : 1, \"username\" : \"username\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /admin/generateAdminKey : Generate a new admin key.
     * Invoke an admin key generation as admin, the key generation happens in the business logic of the system. This new key is usable *once* in /account/proveAdmin, to prove admin identities.
     *
     * @param userId Your User ID (required)
     * @return Successful operation (status code 200)
     *         or User is not an admin (status code 401)
     */
    @Operation(
        operationId = "generateAdminKey",
        summary = "Generate a new admin key.",
        description = "Invoke an admin key generation as admin, the key generation happens in the business logic of the system. This new key is usable *once* in /account/proveAdmin, to prove admin identities.",
        tags = { "Admin" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = UUID.class))
            }),
            @ApiResponse(responseCode = "401", description = "User is not an admin")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/admin/generateAdminKey",
        produces = { "application/json" }
    )
    default ResponseEntity<UUID> generateAdminKey(
        @NotNull @Parameter(name = "user_id", description = "Your User ID", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "user_id", required = true) UUID userId
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /admin/viewAdminKeys : View all unused admin keys in the system
     * View all admin keys which have not been used already.
     *
     * @param userId Your User ID (required)
     * @return Successful operation (status code 200)
     *         or User is not an admin (status code 401)
     */
    @Operation(
        operationId = "viewAdminKeys",
        summary = "View all unused admin keys in the system",
        description = "View all admin keys which have not been used already.",
        tags = { "Admin" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UUID.class)))
            }),
            @ApiResponse(responseCode = "401", description = "User is not an admin")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/admin/viewAdminKeys",
        produces = { "application/json" }
    )
    default ResponseEntity<List<UUID>> viewAdminKeys(
        @NotNull @Parameter(name = "user_id", description = "Your User ID", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "user_id", required = true) UUID userId
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /admin/analytics : Retrieve analytics on users.
     * Return the UserAnalytics object, filled with the correct data. This data is determined by the system only and cannot be tampered with.
     *
     * @param userId Your User ID (required)
     * @return Successful operation (status code 200)
     *         or User is not an admin (status code 401)
     */
    @Operation(
        operationId = "viewAnalytics",
        summary = "Retrieve analytics on users.",
        description = "Return the UserAnalytics object, filled with the correct data. This data is determined by the system only and cannot be tampered with.",
        tags = { "Admin" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = UserAnalytics.class))
            }),
            @ApiResponse(responseCode = "401", description = "User is not an admin")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/admin/analytics",
        produces = { "application/json" }
    )
    default ResponseEntity<UserAnalytics> viewAnalytics(
        @NotNull @Parameter(name = "user_id", description = "Your User ID", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "user_id", required = true) UUID userId
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"user-count\" : 58, \"most-popular-genres\" : [ \"most-popular-genres\", \"most-popular-genres\" ], \"most-popular-book\" : \"most-popular-book\", \"most-followed-users\" : [ \"user4@email.com\", \"tudelft@tudelft.nl\", \"email@user.py\" ], \"active-users\" : 52, \"author-count\" : 12, \"user-log\" : [ \"user1 deactivated their account\", \"user42 got banned by admin388\", \"user21 became an author\" ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
