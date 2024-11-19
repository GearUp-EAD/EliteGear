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
import teamnova.elite_gear.service.ShippingService;


/**
 * Validate that the orderID value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = ShippingOrderUnique.ShippingOrderUniqueValidator.class
)
public @interface ShippingOrderUnique {

    String message() default "{Exists.shipping.order}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ShippingOrderUniqueValidator implements ConstraintValidator<ShippingOrderUnique, UUID> {

        private final ShippingService shippingService;
        private final HttpServletRequest request;

        public ShippingOrderUniqueValidator(final ShippingService shippingService,
                                            final HttpServletRequest request) {
            this.shippingService = shippingService;
            this.request = request;
        }

        @Override
        public boolean isValid(final UUID value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("shippingID");
            if (currentId != null && value.equals(shippingService.get(UUID.fromString(currentId)).getOrder())) {
                // value hasn't changed
                return true;
            }
            return !shippingService.orderExists(value);
        }

    }

}
