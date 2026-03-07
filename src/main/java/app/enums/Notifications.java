package app.enums;

import lombok.Getter;

@Getter
public enum Notifications {
    TEST_NOTIFICATION_1("This is with %s arg!"),
    TEST_NOTIFICATION_2("This is with %s args! This is the other %s arg!"),
    MUST_BE_INT("Input must be a number! You entered: %s"),
    SHIFT_CREATED("Shift created for: %s \nDate: %s \nTime:%s - %s"),
    SHIFT_DELETED("Shift with ID: %s got deleted"),
    SHIFT_UPDATED("Shift with ID: %s got updated:\n USER: %s \nTitle: %s \nDate: %s \nTime: %s - %s"),
    SHIFT_GET_ALL("You fetched %s shifts"),
    SHIFT_GET_BY_ID("You fetched shift with id %s"),
    CLASS_IS_NULL("Class %s is null!"),
    MUST_BE_DATE_FORMAT("Wrong date format. Must be yyyy-mm-dd! You entered: %s"),
    SCHEDULE_CREATED("You've created a schedule for: %s \n" +
        "Monday: %s \n" + "Tuesday: %s \n" + "Wednesday: %s \n" + "Thursday: %s \n" +
        "Friday: %s \n" + "Saturday: %s \n" + "Sunday: %s"
    ),
    USER_GET_BY_ID("You fetched user with id %s"),
    USER_GET_ALL("You fetched %s shifts"),

    SHIFTREQUEST_GET_BY_ID("You fetched request with id %s"),
    SHIFTREQUEST_GET_ALL("You fetched %s requests"),

    GET_ALL("You fetched %s %ss"),
    GET_BY_ID("You fetched %s with ID: %s")
    ;

    // ________________________________________________________

    private final String displayName;

    // ________________________________________________________

    Notifications(String displayName) {
        this.displayName = displayName;
    }

    // ________________________________________________________

}
