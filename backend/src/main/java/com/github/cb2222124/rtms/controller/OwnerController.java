package com.github.cb2222124.rtms.controller;

import com.github.cb2222124.rtms.dto.owner.NewOwnerDTO;
import com.github.cb2222124.rtms.service.KeycloakService;
import com.github.cb2222124.rtms.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    private final OwnerService ownerService;

    private final KeycloakService keycloakService;

    public OwnerController(OwnerService ownerService, KeycloakService keycloakService) {
        this.ownerService = ownerService;
        this.keycloakService = keycloakService;
    }

    @Operation(summary = "Create a new owner")
    @ApiResponse(responseCode = "201", description = "Owner created success", content = @Content(schema = @Schema()))
    @PostMapping
    public ResponseEntity<Void> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Owner details") @RequestBody NewOwnerDTO request) {
        keycloakService.createOwner(request.username(), request.password());
        ownerService.createOwner(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
