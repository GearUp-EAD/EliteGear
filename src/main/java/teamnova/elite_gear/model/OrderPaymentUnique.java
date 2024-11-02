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
import teamnova.elite_gear.service.OrderService;


/**
 * Validate that the id value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = OrderPaymentUnique.OrderPaymentUniqueValidator.class
)
public @interface OrderPaymentUnique {

    String message() default "{Exists.order.payment}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class OrderPaymentUniqueValidator implements ConstraintValidator<OrderPaymentUnique, UUID> {

        private final OrderService orderService;
        private final HttpServletRequest request;

        public OrderPaymentUniqueValidator(final OrderService orderService,
                final HttpServletRequest request) {
            this.orderService = orderService;
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
            final String currentId = pathVariables.get("orderID");
            if (currentId != null && value.equals(orderService.get(UUID.fromString(currentId)).getPayment())) {
                // value hasn't changed
                return true;
            }
            return !orderService.paymentExists(value);
        }

    }

}
