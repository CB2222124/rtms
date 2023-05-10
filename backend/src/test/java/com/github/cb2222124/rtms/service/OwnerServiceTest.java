package com.github.cb2222124.rtms.service;

import com.github.cb2222124.rtms.dto.owner.AddressDTO;
import com.github.cb2222124.rtms.dto.owner.NewOwnerDTO;
import com.github.cb2222124.rtms.model.Address;
import com.github.cb2222124.rtms.model.Owner;
import com.github.cb2222124.rtms.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    public void createOwnerReturnsCreatedStatus() {
        NewOwnerDTO newOwnerDTO = new NewOwnerDTO("Username", "Password", "Name", "name@email.com",
                new AddressDTO("Line1", "Line2", "City", "County", "ABC123"));

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

        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        ownerService.createOwner(newOwnerDTO);

        verify(ownerRepository, times(1)).save(any());
    }
}
