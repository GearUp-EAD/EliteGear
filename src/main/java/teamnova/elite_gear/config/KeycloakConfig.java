package teamnova.elite_gear.config;

import lombok.Getter;
import lombok.Setter;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakConfig {
    private String authServerUrl;
    private String realm;
    private String clientId;
    private String clientSecret;

    @NestedConfigurationProperty
    private Admin admin = new Admin();

    @Getter
    @Setter
    public static class Admin {
        private String username;
        private String password;
    }

    @Bean
    public Keycloak keycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.PASSWORD)
                .username(admin.getUsername())
                .password(admin.getPassword())
                .build();
    }
}