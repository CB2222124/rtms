package com.github.cb2222124.rtms.service;

import com.github.cb2222124.rtms.dto.tax.NewTaxClassDTO;
import com.github.cb2222124.rtms.dto.tax.TaxClassDTO;
import com.github.cb2222124.rtms.exception.TaxClassNotFoundException;
import com.github.cb2222124.rtms.model.TaxClass;
import com.github.cb2222124.rtms.repository.TaxClassRepository;
import org.springframework.stereotype.Service;

@Service
public class TaxClassService {

    final TaxClassRepository taxClassRepository;

    public TaxClassService(TaxClassRepository taxClassRepository) {
        this.taxClassRepository = taxClassRepository;
    }

    /**
     * Creates and persists a new tax class entity from the given DTO.
     *
     * @param request New tax class DTO.
     * @return The ID of the created tax class.
     */
    public Long createTaxClass(NewTaxClassDTO request) {
        TaxClass taxClass = new TaxClass();
        taxClass.setDescription(request.description());
        taxClass.setPricePence(request.pricePence());
        return taxClassRepository.save(taxClass).getId();
    }

    /**
     * Replaces a specific tax class with the tax class information provided.
     *
     * @param request Tax class information DTO.
     * @return Updated tax class.
     * @throws TaxClassNotFoundException Tax class with ID not found.
     */
    public TaxClassDTO updateTaxClass(TaxClassDTO request) throws TaxClassNotFoundException {
        TaxClass taxClass = taxClassRepository.findById(request.id()).orElseThrow(TaxClassNotFoundException::new);
        taxClass.setDescription(request.description());
        taxClass.setPricePence(request.pricePence());
        taxClassRepository.save(taxClass);
        return request;
    }

    /**
     * Gets a specific tax class by ID.
     *
     * @param id Tax class ID.
     * @return Tax class DTO.
     * @throws TaxClassNotFoundException Tax class with ID not found.
     */
    public TaxClassDTO getTaxClass(Long id) throws TaxClassNotFoundException {
        TaxClass taxClass = taxClassRepository.findById(id).orElseThrow(TaxClassNotFoundException::new);
        return new TaxClassDTO(taxClass.getId(), taxClass.getDescription(), taxClass.getPricePence());
    }
}
