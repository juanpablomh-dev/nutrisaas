package com.nutrisaas.core.controller;

import com.nutrisaas.core.dto.ApiError;
import com.nutrisaas.core.dto.RegisterRequest;
import com.nutrisaas.core.dto.UserResponse;
import com.nutrisaas.core.model.User;
import com.nutrisaas.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "User register",
            description = "Register a new user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Register successful",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email Already Exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "EmailAlreadyExists",
                                    value = """
                                            {
                                              "timestamp": "2026-02-21T18:25:43.511Z",
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "The email is already registered: user@example.com"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User newUser = userService.register(request);
        return ResponseEntity.ok(newUser.getUserResponseFromUser());
    }
}
