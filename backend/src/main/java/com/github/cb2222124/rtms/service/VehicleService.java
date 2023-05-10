package com.github.cb2222124.rtms.service;


import com.github.cb2222124.rtms.dto.vehicle.*;
import com.github.cb2222124.rtms.exception.OwnerNotFoundException;
import com.github.cb2222124.rtms.exception.TaxClassNotFoundException;
import com.github.cb2222124.rtms.exception.VehicleNotFoundException;
import com.github.cb2222124.rtms.mapper.VehicleDTOMapper;
import com.github.cb2222124.rtms.model.Owner;
import com.github.cb2222124.rtms.model.TaxClass;
import com.github.cb2222124.rtms.model.TaxInformation;
import com.github.cb2222124.rtms.model.Vehicle;
import com.github.cb2222124.rtms.repository.OwnerRepository;
import com.github.cb2222124.rtms.repository.TaxClassRepository;
import com.github.cb2222124.rtms.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;
    private final TaxClassRepository taxClassRepository;

    public VehicleService(VehicleRepository vehicleRepository, OwnerRepository ownerRepository, TaxClassRepository taxClassRepository) {
        this.vehicleRepository = vehicleRepository;
        this.ownerRepository = ownerRepository;
        this.taxClassRepository = taxClassRepository;
    }

    /**
     * Creates a new vehicle.
     *
     * @param newVehicleDTO New vehicle information DTO.
     * @return New vehicle registration.
     * @throws OwnerNotFoundException    Specfied owner does not exist.
     * @throws TaxClassNotFoundException Specified tax class does not exist.
     */
    @Transactional
    public String createVehicle(NewVehicleDTO newVehicleDTO) throws OwnerNotFoundException, TaxClassNotFoundException {
        Owner owner = ownerRepository.findById(newVehicleDTO.owner()).orElseThrow(OwnerNotFoundException::new);
        TaxClass taxClass = taxClassRepository.findById(newVehicleDTO.taxClass()).orElseThrow(TaxClassNotFoundException::new);
        Vehicle vehicle = new Vehicle();
        vehicle.setOwner(owner);
        vehicle.setRegistration(newVehicleDTO.registration());
        vehicle.setMake(newVehicleDTO.make());
        vehicle.setModel(newVehicleDTO.model());
        vehicle.setYear(newVehicleDTO.year());
        vehicle.setMileage(newVehicleDTO.mileage());
        vehicle.setColour(newVehicleDTO.colour());
        vehicle.setSorn(false);
        TaxInformation taxInformation = new TaxInformation();
        taxInformation.setVehicle(vehicle);
        taxInformation.setTaxClass(taxClass);
        taxInformation.setValidUntil(LocalDate.now().plusYears(1));
        vehicle.setTaxInformation(taxInformation);
        vehicleRepository.save(vehicle);
        return vehicle.getRegistration();
    }

    /**
     * Gets a list of vehicles owned by a specific owner.
     *
     * @param owner Owner username.
     * @return List of vehicle DTOs.
     */
    public List<VehicleDTO> getVehicles(String owner) {
        List<Vehicle> vehicles = vehicleRepository.findVehiclesByOwnerUsername(owner);
        return vehicles.stream().map(VehicleDTOMapper::map).toList();
    }

    /**
     * Gets a specific vehicle by registration.
     *
     * @param registration Vehicle registration.
     * @return Vehicle information DTO.
     * @throws VehicleNotFoundException Vehicle with registration does not exist.
     */
    public VehicleDTO getVehicle(String registration) throws VehicleNotFoundException {
        Vehicle vehicle = vehicleRepository.findVehicleByRegistration(registration).orElseThrow(VehicleNotFoundException::new);
        return VehicleDTOMapper.map(vehicle);
    }

    /**
     * Updates the owner of a vehicle.
     *
     * @param updateVehicleOwnerDTO Update vehicle owner DTO (Vehicle registration and owner ID).
     * @throws VehicleNotFoundException Vehicle with registration does not exist.
     * @throws OwnerNotFoundException   Owner with ID does not exist.
     */
    @Transactional
    public void updateVehicleOwner(UpdateVehicleOwnerDTO updateVehicleOwnerDTO) throws VehicleNotFoundException, OwnerNotFoundException {
        Vehicle vehicle = vehicleRepository.findVehicleByRegistration(updateVehicleOwnerDTO.registration())
                .orElseThrow(VehicleNotFoundException::new);
        Owner owner = ownerRepository.findById(updateVehicleOwnerDTO.owner())
                .orElseThrow(OwnerNotFoundException::new);
        vehicle.setOwner(owner);
        vehicleRepository.save(vehicle);
    }

    /**
     * Updates the SORN status of a vehicle.
     *
     * @param updateVehicleSornDTO Update vehicle SORN status DTO (Vehicle registration and SORN status).
     * @return Whether this operation resulted in a change.
     * @throws VehicleNotFoundException Vehicle with registration does not exist.
     */
    @Transactional
    public boolean updateSornStatus(UpdateVehicleSornDTO updateVehicleSornDTO) throws VehicleNotFoundException {
        Vehicle vehicle = vehicleRepository.findVehicleByRegistration(updateVehicleSornDTO.registration())
                .orElseThrow(VehicleNotFoundException::new);
        if (vehicle.isSorn() == updateVehicleSornDTO.sorn()) return false;
        vehicle.setSorn(updateVehicleSornDTO.sorn());
        vehicleRepository.save(vehicle);
        return true;
    }

    /**
     * Updates the tax class of a vehicle.
     *
     * @param updateVehicleTaxClassDTO Update vehicle tax class DTO (Vehicle registration and tax class ID).
     * @throws VehicleNotFoundException  Vehicle with registration does not exist.
     * @throws TaxClassNotFoundException Tax class with ID does not exist.
     */
    @Transactional
    public void updateTaxClass(UpdateVehicleTaxClassDTO updateVehicleTaxClassDTO) throws VehicleNotFoundException, TaxClassNotFoundException {
        Vehicle vehicle = vehicleRepository.findVehicleByRegistration(updateVehicleTaxClassDTO.registration())
                .orElseThrow(VehicleNotFoundException::new);
        TaxClass taxClass = taxClassRepository.findById(updateVehicleTaxClassDTO.taxClass())
                .orElseThrow(TaxClassNotFoundException::new);
        TaxInformation taxInformation = vehicle.getTaxInformation();
        taxInformation.setTaxClass(taxClass);
        vehicleRepository.save(vehicle);
    }
}