package com.example.springbootkeycloak.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${jwt.auth.converter.principal-attribute}")
    private String  principalAtribute;

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {

        Collection<GrantedAuthority> authorities = Stream
                .concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(), extractResourceRoles(jwt)
                        .stream()).toList();

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalName(jwt));
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {

        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        ObjectMapper objectMapper = new ObjectMapper();

        if (jwt.getClaim("resource_access") == null) {
            return List.of();
        }

        resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess.get(resourceId) == null) {
            return List.of();
        }

        resource = objectMapper.convertValue(resourceAccess.get(resourceId), new TypeReference<>() {});

        if (resource.get("roles") == null) {
            return List.of();
        }

        resourceRoles = objectMapper.convertValue(resource.get("roles"), new TypeReference<>() {});

        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role))).toList();
    }

    public String getPrincipalName(Jwt jwt) {

        String claimName = JwtClaimNames.SUB;

        if (principalAtribute != null) {
            claimName = principalAtribute;
        }

        return jwt.getClaim(claimName);
    }
}
