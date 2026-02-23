package com.nutrisaas.core.controller;

import com.nutrisaas.core.dto.*;
import com.nutrisaas.core.model.User;
import com.nutrisaas.core.security.TokenProvider;
import com.nutrisaas.core.security.tenant.NoTenant;
import com.nutrisaas.core.service.AuthService;
import com.nutrisaas.core.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Authentication and password recovery endpoints")
@RestController
@RequestMapping("/auth/v1")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenProvider tokenProvider;
    private final PasswordResetService passwordResetService;


    @NoTenant
    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns a signed JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "UnauthorizedExample",
                                    value = """
                                            {
                                              "timestamp": "2026-02-21T18:25:43.511Z",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Invalid email or password"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        User user = authService.validate(request.getEmail(), request.getPassword());
        String token = tokenProvider.createToken(user, request.getRememberMe(), user.getAuthoritiesString());
        return new AuthResponse(token);
    }

    @NoTenant
    @Operation(
            summary = "Forgot password",
            description = "Generates a password reset token if the account exists. Always returns 200 to prevent user enumeration."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Recovery process initiated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))
            )
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest req) {
        passwordResetService.createPasswordResetTokenForEmail(req.getEmail());
        return ResponseEntity.ok(new MessageResponse("If the account exists, you will receive an email with instructions"));
    }

    @NoTenant
    @Operation(
            summary = "Reset password",
            description = "Resets the user password using a valid reset token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password updated correctly",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "InvalidTokenExample",
                                    value = """
                                            {
                                              "timestamp": "2026-02-21T18:25:43.511Z",
                                              "status": 400,
                                              "error": "InvalidTokenExample",
                                              "message": "Invalid or expired token"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Password does not meet policy requirements",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "PasswordPolicyRequirements",
                                    value = """
                                            {
                                              "timestamp": "2026-02-21T18:25:43.511Z",
                                              "status": 402,
                                              "error": "PasswordPolicyRequirements",
                                              "message": "Password does not meet policy requirements"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest req) {
        passwordResetService.resetPassword(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password updated correctly"));
    }

}
