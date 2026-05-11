package kg.home.demo.service.impl;

import kg.home.demo.entity.MultipleThread;
import kg.home.demo.enums.ChargeResultDetail;
import kg.home.demo.enums.MultipleThreadStatus;
import kg.home.demo.exceptions.RecurringChargeException;
import kg.home.demo.models.RecurringChargeResult;
import kg.home.demo.repository.MultipleThreadRepository;
import kg.home.demo.service.MultipleThreadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kg.home.demo.enums.ChargeResultDetail.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MultipleThreadServiceImpl implements MultipleThreadService {

    private final MultipleThreadRepository multipleThreadRepository;

    @Override
    @Transactional
    public boolean transferStatus(Long itemId, MultipleThreadStatus fromStatus, MultipleThreadStatus toStatus) {
        Optional<MultipleThread> multipleThreadOptional = multipleThreadRepository.findById(itemId);
        if (multipleThreadOptional.isPresent() && multipleThreadOptional.get().getStatus() == fromStatus) {
            MultipleThread multipleThread = multipleThreadOptional.get();
            multipleThread.setStatus(toStatus);
            multipleThreadRepository.saveAndFlush(multipleThread);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public RecurringChargeResult performRecurringChargeForTaxPayer(Long itemId) {
        // проводим необходимые проверки
        MultipleThread multipleThread = multipleThreadRepository.findById(itemId)
                .orElseThrow(() -> new RecurringChargeException(FOR_CHARGE_NOT_FOUND));

        if (!MultipleThreadStatus.RECURRING_CHARGE_ALLOWED_STATUSES.contains(multipleThread.getStatus()))
            throw new RecurringChargeException(STATUS_NOT_ALLOWED_FOR_CHARGE);

        // подготавливаем данные
        String chargeInfo = null;
        MultipleThreadStatus initialStatus = multipleThread.getStatus();
        RecurringChargeResult chargeResult = RecurringChargeResult.builder()
                .itemId(itemId)
                .initialStatus(initialStatus)
                .build();

        // блокируем НП для изменения
        boolean isLockedForCharge = this.transferStatus(itemId, initialStatus, MultipleThreadStatus.IN_PROGRESS);
        if (!isLockedForCharge) {
            log.warn("Unable to perform recurring charge for multipleThead with ID {} due failed locking to IN_PROGRESS status", itemId);
            chargeResult.setChargeError(STATUS_NOT_ALLOWED_FOR_CHARGE);
//          логировать действие в таблицу
//            recurringChargeLog.insertRecurringChargeLog(taxPayer, TaxPayerRecurringChargeStatus.ERROR, "Невозможно снять Абоненсткую платы у Налогоплательщика. Ошибка при смене Cтатуса Налогоплательщика!");
            return chargeResult;
        }

        try {
            chargeInfo = "SUCCESS";
            chargeResult.setChargeInfo(chargeInfo);
            // логировать в таблицу
        } catch (RecurringChargeException e) {
            if (e.getErrorDetail() == ChargeResultDetail.INSUFFICIENT_BALANCE) {
                initialStatus = MultipleThreadStatus.SUSPEND;
            }
            chargeResult.setChargeError(e.getErrorDetail());
            // логировать в таблицу Невозможно снять Абоненсткую платы у Налогоплательщика. Ошибка: " + e.getMessage()
            log.error("Unable to perform recurring charge for itemId with ID {} due billing error", itemId, e);
        }  catch (Exception e) {
            chargeResult.setChargeError(UNEXPECTED_ERROR);
            // логировать в таблицу "Невозможно снять Абоненсткую платы у Налогоплательщика. Неизвестная ошибка: " + e.getMessage()
            log.error("Unable to perform recurring charge for itemId with ID {} due unexpected error", itemId, e);
        } finally {
            this.transferStatus(itemId, MultipleThreadStatus.IN_PROGRESS,
                    chargeResult.isSuccess() ? MultipleThreadStatus.COMPLETE : initialStatus);
        }

        return chargeResult;

        /*
        // пытаемся списать абонплату
        try {
            chargeInfoModel = recurringChargeService.chargeAP(taxPayerId, LocalDateTime.now());
            chargeResult.setChargeInfo(chargeInfoModel);
            recurringChargeLog.insertRecurringChargeLog(taxPayer, TaxPayerRecurringChargeStatus.SUCCESS, "Успешно обработано!");
        } catch (OfdBillingException e) {
            if (e.getErrorDetail() == ResultDetail.INSUFFICIENT_BALANCE) {
                initialTaxPayerStatus = OfdStatus.SUSPEND;
            }
            chargeResult.setChargeError(e.getErrorDetail());
            recurringChargeLog.insertRecurringChargeLog(taxPayer, TaxPayerRecurringChargeStatus.ERROR, "Невозможно снять Абоненсткую платы у Налогоплательщика. Ошибка: " + e.getMessage());
            log.error("Unable to perform recurring charge for taxPayer with ID {} due billing error", taxPayerId, e);
        } catch (Exception e) {
            chargeResult.setChargeError(UNEXPECTED_ERROR);
            recurringChargeLog.insertRecurringChargeLog(taxPayer, TaxPayerRecurringChargeStatus.ERROR, "Невозможно снять Абоненсткую платы у Налогоплательщика. Неизвестная ошибка: " + e.getMessage());
            log.error("Unable to perform recurring charge for taxPayer with ID {} due unexpected error", taxPayerId, e);
        } finally {
            taxPayerService.transferStatus(taxPayerId, OfdStatus.IN_PROGRESS,
                    chargeResult.isSuccess() ? OfdStatus.ACTIVE : initialTaxPayerStatus);
        }

        // сообщить связанным функционалам, что попытка списания завершилась
        syncEventPublisher.publishEvent(new RecurringChargePerformed(this, chargeResult));
* */
    }

}

