package com.github.cb2222124.rtms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cb2222124.rtms.dto.tax.TaxPaymentDTO;
import com.github.cb2222124.rtms.exception.VehicleNotFoundException;
import com.github.cb2222124.rtms.service.TaxPaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaxPaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class TaxPaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private TaxPaymentService taxPaymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void taxVehicleReturnsCreated() throws Exception {
        ResultActions response = mockMvc.perform(post("/tax")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TaxPaymentDTO("ABC123"))));

        response.andExpect(status().isCreated());
    }

    @Test
    void taxVehicleReturnsNotFoundWhenVehicleNotFound() throws Exception {
        given(taxPaymentService.taxVehicle(any())).willThrow(new VehicleNotFoundException());

        ResultActions response = mockMvc.perform(post("/tax")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TaxPaymentDTO("ABC123"))));

        response.andExpect(status().isNotFound());
    }

}
