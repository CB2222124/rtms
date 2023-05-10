package com.github.cb2222124.rtms.service;

import com.github.cb2222124.rtms.dto.tax.NewTaxClassDTO;
import com.github.cb2222124.rtms.dto.tax.TaxClassDTO;
import com.github.cb2222124.rtms.model.TaxClass;
import com.github.cb2222124.rtms.repository.TaxClassRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaxClassServiceTest {

    @Mock
    private TaxClassRepository taxClassRepository;

    @InjectMocks
    private TaxClassService taxClassService;

    @Test
    void createTaxClassReturnsId() {
        NewTaxClassDTO taxClassDTO = new NewTaxClassDTO("Description", 1L);
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(taxClass);

        Long id = taxClassService.createTaxClass(taxClassDTO);

        assertEquals(taxClass.getId(), id);
    }

    @Test
    void updateTaxClassReturnsUpdatedDTO() {
        TaxClassDTO request = new TaxClassDTO(1L, "Description", 1L);
        TaxClass taxClass = new TaxClass();
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassDTO result = taxClassService.updateTaxClass(request);

        assertEquals(request, result);
    }

    @Test
    void getTaxClassReturnsDTO() {
        TaxClass taxClass = new TaxClass();
        taxClass.setDescription("Description");
        taxClass.setPricePence(1L);
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassDTO result = taxClassService.getTaxClass(1L);

        assertEquals(taxClass.getId(), result.id());
        assertEquals(taxClass.getDescription(), result.description());
        assertEquals(taxClass.getPricePence(), result.pricePence());
    }

    @Test
    void updateTaxClassThrowsWhenIdNotFound() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () ->
                taxClassService.updateTaxClass(new TaxClassDTO(1L, "Description", 1L)));
    }

    @Test
    void getTaxClassThrowsWhenIdNotFound() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> taxClassService.getTaxClass(1L));
    }
}
