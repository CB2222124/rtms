package com.github.cb2222124.rtms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cb2222124.rtms.dto.owner.AddressDTO;
import com.github.cb2222124.rtms.dto.owner.NewOwnerDTO;
import com.github.cb2222124.rtms.service.KeycloakService;
import com.github.cb2222124.rtms.service.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "JWT_TOKEN_URI=http://mock",
        "JWT_MASTER_TOKEN_URI=http://mock",
        "JWT_USERS_URI=http://mock",
        "JWT_JWK_SET_URI=http://mock",
        "JWT_ISSUER_URI=http://mock",
        "PORT=1",
        "KEYCLOAK_ADMIN=mock",
        "KEYCLOAK_ADMIN_PASSWORD=mock"
})
@WebMvcTest(controllers = OwnerController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private OwnerService ownerService;

    @MockBean
    @SuppressWarnings("unused")
    private KeycloakService keycloakService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void postOwnerReturnsCreatedStatus() throws Exception {
        NewOwnerDTO newOwnerDTO = new NewOwnerDTO("Username", "Password", "Name", "name@email.com",
                new AddressDTO("Line1", "Line2", "City", "County", "ABC123"));

        ResultActions response = mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newOwnerDTO)));

        response.andExpect(status().isCreated());
    }
}
