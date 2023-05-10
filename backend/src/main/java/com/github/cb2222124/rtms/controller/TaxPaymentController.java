package com.github.cb2222124.rtms.controller;

import com.github.cb2222124.rtms.dto.tax.TaxPaymentDTO;
import com.github.cb2222124.rtms.exception.VehicleNotFoundException;
import com.github.cb2222124.rtms.service.TaxPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tax")
public class TaxPaymentController {

    private final TaxPaymentService taxPaymentService;

    public TaxPaymentController(TaxPaymentService taxPaymentService) {
        this.taxPaymentService = taxPaymentService;
    }

    @Operation(summary = "Tax a vehicle")
    @ApiResponse(responseCode = "201", description = "Tax payment created success", content = @Content(schema = @Schema()))
    @ApiResponse(responseCode = "404", description = "Vehicle not found for registration", content = @Content(schema = @Schema()))
    @PostMapping
    public ResponseEntity<Void> tax(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payment details") @RequestBody TaxPaymentDTO request) {
        taxPaymentService.taxVehicle(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<Void> handleVehicleNotFoundException() {
        return ResponseEntity.notFound().build();
    }
}
