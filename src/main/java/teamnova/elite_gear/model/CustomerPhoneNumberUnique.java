package teamnova.elite_gear.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.servlet.HandlerMapping;
import teamnova.elite_gear.service.CustomerService;


/**
 * Validate that the phoneNumber value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = CustomerPhoneNumberUnique.CustomerPhoneNumberUniqueValidator.class
)
public @interface CustomerPhoneNumberUnique {

    String message() default "{Exists.customer.phoneNumber}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CustomerPhoneNumberUniqueValidator implements ConstraintValidator<CustomerPhoneNumberUnique, Integer> {

        private final CustomerService customerService;
        private final HttpServletRequest request;

        public CustomerPhoneNumberUniqueValidator(final CustomerService customerService,
                final HttpServletRequest request) {
            this.customerService = customerService;
            this.request = request;
        }

        @Override
        public boolean isValid(final Integer value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("customerID");
            if (currentId != null && value.equals(customerService.get(UUID.fromString(currentId)).getPhoneNumber())) {
                // value hasn't changed
                return true;
            }
            return !customerService.phoneNumberExists(value);
        }

    }

}
