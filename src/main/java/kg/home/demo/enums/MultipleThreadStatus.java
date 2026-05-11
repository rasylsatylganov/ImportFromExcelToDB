package kg.home.demo.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum MultipleThreadStatus {
    NEW("Новый", "success"),
    ACTIVE("Активный", "success"),
    IN_PROGRESS("В обработке", "warning"),
    SUSPEND("В ожидании", "warning"),
    ERROR("Ошибка", "danger"),
    COMPLETE("Исполнен", "success"),
    ;

    @Getter
    private String description;

    @Getter
    private String color;

    MultipleThreadStatus(String description, String color) {
        this.description = description;
        this.color = color;
    }

    public static List<MultipleThreadStatus> RECURRING_CHARGE_ALLOWED_STATUSES = Arrays.asList(ACTIVE, SUSPEND);

}
