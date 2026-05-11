package kg.home.demo.service;

import kg.home.demo.enums.MultipleThreadStatus;
import kg.home.demo.models.RecurringChargeResult;

public interface MultipleThreadService {

    boolean transferStatus(Long itemId, MultipleThreadStatus fromStatus, MultipleThreadStatus toStatus);

    RecurringChargeResult performRecurringChargeForTaxPayer(Long itemId);

}
