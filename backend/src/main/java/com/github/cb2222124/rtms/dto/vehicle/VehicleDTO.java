package com.github.cb2222124.rtms.dto.vehicle;

import java.time.LocalDate;

public record VehicleDTO(String registration, String make, String model, int year, int mileage, String colour,
                         boolean sorn, LocalDate taxValidUntil, Long taxClass, Long renewalPricePence) {
}
