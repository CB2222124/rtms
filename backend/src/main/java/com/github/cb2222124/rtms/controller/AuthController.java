package com.github.cb2222124.rtms.controller;

import com.github.cb2222124.rtms.service.KeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakService keycloakService;

    public AuthController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @Operation(summary = "Retrieve an access token")
    @ApiResponse(responseCode = "200", description = "Access and refresh tokens", content = @Content(schema = @Schema()))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema()))
    @PostMapping("/token")
    public ResponseEntity<String> authorise(@RequestHeader HttpHeaders headers, @RequestBody String body) {
        return keycloakService.token(headers, body);
    }
}
