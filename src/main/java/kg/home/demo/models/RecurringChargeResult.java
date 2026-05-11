package kg.home.demo.models;

import kg.home.demo.enums.ChargeResultDetail;
import kg.home.demo.enums.MultipleThreadStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecurringChargeResult {


    Long itemId;
    MultipleThreadStatus initialStatus;
    String chargeInfo;
    ChargeResultDetail chargeError;

    public boolean isSuccess() {
        return chargeError == null;
    }

    public String getChargeErrorMessage() {
        return chargeError != null ? chargeError.name() : "неизвестная ошибка";
    }

}
