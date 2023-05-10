package com.github.cb2222124.rtms.repository;

import com.github.cb2222124.rtms.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findVehicleByRegistration(String registration);

    List<Vehicle> findVehiclesByOwnerUsername(String username);
}
