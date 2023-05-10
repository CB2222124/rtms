package com.github.cb2222124.rtms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cb2222124.rtms.dto.vehicle.*;
import com.github.cb2222124.rtms.exception.OwnerNotFoundException;
import com.github.cb2222124.rtms.exception.TaxClassNotFoundException;
import com.github.cb2222124.rtms.exception.VehicleNotFoundException;
import com.github.cb2222124.rtms.service.VehicleService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createVehicleReturnsCreatedStatus() throws Exception {
        NewVehicleDTO newVehicleDTO = new NewVehicleDTO("ABC123", "Make", "Model", 2023, 1,
                "Colour", 1L, 1L);

        doReturn("ABC123").when(vehicleService).createVehicle(newVehicleDTO);

        ResultActions response = mockMvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newVehicleDTO)));

        response.andExpect(status().isCreated());
    }

    @Test
    void createVehicleReturnsNotFoundWhenOwnerNotFound() throws Exception {
        NewVehicleDTO newVehicleDTO = new NewVehicleDTO("ABC123", "Make", "Model", 2023, 1,
                "Colour", 1L, 1L);

        doThrow(new OwnerNotFoundException()).when(vehicleService).createVehicle(newVehicleDTO);

        ResultActions response = mockMvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newVehicleDTO)));

        response.andExpect(status().isNotFound())
                .andExpect(content().string("Owner not found"));
    }

    @Test
    void createVehicleReturnsNotFoundWhenTaxClassNotFound() throws Exception {
        NewVehicleDTO newVehicleDTO = new NewVehicleDTO("ABC123", "Make", "Model", 2023, 1,
                "Colour", 1L, 1L);

        doThrow(new TaxClassNotFoundException()).when(vehicleService).createVehicle(any());

        ResultActions response = mockMvc.perform(post("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newVehicleDTO)));

        response.andExpect(status().isNotFound())
                .andExpect(content().string("Tax class not found"));
    }

    @Test
    void getVehiclesReturnsVehicleDTOList() throws Exception {
        LocalDate now = LocalDate.now();
        VehicleDTO vehicleDTO = new VehicleDTO("ABC123", "Make", "Model", 2013, 1,
                "Colour", false, now, 1L, 1L);

        doReturn(List.of(vehicleDTO)).when(vehicleService).getVehicles("Owner");
        ResultActions response = mockMvc.perform(get("/vehicles/Owner"));

        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(vehicleDTO))));
    }

    @Test
    void getVehiclesReturnsNotFoundWhenOwnerNotFound() throws Exception {
        doThrow(new OwnerNotFoundException()).when(vehicleService).getVehicles("Owner");
        ResultActions response = mockMvc.perform(get("/vehicles/Owner"));

        response.andExpect(status().isNotFound())
                .andExpect(content().string("Owner not found"));
    }

    @Test
    void getVehicleByRegistrationReturnsVehicleDTO() throws Exception {
        LocalDate now = LocalDate.now();
        VehicleDTO vehicleDTO = new VehicleDTO("ABC123", "Make", "Model", 2013, 1,
                "Colour", false, now, 1L, 1L);

        doReturn(vehicleDTO).when(vehicleService).getVehicle("ABC123");
        ResultActions response = mockMvc.perform(get("/vehicles/registration/ABC123"));

        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(vehicleDTO)));
    }

    @Test
    void getVehicleByRegistrationReturnsNotFoundWhenNotFound() throws Exception {
        doThrow(new VehicleNotFoundException()).when(vehicleService).getVehicle("ABC123");
        ResultActions response = mockMvc.perform(get("/vehicles/registration/ABC123"));

        response.andExpect(status().isNotFound())
                .andExpect(content().string("Vehicle not found"));
    }

    @Test
    void updateVehicleOwnerReturnsNoContent() throws Exception {
        UpdateVehicleOwnerDTO updateVehicleOwnerDTO = new UpdateVehicleOwnerDTO("ABC123", 1L);

        doNothing().when(vehicleService).updateVehicleOwner(updateVehicleOwnerDTO);

        ResultActions response = mockMvc.perform(patch("/vehicles/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVehicleOwnerDTO)));

        response.andExpect(status().isNoContent());
    }

    @Test
    void updateVehicleOwnerReturnsNotFoundWhenVehicleNotFound() throws Exception {
        UpdateVehicleOwnerDTO updateVehicleOwnerDTO = new UpdateVehicleOwnerDTO("ABC123", 1L);

        doThrow(new VehicleNotFoundException()).when(vehicleService).updateVehicleOwner(updateVehicleOwnerDTO);

        ResultActions response = mockMvc.perform(patch("/vehicles/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVehicleOwnerDTO)));

        response.andExpect(status().isNotFound())
                .andExpect(content().string("Vehicle not found"));
    }

    @Test
    void updateVehicleOwnerReturnsNotFoundWhenOwnerNotFound() throws Exception {
        UpdateVehicleOwnerDTO updateVehicleOwnerDTO = new UpdateVehicleOwnerDTO("ABC123", 1L);

        doThrow(new OwnerNotFoundException()).when(vehicleService).updateVehicleOwner(updateVehicleOwnerDTO);

        ResultActions response = mockMvc.perform(patch("/vehicles/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVehicleOwnerDTO)));

        response.andExpect(status().isNotFound())
                .andExpect(content().string("Owner not found"));
    }

    @Test
    void updateVehicleSornReturnsOkAndChanged() throws Exception {
        UpdateVehicleSornDTO updateVehicleSornDTO = new UpdateVehicleSornDTO("ABC123", true);

        doReturn(true).when(vehicleService).updateSornStatus(updateVehicleSornDTO);

        ResultActions response = mockMvc.perform(patch("/vehicles/sorn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVehicleSornDTO)));

        response.andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void updateVehicleSornReturnsOkAndNotChanged() throws Exception {
        UpdateVehicleSornDTO updateVehicleSornDTO = new UpdateVehicleSornDTO("ABC123", true);

        doReturn(false).when(vehicleService).updateSornStatus(updateVehicleSornDTO);

        ResultActions response = mockMvc.perform(patch("/vehicles/sorn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVehicleSornDTO)));

        response.andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void updateVehicleSornReturnsNotFoundWhenVehicleNotFound() throws Exception {
        UpdateVehicleSornDTO updateVehicleSornDTO = new UpdateVehicleSornDTO("ABC123", true);

        doThrow(new VehicleNotFoundException()).when(vehicleService).updateSornStatus(updateVehicleSornDTO);

        ResultActions response = mockMvc.perform(patch("/vehicles/sorn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVehicleSornDTO)));

        response.andExpect(status().isNotFound())
                .andExpect(content().string("Vehicle not found"));
    }

    @Test
    void updateVehicleTaxClassReturnsNoContent() throws Exception {
        UpdateVehicleTaxClassDTO updateVehicleTaxClassDTO = new UpdateVehicleTaxClassDTO("ABC123", 1L);

        doNothing().when(vehicleService).updateTaxClass(updateVehicleTaxClassDTO);

        ResultActions response = mockMvc.perform(patch("/vehicles/taxClass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVehicleTaxClassDTO)));

        response.andExpect(status().isNoContent());
    }

    @Test
    void updateVehicleTaxClassReturnsNotFoundWhenVehicleNotFound() throws Exception {
        UpdateVehicleTaxClassDTO updateVehicleTaxClassDTO = new UpdateVehicleTaxClassDTO("ABC123", 1L);

        doThrow(new VehicleNotFoundException()).when(vehicleService).updateTaxClass(updateVehicleTaxClassDTO);

        ResultActions response = mockMvc.perform(patch("/vehicles/taxClass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVehicleTaxClassDTO)));

        response.andExpect(status().isNotFound())
                .andExpect(content().string("Vehicle not found"));
    }

    @Test
    void updateVehicleTaxClassReturnsNotFoundWhenTaxClassNotFound() throws Exception {
        UpdateVehicleTaxClassDTO updateVehicleTaxClassDTO = new UpdateVehicleTaxClassDTO("ABC123", 1L);

        doThrow(new TaxClassNotFoundException()).when(vehicleService).updateTaxClass(updateVehicleTaxClassDTO);

        ResultActions response = mockMvc.perform(patch("/vehicles/taxClass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVehicleTaxClassDTO)));

        response.andExpect(status().isNotFound())
                .andExpect(content().string("Tax class not found"));
    }
}