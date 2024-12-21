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
import teamnova.elite_gear.service.PaymentService;


/**
 * Validate that the orderID value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = PaymentOrderUnique.PaymentOrderUniqueValidator.class
)
public @interface PaymentOrderUnique {

    String message() default "{Exists.payment.order}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class PaymentOrderUniqueValidator implements ConstraintValidator<PaymentOrderUnique, UUID> {

        private final PaymentService paymentService;
        private final HttpServletRequest request;

        public PaymentOrderUniqueValidator(final PaymentService paymentService,
                                           final HttpServletRequest request) {
            this.paymentService = paymentService;
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
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equals(paymentService.get(UUID.fromString(currentId)).getOrder())) {
                // value hasn't changed
                return true;
            }
            return !paymentService.orderExists(value);
        }

    }

}
