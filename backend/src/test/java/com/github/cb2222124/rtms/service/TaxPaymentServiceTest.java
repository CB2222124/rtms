package com.github.cb2222124.rtms.service;

import com.github.cb2222124.rtms.dto.tax.TaxPaymentDTO;
import com.github.cb2222124.rtms.dto.vehicle.VehicleDTO;
import com.github.cb2222124.rtms.exception.VehicleNotFoundException;
import com.github.cb2222124.rtms.model.*;
import com.github.cb2222124.rtms.repository.TaxPaymentRepository;
import com.github.cb2222124.rtms.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaxPaymentServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private TaxPaymentRepository taxPaymentRepository;

    @InjectMocks
    private TaxPaymentService taxPaymentService;

    @Test
    void taxPaymentReturnsTaxedVehicle() {
        LocalDate now = LocalDate.now();

        TaxClass taxClass = new TaxClass();
        taxClass.setPricePence(1L);

        TaxInformation taxInformation = new TaxInformation();
        taxInformation.setTaxClass(taxClass);
        taxInformation.setValidUntil(now);

        Owner owner = new Owner();
        owner.setId(1L);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setRegistration("ABC123");
        vehicle.setOwner(new Owner());
        vehicle.setTaxInformation(taxInformation);

        TaxPayment expectedTaxPayment = new TaxPayment();
        expectedTaxPayment.setVehicle(vehicle);
        expectedTaxPayment.setOwner(owner);
        expectedTaxPayment.setTaxClass(taxClass);
        expectedTaxPayment.setDatePaid(now);
        expectedTaxPayment.setPricePencePaid(taxClass.getPricePence());

        given(vehicleRepository.findVehicleByRegistration("ABC123")).willReturn(Optional.of(vehicle));
        given(taxPaymentRepository.save(any(TaxPayment.class))).willReturn(expectedTaxPayment);
        given(vehicleRepository.save(any(Vehicle.class))).willReturn(vehicle);

        VehicleDTO taxedVehicle = taxPaymentService.taxVehicle(new TaxPaymentDTO("ABC123"));

        verify(vehicleRepository, times(1)).findVehicleByRegistration("ABC123");
        verify(taxPaymentRepository, times(1)).save(any(TaxPayment.class));
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        assertEquals(taxedVehicle.taxValidUntil(), now.plusYears(1));
    }

    @Test
    void taxPaymentThrowsWhenRegistrationNotFound() {
        String registration = "ABC123";

        given(vehicleRepository.findVehicleByRegistration(registration)).willReturn(Optional.empty());

        verify(taxPaymentRepository, never()).save(any(TaxPayment.class));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        assertThrows(VehicleNotFoundException.class, () ->
                taxPaymentService.taxVehicle(new TaxPaymentDTO(registration)));
    }
}
