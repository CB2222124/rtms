package com.github.cb2222124.rtms.service;

import com.github.cb2222124.rtms.dto.vehicle.*;
import com.github.cb2222124.rtms.exception.OwnerNotFoundException;
import com.github.cb2222124.rtms.exception.TaxClassNotFoundException;
import com.github.cb2222124.rtms.exception.VehicleNotFoundException;
import com.github.cb2222124.rtms.model.Owner;
import com.github.cb2222124.rtms.model.TaxClass;
import com.github.cb2222124.rtms.model.TaxInformation;
import com.github.cb2222124.rtms.model.Vehicle;
import com.github.cb2222124.rtms.repository.OwnerRepository;
import com.github.cb2222124.rtms.repository.TaxClassRepository;
import com.github.cb2222124.rtms.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private TaxClassRepository taxClassRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void createVehicleReturnsId() {
        Owner owner = new Owner();
        owner.setId(1L);
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        NewVehicleDTO newVehicleDTO = new NewVehicleDTO("ABC123", "Make", "Model", 2023,
                1, "Colour", owner.getId(), taxClass.getId());

        given(ownerRepository.findById(owner.getId())).willReturn(Optional.of(owner));
        given(taxClassRepository.findById(taxClass.getId())).willReturn(Optional.of(taxClass));
        given(vehicleRepository.save(any())).willReturn(vehicle);
        String result = vehicleService.createVehicle(newVehicleDTO);

        assertEquals("ABC123", result);
    }

    @Test
    void getVehiclesReturnsDTOList() {
        Owner owner = new Owner();
        owner.setUsername("Owner");
        TaxInformation taxInformation = new TaxInformation();
        taxInformation.setTaxClass(new TaxClass());
        taxInformation.setValidUntil(LocalDate.now());
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setId(1L);
        vehicle1.setRegistration("ABC123");
        vehicle1.setOwner(owner);
        vehicle1.setTaxInformation(taxInformation);
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setId(2L);
        vehicle2.setRegistration("XYZ789");
        vehicle2.setOwner(owner);
        vehicle2.setTaxInformation(taxInformation);

        given(vehicleRepository.findVehiclesByOwnerUsername(owner.getUsername())).willReturn(List.of(vehicle1, vehicle2));
        List<VehicleDTO> result = vehicleService.getVehicles(owner.getUsername());

        assertEquals(2, result.size());
        assertEquals("ABC123", result.get(0).registration());
        assertEquals("XYZ789", result.get(1).registration());
    }

    @Test
    void getVehicleReturnsDTO() {
        TaxInformation taxInformation = new TaxInformation();
        taxInformation.setTaxClass(new TaxClass());
        taxInformation.setValidUntil(LocalDate.now());
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setRegistration("ABC123");
        vehicle.setTaxInformation(taxInformation);

        given(vehicleRepository.findVehicleByRegistration(vehicle.getRegistration())).willReturn(Optional.of(vehicle));
        VehicleDTO result = vehicleService.getVehicle(vehicle.getRegistration());
        assertEquals(vehicle.getRegistration(), result.registration());
    }

    @Test
    void updateVehicleOwnerUpdatesOwner() {
        Owner oldOwner = new Owner();
        oldOwner.setId(1L);
        Owner newOwner = new Owner();
        newOwner.setId(2L);
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistration("ABC123");
        vehicle.setOwner(oldOwner);

        given(vehicleRepository.findVehicleByRegistration(vehicle.getRegistration())).willReturn(Optional.of(vehicle));
        given(ownerRepository.findById(newOwner.getId())).willReturn(Optional.of(newOwner));
        vehicleService.updateVehicleOwner(new UpdateVehicleOwnerDTO(vehicle.getRegistration(), newOwner.getId()));

        verify(vehicleRepository).save(vehicle);
        assertEquals(newOwner.getId(), vehicle.getOwner().getId());
    }

    @Test
    void changeSornStatusUpdatesAndReturnsTrue() {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistration("ABC123");
        vehicle.setSorn(false);

        given(vehicleRepository.findVehicleByRegistration(vehicle.getRegistration())).willReturn(Optional.of(vehicle));
        UpdateVehicleSornDTO updateVehicleSornDTO = new UpdateVehicleSornDTO(vehicle.getRegistration(), true);
        boolean result = vehicleService.updateSornStatus(updateVehicleSornDTO);

        verify(vehicleRepository).save(vehicle);
        assertTrue(result);
        assertTrue(vehicle.isSorn());
    }

    @Test
    void noChangeSornStatusReturnsFalse() {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistration("ABC123");
        vehicle.setSorn(false);

        given(vehicleRepository.findVehicleByRegistration(vehicle.getRegistration())).willReturn(Optional.of(vehicle));
        UpdateVehicleSornDTO updateVehicleSornDTO = new UpdateVehicleSornDTO(vehicle.getRegistration(), false);
        boolean result = vehicleService.updateSornStatus(updateVehicleSornDTO);

        verify(vehicleRepository, never()).save(vehicle);
        assertFalse(result);
        assertFalse(vehicle.isSorn());
    }

    @Test
    void updateTaxClassUpdatesVehicle() {
        TaxClass oldTaxClass = new TaxClass();
        oldTaxClass.setId(1L);
        TaxClass newTaxClass = new TaxClass();
        newTaxClass.setId(2L);
        TaxInformation taxInformation = new TaxInformation();
        taxInformation.setTaxClass(oldTaxClass);
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistration("ABC123");
        vehicle.setTaxInformation(taxInformation);

        given(vehicleRepository.findVehicleByRegistration(vehicle.getRegistration()))
                .willReturn(Optional.of(vehicle));
        given(taxClassRepository.findById(newTaxClass.getId())).willReturn(Optional.of(newTaxClass));
        UpdateVehicleTaxClassDTO updateVehicleTaxClassDTO = new UpdateVehicleTaxClassDTO(vehicle.getRegistration(), newTaxClass.getId());
        vehicleService.updateTaxClass(updateVehicleTaxClassDTO);

        verify(vehicleRepository).save(vehicle);
        assertEquals(newTaxClass.getId(), vehicle.getTaxInformation().getTaxClass().getId());
    }

    @Test
    void createVehicleThrowsWhenOwnerNotFound() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OwnerNotFoundException.class, () -> vehicleService.createVehicle(
                new NewVehicleDTO("ABC123", "Make", "Model", 2023,
                        1, "Colour", 1L, 1L)
        ));
    }

    @Test
    void createVehicleThrowsWhenTaxClassNotFound() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(new Owner()));
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaxClassNotFoundException.class, () -> vehicleService.createVehicle(
                new NewVehicleDTO("ABC123", "Make", "Model", 2023,
                        1, "Colour", 1L, 1L)));
    }

    @Test
    void getVehicleThrowsWhenVehicleNotFound() {
        when(vehicleRepository.findVehicleByRegistration("ABC123")).thenReturn(Optional.empty());
        assertThrows(VehicleNotFoundException.class, () -> vehicleService.getVehicle("ABC123"));
    }

    @Test
    void updateVehicleOwnerThrowsWhenVehicleNotFound() {
        when(vehicleRepository.findVehicleByRegistration("ABC123")).thenReturn(Optional.empty());
        assertThrows(VehicleNotFoundException.class, () -> vehicleService.updateVehicleOwner(
                new UpdateVehicleOwnerDTO("ABC123", 1L)));
    }

    @Test
    void updateVehicleOwnerThrowsWhenOwnerNotFound() {
        when(vehicleRepository.findVehicleByRegistration("ABC123")).thenReturn(Optional.of(new Vehicle()));
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OwnerNotFoundException.class, () -> vehicleService.updateVehicleOwner(
                new UpdateVehicleOwnerDTO("ABC123", 1L)));
    }

    @Test
    void updateVehicleSornThrowsWhenVehicleNotFound() {
        when(vehicleRepository.findVehicleByRegistration("ABC123")).thenReturn(Optional.empty());
        assertThrows(VehicleNotFoundException.class, () -> vehicleService.updateSornStatus(
                new UpdateVehicleSornDTO("ABC123", true)));
    }

    @Test
    void updateVehicleTaxClassThrowsWhenVehicleNotFound() {
        when(vehicleRepository.findVehicleByRegistration("ABC123")).thenReturn(Optional.empty());
        assertThrows(VehicleNotFoundException.class, () -> vehicleService.updateTaxClass(
                new UpdateVehicleTaxClassDTO("ABC123", 1L)));
    }

    @Test
    void updateVehicleTaxClassThrowsWhenTaxClassNotFound() {
        when(vehicleRepository.findVehicleByRegistration("ABC123")).thenReturn(Optional.of(new Vehicle()));
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaxClassNotFoundException.class, () -> vehicleService.updateTaxClass(
                new UpdateVehicleTaxClassDTO("ABC123", 1L)));
    }
}
