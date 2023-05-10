package com.github.cb2222124.rtms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${JWT_JWK_SET_URI}")
    private String jwkSetUri;
    @Value("${KEYCLOAK_ADMIN_ROLENAME}")
    private String ADMIN;
    @Value("${KEYCLOAK_OWNER_ROLENAME}")
    private String OWNER;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                //Documentation.
                .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                //Token requests.
                .requestMatchers(HttpMethod.POST, "/auth/token").permitAll()
                //Allow anyone to register.
                .requestMatchers(HttpMethod.POST, "/owners").permitAll()
                //Tax class requests.
                .requestMatchers(HttpMethod.GET, "/taxClasses/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/taxClasses").hasRole(ADMIN)
                .requestMatchers(HttpMethod.POST, "/taxClasses").hasRole(ADMIN)
                //Tax request.
                .requestMatchers(HttpMethod.POST, "/tax").hasRole(OWNER)
                //Vehicle query requests.
                .requestMatchers(HttpMethod.GET, "/vehicles/registration/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/vehicles/**").authenticated() //TODO: @Method level security with @PreAuthorize to require admin role to to be the specific user.
                //Vehicle change requests.
                .requestMatchers(HttpMethod.PATCH, "/vehicles/owner").hasRole(ADMIN)
                .requestMatchers(HttpMethod.PATCH, "/vehicles/sorn").hasRole(ADMIN)
                .requestMatchers(HttpMethod.PATCH, "/vehicles/taxClass").hasRole(ADMIN)
                .requestMatchers(HttpMethod.POST, "/vehicles").hasRole(ADMIN)
                .anyRequest().authenticated()
                .and().csrf().disable(); //I'd like to research this more, seems appropriate for non-browser clients? Need it for no-auth POST requests nonetheless.
        http.oauth2ResourceServer().jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()));
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    @SuppressWarnings("unchecked")
    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter((jwt) -> {
            final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
            return ((List<String>) realmAccess.get("roles")).stream()
                    .map(roleName -> "ROLE_" + roleName)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return jwtConverter;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }


}
