package com.github.cb2222124.rtms.dto.vehicle;

public record NewVehicleDTO(String registration, String make, String model, int year, int mileage, String colour,
                            Long owner, Long taxClass) {
}
