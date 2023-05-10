package com.github.cb2222124.rtms.controller;

import com.github.cb2222124.rtms.dto.vehicle.*;
import com.github.cb2222124.rtms.exception.OwnerNotFoundException;
import com.github.cb2222124.rtms.exception.TaxClassNotFoundException;
import com.github.cb2222124.rtms.exception.VehicleNotFoundException;
import com.github.cb2222124.rtms.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Create a new vehicle")
    @ApiResponse(responseCode = "201", description = "Vehicle created success", content = @Content(schema = @Schema()))
    @ApiResponse(responseCode = "404", description = "Owner not found", content = @Content(schema = @Schema()))
    @ApiResponse(responseCode = "404", description = "Tax class not found", content = @Content(schema = @Schema()))
    @PostMapping
    public ResponseEntity<Void> createVehicle(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Vehicle details") @RequestBody NewVehicleDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(UriComponentsBuilder.fromPath("/vehicles/registration/{registration}")
                .buildAndExpand(vehicleService.createVehicle(request)).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(summary = "Find vehicles by owner")
    @ApiResponse(responseCode = "200", description = "Vehicles found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = VehicleDTO.class))))
    @ApiResponse(responseCode = "404", description = "Owner not found", content = @Content(schema = @Schema()))
    @GetMapping("/{owner}")
    public ResponseEntity<List<VehicleDTO>> getVehicles(@Parameter(description = "Owner username") @PathVariable String owner) {
        return ResponseEntity.ok(vehicleService.getVehicles(owner));
    }

    @Operation(summary = "Find a vehicle by registration")
    @ApiResponse(responseCode = "200", description = "Vehicle found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VehicleDTO.class)))
    @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema()))
    @GetMapping("/registration/{registration}")
    public ResponseEntity<VehicleDTO> getVehicle(@Parameter(description = "Vehicle registration") @PathVariable String registration) {
        return ResponseEntity.ok(vehicleService.getVehicle(registration));
    }

    @Operation(summary = "Update the owner of a vehicle")
    @ApiResponse(responseCode = "204", description = "Vehicle update success", content = @Content(schema = @Schema()))
    @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema()))
    @ApiResponse(responseCode = "404", description = "Owner not found", content = @Content(schema = @Schema()))
    @PatchMapping("/owner")
    public ResponseEntity<Void> updateVehicleOwner(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Registration and new owner") @RequestBody UpdateVehicleOwnerDTO updateVehicleOwnerDTO) {
        vehicleService.updateVehicleOwner(updateVehicleOwnerDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update the SORN status of a vehicle")
    @ApiResponse(responseCode = "200", description = "If SORN status has changed", content = @Content(schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema()))
    @PatchMapping("/sorn")
    public ResponseEntity<Boolean> updateSornStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Registration and SORN status") @RequestBody UpdateVehicleSornDTO updateVehicleSornDTO) {
        return ResponseEntity.ok(vehicleService.updateSornStatus(updateVehicleSornDTO));
    }

    @Operation(summary = "Update the tax class of a vehicle")
    @ApiResponse(responseCode = "204", description = "Vehicle update success", content = @Content(schema = @Schema()))
    @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema()))
    @ApiResponse(responseCode = "404", description = "Tax class not found", content = @Content(schema = @Schema()))
    @PatchMapping("/taxClass")
    public ResponseEntity<Void> updateTaxClass(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Registration and new tax class") @RequestBody UpdateVehicleTaxClassDTO updateVehicleTaxClassDTO) {
        vehicleService.updateTaxClass(updateVehicleTaxClassDTO);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    public ResponseEntity<String> handleOwnerNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Owner not found");
    }

    @ExceptionHandler(TaxClassNotFoundException.class)
    public ResponseEntity<String> handleTaxClassNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tax class not found");
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<String> handleVehicleNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found");
    }
}