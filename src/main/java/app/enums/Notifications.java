package app.enums;

import lombok.Getter;

@Getter
public enum Notifications {
    TEST_NOTIFICATION_1("This is with %s arg!"),
    TEST_NOTIFICATION_2("This is with %s args! This is the other %s arg!"),
    MUST_BE_INT("Input must be a number! You entered: %s"),
    SHIFT_CREATED("Shift created for: %s \nDate: %s \nTime:%s - %s"),
    SHIFT_DELETED("Shift with ID: %s got deleted"),
    SHIFT_UPDATED("Shift with ID: %s got updated!"),
    CLASS_IS_NULL("Class %s is null!"),
    MUST_BE_DATE_FORMAT("Wrong date format. Must be yyyy-mm-dd! You entered: %s"),
    SCHEDULE_CREATED("You've created a schedule for: %s \n" +
        "Monday: %s \n" + "Tuesday: %s \n" + "Wednesday: %s \n" + "Thursday: %s \n" +
        "Friday: %s \n" + "Saturday: %s \n" + "Sunday: %s"
    ),

    GET_ALL("You fetched %s %ss"),
    GET_BY_ID("You fetched %s with ID: %s"),
    NOT_FOUND_ID("%s not found with ID: %s"),

    OBJECT_WITH_ID_NOT_FOUND("%s with ID: %s was not found..."),
    OBJECT_WITH_NAME_NOT_FOUND("%s with name: %s was not found..."),

    LOGGED_IN("Welcome back %s!"),
    LOGGED_OUT("See you later!"),
    NOT_LOGGED_IN("Not logged in..."),
    LOGIN_FAILED("Login failed.."),
    WRONG_PASSWORD("You entered the wrong password!"),
    WRONG_USERNAME("You did not enter a valid username!"),
    USERNAME_EXISTS("Username: %s already exists! Choose another username"),
    REGISTER_SUCCESS("Welcome to this site %s! We hope you will enjoy the site"),
    REGISTER_NO_USERNAME("You must enter a username"),
    REGISTER_NO_PASSWORD("You must enter a password"),
    REGISTER_NO_PASSWORD_REPEAT("You must verify your password"),
    REGISTER_PASSWORD_MISMATCH("Password confirmation does not match"),
    REGISTER_NO_NAME("You must enter your name"),
    USERNAME_UPDATED("You updated your username to %s!"),
    PASSWORD_UPDATED("You updated your password!"),
    DELETE_USER_SUCESS("You successfully deleted the user: %s"),
    ADMINS_ONLY("Forbidden! Admins only!"),
    USER_NOT_FOUND_ID("User not found with ID: %s!"),
    USER_NOT_FOUND_USERNAME("User not found with username: %s!"),
    USERNAME_CONFIRM_MISMATCH("Username confirmation does not match!"),


    SHIFT_NOT_FOUND("Shift not found with ID: %s"),
    RESPONSIBILITY_DELETED("Responsibility deleted with ID: %s"),

    NOT_ALLOWED("You're not allowed to do this!"),
    SHIFT_REQUEST_DELETED("Shift request with ID: %s has been successfully deleted!"),

    ;

    // ________________________________________________________

    private final String displayName;

    // ________________________________________________________

    Notifications(String displayName) {
        this.displayName = displayName;
    }

    // ________________________________________________________

}
