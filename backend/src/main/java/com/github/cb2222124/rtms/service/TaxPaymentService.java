package com.github.cb2222124.rtms.service;

import com.github.cb2222124.rtms.dto.tax.TaxPaymentDTO;
import com.github.cb2222124.rtms.dto.vehicle.VehicleDTO;
import com.github.cb2222124.rtms.exception.VehicleNotFoundException;
import com.github.cb2222124.rtms.mapper.VehicleDTOMapper;
import com.github.cb2222124.rtms.model.TaxPayment;
import com.github.cb2222124.rtms.model.Vehicle;
import com.github.cb2222124.rtms.repository.TaxPaymentRepository;
import com.github.cb2222124.rtms.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TaxPaymentService {

    private final VehicleRepository vehicleRepository;
    private final TaxPaymentRepository taxPaymentRepository;

    public TaxPaymentService(VehicleRepository vehicleRepository, TaxPaymentRepository taxPaymentRepository) {
        this.vehicleRepository = vehicleRepository;
        this.taxPaymentRepository = taxPaymentRepository;
    }

    /**
     * Taxes a specific vehicle by registration. A TaxPayment entity is created and persisted for auditing purposes.
     * The specified vehicles tax is made valid for another year from the current date if it is expired, otherwise a
     * year is added to the current expiry date.
     * NOTE: A nice idea to look into in the future is something like the Stripe API. The user would process the payment
     * on the frontend, call this service with a token when is verfied against stripe. This keeps holding payment details
     * out of our service.
     *
     * @param taxPaymentDTO Tax payment information.
     * @return Updated vehicle.
     * @throws VehicleNotFoundException Vehicle not found for registration.
     */
    @Transactional
    public VehicleDTO taxVehicle(TaxPaymentDTO taxPaymentDTO) throws VehicleNotFoundException {
        Vehicle vehicle = vehicleRepository.findVehicleByRegistration(taxPaymentDTO.registration()).orElseThrow(VehicleNotFoundException::new);
        TaxPayment taxPayment = new TaxPayment();
        taxPayment.setVehicle(vehicle);
        taxPayment.setOwner(vehicle.getOwner());
        taxPayment.setTaxClass(vehicle.getTaxInformation().getTaxClass());
        taxPayment.setPricePencePaid(vehicle.getTaxInformation().getTaxClass().getPricePence());
        taxPayment.setDatePaid(LocalDate.now());
        if (vehicle.getTaxInformation().getValidUntil().isBefore(LocalDate.now())) {
            vehicle.getTaxInformation().setValidUntil(LocalDate.now().plusYears(1));
        } else {
            vehicle.getTaxInformation().setValidUntil(vehicle.getTaxInformation().getValidUntil().plusYears(1));
        }
        vehicleRepository.save(vehicle);
        taxPaymentRepository.save(taxPayment);
        return VehicleDTOMapper.map(vehicle);
    }
}
