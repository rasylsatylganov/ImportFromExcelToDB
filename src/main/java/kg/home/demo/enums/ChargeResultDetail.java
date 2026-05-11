package kg.home.demo.enums;

import lombok.Getter;

public enum ChargeResultDetail {
    BAD_REQUEST("Неверный запрос"),
    NOT_ALLOWED("Не разрешено"),
    NOT_FOUND("Запись не найдена"),
    NOT_SAVED("Запись не сохранена"),
    UNEXPECTED_ERROR("Неизвестная ошибка"),
    FOR_CHARGE_NOT_FOUND,
    STATUS_NOT_ALLOWED_FOR_CHARGE,
    INSUFFICIENT_BALANCE,
    ;

    @Getter
    private String description;

    ChargeResultDetail() {}
    ChargeResultDetail(String description) {
        this.description = description;
    }
}
