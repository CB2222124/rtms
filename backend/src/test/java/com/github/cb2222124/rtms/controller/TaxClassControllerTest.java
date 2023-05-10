package com.github.cb2222124.rtms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cb2222124.rtms.dto.tax.NewTaxClassDTO;
import com.github.cb2222124.rtms.dto.tax.TaxClassDTO;
import com.github.cb2222124.rtms.exception.TaxClassNotFoundException;
import com.github.cb2222124.rtms.service.TaxClassService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = TaxClassController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class TaxClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaxClassService taxClassService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTaxClassReturnsCreatedAndLocation() throws Exception {
        given(taxClassService.createTaxClass(ArgumentMatchers.any())).willReturn(1L);

        ResultActions response = mockMvc.perform(post("/taxClasses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new NewTaxClassDTO("Description", 1L))));

        response.andExpect(status().isCreated())
                .andExpect(header().string("location", "/taxClasses/1"));
    }

    @Test
    void updateTaxClassReturnsOkAndUpdatedTaxClassDTO() throws Exception {
        TaxClassDTO request = new TaxClassDTO(1L, "Description", 1L);

        given(taxClassService.updateTaxClass(request)).willReturn(request);

        ResultActions response = mockMvc.perform(put("/taxClasses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(request.description()))
                .andExpect(jsonPath("$.pricePence").value(request.pricePence()));
    }

    @Test
    void getTaxClassReturnsOkAndTaxClassDTO() throws Exception {
        Long id = 1L;
        TaxClassDTO taxClassDTO = new TaxClassDTO(1L, "Description", 1L);
        given(taxClassService.getTaxClass(id)).willReturn(taxClassDTO);

        ResultActions response = mockMvc.perform(get("/taxClasses/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(taxClassDTO)));
    }

    @Test
    void updateTaxClassReturnsNotFoundWhenTaxClassNotFound() throws Exception {
        TaxClassDTO request = new TaxClassDTO(1L, "Description", 1L);

        given(taxClassService.updateTaxClass(request)).willThrow(new TaxClassNotFoundException());

        ResultActions response = mockMvc.perform(put("/taxClasses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(status().isNotFound());
    }

    @Test
    void getTaxClassReturnsNotFoundWhenTaxClassNotFound() throws Exception {
        given(taxClassService.getTaxClass(1L)).willThrow(new TaxClassNotFoundException());

        ResultActions response = mockMvc.perform(get("/taxClasses/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound());
    }
}

