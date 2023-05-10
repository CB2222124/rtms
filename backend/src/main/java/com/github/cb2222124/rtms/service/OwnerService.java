package com.github.cb2222124.rtms.service;

import com.github.cb2222124.rtms.dto.owner.NewOwnerDTO;
import com.github.cb2222124.rtms.model.Address;
import com.github.cb2222124.rtms.model.Owner;
import com.github.cb2222124.rtms.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    /**
     * Creates and persists a new owner entity from a new owner DTO.
     *
     * @param newOwnerDTO New owner DTO.
     */
    public void createOwner(NewOwnerDTO newOwnerDTO) {
        Owner owner = new Owner();
        owner.setUsername(newOwnerDTO.username());
        owner.setName(newOwnerDTO.name());
        owner.setEmail(newOwnerDTO.email());
        owner.setVehicles(new ArrayList<>());

        Address address = new Address();
        address.setOwner(owner);
        address.setLine1(newOwnerDTO.address().line1());
        address.setLine2(newOwnerDTO.address().line2());
        address.setCity(newOwnerDTO.address().city());
        address.setCounty(newOwnerDTO.address().county());
        address.setPostcode(newOwnerDTO.address().postcode());

        owner.setAddress(address);
        ownerRepository.save(owner);
    }
}
