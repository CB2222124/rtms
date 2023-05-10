package com.github.cb2222124.rtms.service;

import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class KeycloakService {

    @Value("${JWT_TOKEN_URI}")
    private String TOKEN_URL;

    @Value("${JWT_MASTER_TOKEN_URI}")
    private String MASTER_TOKEN_URL;

    @Value("${JWT_USERS_URI}")
    private String USERS_URL;

    @Value("${KEYCLOAK_ADMIN}")
    private String ADMIN;

    @Value("${KEYCLOAK_ADMIN_PASSWORD}")
    private String ADMIN_PASSWORD;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Forwards the headers and body of a request to keycloak and returns the result.
     * In practice this could have some sort of validation before querying keycloak.
     *
     * @param headers Request headers.
     * @param body    Request body.
     * @return Keycloak response.
     */
    public ResponseEntity<String> token(HttpHeaders headers, String body) {
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(TOKEN_URL, HttpMethod.POST, requestEntity, String.class);
    }

    public void createOwner(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getMasterAdminToken());
        HttpEntity<String> requestEntity = new HttpEntity<>(keycloakUserJson(username, password), headers);
        restTemplate.postForEntity(USERS_URL, requestEntity, String.class);
    }

    /**
     * Retrieves an access token for the master realm admin.
     * NOTE: Really we should use client credential grant, but had some issues configuring the master realm through
     * admin portal when running in a container.
     *
     * @return Access token.
     */
    private String getMasterAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", ADMIN);
        body.add("password", ADMIN_PASSWORD);
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(MASTER_TOKEN_URL, request, String.class);
        JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody())).getAsJsonObject();
        return jsonObject.get("access_token").getAsString();
    }

    /**
     * Contructs a keycloak new user json with the given parameters.
     *
     * @param username Username.
     * @param password Password.
     * @return Request body.
     */
    private String keycloakUserJson(String username, String password) {

        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("enabled", true);

        JsonObject credentialJson = new JsonObject();
        credentialJson.addProperty("type", "password");
        credentialJson.addProperty("value", password);
        credentialJson.addProperty("temporary", false);

        JsonArray credentialsArray = new JsonArray();
        credentialsArray.add(credentialJson);

        JsonArray realmRolesJsonArray = new JsonArray();
        realmRolesJsonArray.add("owner");

        json.add("credentials", credentialsArray);
        json.add("realmRoles", realmRolesJsonArray);

        return json.toString();
    }
}
