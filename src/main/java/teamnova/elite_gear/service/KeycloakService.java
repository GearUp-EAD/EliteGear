package teamnova.elite_gear.service;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import teamnova.elite_gear.config.KeycloakConfig;
import teamnova.elite_gear.model.CustomerDTO;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final KeycloakConfig keycloakConfig;
    private final Keycloak keycloak;

    public String addCustomer(CustomerDTO customerDTO) {
        try {
            UserRepresentation customer = createUserRepresentation(customerDTO);
            Response response = keycloak.realm(keycloakConfig.getRealm())
                    .users()
                    .create(customer);

            if (response.getStatus() != 201) {
                log.error("Failed to create user: {}", response.getStatusInfo());
                return null;
            }

            String userId = CreatedResponseUtil.getCreatedId(response);
//            sendVerificationEmail(userId);
//            addUserToGroup(userId, "f71a3776-1d05-45fd-8024-c7835488efad");

            return String.format("User created successfully: %s", customerDTO.getEmail());

        } catch (WebApplicationException e) {
            log.error("Failed to create user: {}", customerDTO.getEmail(), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    private UserRepresentation createUserRepresentation(CustomerDTO customerDTO) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(customerDTO.getEmail());
        user.setFirstName(customerDTO.getName());
        user.setEmail(customerDTO.getEmail());
        user.setEnabled(true);
        return user;
    }

    private void sendVerificationEmail(String userId) {
        try {
            UserResource userResource = keycloak.realm(keycloakConfig.getRealm())
                    .users()
                    .get(userId);
            userResource.executeActionsEmail(Arrays.asList("UPDATE_PASSWORD", "VERIFY_EMAIL"));
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private void addUserToGroup(String userId, String groupId) {
        try {
            keycloak.realm(keycloakConfig.getRealm())
                    .users()
                    .get(userId)
                    .joinGroup(groupId);
        } catch (Exception e) {
            log.error("Failed to add user to group: {}", groupId, e);
            throw new RuntimeException("Failed to add user to group", e);
        }
    }
}