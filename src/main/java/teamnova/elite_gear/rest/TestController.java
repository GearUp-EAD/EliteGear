package teamnova.elite_gear.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamnova.elite_gear.model.CustomerDTO;
import teamnova.elite_gear.service.KeycloakService;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
class CreateUserRequest {
    private String email;
    private String fullName;
}

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final KeycloakService keycloakService;

    @PostMapping("/create-user")
    public ResponseEntity<?> testCreateUser(@RequestBody CreateUserRequest request) {
        try {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setEmail(request.getEmail());
            customerDTO.setName(request.getFullName());

            String result = keycloakService.addCustomer(customerDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Failed to create user",
                            "message", e.getMessage()
                    ));
        }
    }
}