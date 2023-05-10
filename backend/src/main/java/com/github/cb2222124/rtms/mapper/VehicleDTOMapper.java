package com.github.cb2222124.rtms.mapper;

import com.github.cb2222124.rtms.dto.vehicle.VehicleDTO;
import com.github.cb2222124.rtms.model.Vehicle;

public class VehicleDTOMapper {

    /**
     * Maps a vehicle entity to a vehicle DTO.
     *
     * @param vehicle Vehicle.
     * @return Vehicle DTO.
     */
    public static VehicleDTO map(Vehicle vehicle) {
        return new VehicleDTO(
                vehicle.getRegistration(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getMileage(),
                vehicle.getColour(),
                vehicle.isSorn(),
                vehicle.getTaxInformation().getValidUntil(),
                vehicle.getTaxInformation().getTaxClass().getId(),
                vehicle.getTaxInformation().getTaxClass().getPricePence());
    }
}
