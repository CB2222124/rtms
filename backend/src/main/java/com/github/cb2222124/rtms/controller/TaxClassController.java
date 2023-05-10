package com.github.cb2222124.rtms.controller;

import com.github.cb2222124.rtms.dto.tax.NewTaxClassDTO;
import com.github.cb2222124.rtms.dto.tax.TaxClassDTO;
import com.github.cb2222124.rtms.exception.TaxClassNotFoundException;
import com.github.cb2222124.rtms.service.TaxClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/taxClasses")
public class TaxClassController {

    private final TaxClassService taxClassService;

    public TaxClassController(TaxClassService taxClassService) {
        this.taxClassService = taxClassService;
    }

    @Operation(summary = "Create a new tax class")
    @ApiResponse(responseCode = "201", description = "Created tax class", content = @Content(schema = @Schema()))
    @PostMapping
    public ResponseEntity<Void> createTaxClass(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Tax class details") @RequestBody NewTaxClassDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(UriComponentsBuilder.fromPath("/taxClasses/{id}").buildAndExpand(taxClassService.createTaxClass(request)).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing tax class")
    @ApiResponse(responseCode = "200", description = "Updated tax class details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NewTaxClassDTO.class)))
    @ApiResponse(responseCode = "404", description = "Tax class not found for ID", content = @Content(schema = @Schema()))
    @PutMapping
    public ResponseEntity<TaxClassDTO> updateTaxClass(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Tax class details") @RequestBody TaxClassDTO request) {
        return ResponseEntity.ok(taxClassService.updateTaxClass(request));
    }

    @Operation(summary = "Get existing tax class details")
    @ApiResponse(responseCode = "200", description = "Tax class details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NewTaxClassDTO.class)))
    @ApiResponse(responseCode = "404", description = "Tax class not found for ID", content = @Content(schema = @Schema()))
    @GetMapping("/{id}")
    public ResponseEntity<TaxClassDTO> getTaxClass(
            @Parameter(description = "ID of tax class") @PathVariable Long id) {
        return ResponseEntity.ok(taxClassService.getTaxClass(id));
    }

    @ExceptionHandler(TaxClassNotFoundException.class)
    public ResponseEntity<Void> handleTaxClassNotFoundException() {
        return ResponseEntity.notFound().build();
    }
}
