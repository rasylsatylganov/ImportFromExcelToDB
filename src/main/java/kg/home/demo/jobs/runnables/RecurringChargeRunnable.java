package kg.home.demo.jobs.runnables;

import kg.home.demo.entity.MultipleThread;
import kg.home.demo.service.MultipleThreadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Scope("prototype")
public class RecurringChargeRunnable implements Runnable {

    @Autowired
    MultipleThreadService multipleThreadService;

    private MultipleThread multipleThread;
    private final String executionIdentifier;


    public RecurringChargeRunnable() {
        executionIdentifier = UUID.randomUUID().toString();
    }

    public RecurringChargeRunnable(MultipleThread multipleThread) {
        this.multipleThread = multipleThread;
        executionIdentifier = UUID.randomUUID().toString();
    }

    @Override
    public void run() {
        log.info("{} :: RECURRING CHARGE_UP THREAD STARTED", this.executionIdentifier);

        multipleThreadService.performRecurringChargeForTaxPayer(multipleThread.getId());

        log.info("{} :: RECURRING CHARGE_UP THREAD COMPLETE", this.executionIdentifier);
    }

    public void setMultipleThread(MultipleThread multipleThread) {
        this.multipleThread = multipleThread;
    }
}
