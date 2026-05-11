package kg.home.demo.service.impl;

import kg.home.demo.entity.MultipleThread;
import kg.home.demo.jobs.runnables.RecurringChargeRunnable;
import kg.home.demo.repository.MultipleThreadRepository;
import kg.home.demo.service.RecurringChargeLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RecurringChargeLogServiceImpl implements RecurringChargeLogService {

    private static final Integer RC_BATCH_LIMIT = 50;

    @Autowired
    @Qualifier("recurringChargeExecutor")
    private ThreadPoolTaskExecutor recurringChargeExecutor;

    @Autowired
    private MultipleThreadRepository multipleThreadRepository;

//    @Autowired
//    private RecurringChargeLogRepository recurringChargeLogRepository;

    @Autowired
    ObjectFactory<RecurringChargeRunnable> recurringChargeRunnableObjectFactory;

    private volatile boolean isRunning = false;

    private LocalDateTime nextFullScanDate = LocalDateTime.now();

    @Override
    public void recurringChargeExecute() {
        log.debug("JOB: RecurringChargeExecute, status: STARTED");

        if  (nextFullScanDate.plusSeconds(60).compareTo(LocalDateTime.now()) > 0) return;

        if (!isRunning) {
            try {
                isRunning = true;
                int freeThreadsCount =  recurringChargeExecutor.getMaxPoolSize() - recurringChargeExecutor.getActiveCount();
                int fetchRowsLimit = freeThreadsCount > RC_BATCH_LIMIT ? RC_BATCH_LIMIT : freeThreadsCount;

                List<MultipleThread> multipleThreads = multipleThreadRepository.findActiveForProcessing(fetchRowsLimit);
                log.debug("JOB: RecurringChargeExecute, processing {} items...", multipleThreads.size());
                if (multipleThreads.size() == 0) nextFullScanDate = LocalDateTime.now();

                for (MultipleThread multipleThread :multipleThreads) {
                    if (recurringChargeExecutor.getActiveCount() < recurringChargeExecutor.getMaxPoolSize()) {
                        RecurringChargeRunnable recurringChargeRunnable = recurringChargeRunnableObjectFactory.getObject();
                        recurringChargeRunnable.setMultipleThread(multipleThread);
                        recurringChargeExecutor.execute(recurringChargeRunnable);
                    }
                }
            } catch (Exception e) {
                log.error("JOB: RecurringChargeExecute, status: FAIL", e);
            } finally {
                isRunning = false;
            }
        } else {
            log.warn("JOB: RecurringChargeExecute already running");
        }
        log.debug("JOB: RecurringChargeExecute, status: FINISHED");
    }
/*
    public void insertRecurringChargeLog(TaxPayer taxPayer, TaxPayerRecurringChargeStatus taxPayerRecurringChargeStatus, String message) {
        try {
            RecurringChargeLog recurringChargeLog = new RecurringChargeLog();
            recurringChargeLog.setTaxPayer(taxPayer);
            recurringChargeLog.setRecurringChargeStatus(taxPayerRecurringChargeStatus);
            recurringChargeLog.setMessage(message);
            recurringChargeLogRepository.save(recurringChargeLog);
        } catch (Exception e) {
            log.warn("ERROR: Write data in RecurringChargeLog for {} status: {} message: {} ", taxPayer.getId(), taxPayerRecurringChargeStatus, message.toString());
        }
    }
*/
}
