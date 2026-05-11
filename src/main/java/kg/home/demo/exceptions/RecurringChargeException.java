package kg.home.demo.exceptions;

import kg.home.demo.enums.ChargeResultDetail;
import lombok.Getter;

public class RecurringChargeException extends RuntimeException {
    @Getter
    private ChargeResultDetail errorDetail;

    public RecurringChargeException(ChargeResultDetail errorDetail) {
        super(errorDetail.getDescription());
        this.errorDetail = errorDetail;
    }

    public RecurringChargeException(String message) {
        super(message);
    }

    public RecurringChargeException(String message, Throwable cause) {
        super(message, cause);
    }
}
