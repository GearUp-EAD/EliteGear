package teamnova.elite_gear.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamnova.elite_gear.model.CustomerDTO;
import teamnova.elite_gear.service.CustomerService;
import teamnova.elite_gear.util.ReferencedException;
import teamnova.elite_gear.util.ReferencedWarning;



@RestController
@RequestMapping(value = "/api/customers", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerResource {

    private final CustomerService customerService;

    public CustomerResource(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{customerID}")
    public ResponseEntity<CustomerDTO> getCustomer(
            @PathVariable(name = "customerID") final UUID customerID) {
        return ResponseEntity.ok(customerService.get(customerID));
    }
    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createCustomer(@RequestBody @Valid final CustomerDTO customerDTO) {
        final UUID createdCustomerID = customerService.create(customerDTO);
        return new ResponseEntity<>(createdCustomerID, HttpStatus.CREATED);
    }

    @PutMapping("/{customerID}")
    public ResponseEntity<UUID> updateCustomer(
            @PathVariable(name = "customerID") final UUID customerID,
            @RequestBody @Valid final CustomerDTO customerDTO) {
        customerService.update(customerID, customerDTO);
        return ResponseEntity.ok(customerID);
    }

    @DeleteMapping("/{customerID}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable(name = "customerID") final UUID customerID) {
        final ReferencedWarning referencedWarning = customerService.getReferencedWarning(customerID);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        customerService.delete(customerID);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/extract-payload")
    public UUID extractJwtPayload(@RequestHeader("Authorization") String authorizationHeader) {

        try {
            String token = authorizationHeader.replace("Bearer ", "").trim();
            String[] tokenParts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(payload);
            return customerService.checkCustomer(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to decode the JWT payload: " + e.getMessage());
        }
    }
}
