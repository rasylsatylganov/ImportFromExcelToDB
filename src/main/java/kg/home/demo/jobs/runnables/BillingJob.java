package kg.home.demo.jobs.runnables;

import kg.home.demo.service.RecurringChargeLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "job", name = "isRunning", havingValue = "true"
)
@RequiredArgsConstructor
public class BillingJob {

    private final RecurringChargeLogService recurringChargeLogService;

    @Scheduled(cron = "${job.billing.recurring-charge-ap}")
    public void recurringChargeOfSubscriptionFee() {
        log.debug("RecurringChargeOfSubscriptionFee : START");
        recurringChargeLogService.recurringChargeExecute();
        log.debug("RecurringChargeOfSubscriptionFee : END");
    }

}
