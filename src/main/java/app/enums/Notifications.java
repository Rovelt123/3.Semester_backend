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
    MUST_BE_TIME_FORMAT("Wrong time format. Must be 00:00! You entered: %s "),
    SCHEDULE_CREATED("You've created a schedule for: %s \n" +
        "Monday: %s \n" + "Tuesday: %s \n" + "Wednesday: %s \n" + "Thursday: %s \n" +
        "Friday: %s \n" + "Saturday: %s \n" + "Sunday: %s"
    ),

    GET_ALL("You fetched %s %ss"),
    GET_ALL_EMPTY("No data was fetched because %s was empty!"),
    GET_BY_ID("You fetched %s with ID: %s"),
    GET_BY_NAME("You fetched %s with name: %s"),
    GET_BY_USER("You fetched %s by user ID: %s"),
    NOT_FOUND_ID("%s not found with ID: %s"),
    NOT_FOUND_WITH_NAME("%s not found with name: %s"),
    NOT_OWNED("You dont own %s with ID: %s"),
    STRING_EMPTY("You must enter context, cannot be empty!"),
    DELETED_WITH_ID("%s deleted with ID: %s"),
    FIELD_EMPTY("Field must not be empty!"),
    ENUM_NOT_FOUND("Enum not found!"),
    CREATED("%s was successfully created"),
    UPDATED("%s was successfully updated"),

    GET_CONVERSATION("You fetched conversation between %s and %s"),

    OBJECT_WITH_ID_NOT_FOUND("%s with ID: %s was not found..."),
    OBJECT_WITH_NAME_NOT_FOUND("%s with name: %s was not found..."),

    GET_RESPONSIBILITY_NAME("%s is not a valid responsibility!"),

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
    REGISTER_NO_FIRSTNAME("You must enter your first name"),
    REGISTER_NO_LASTNAME("You must enter your last name"),
    USERNAME_UPDATED("You updated your username to %s!"),
    UPDATE_PASSWORD_NO_NEWPASSWORD("You must enter a new password!"),
    UPDATE_PASSWORD_NO_NEWPASSWORD_REPEAT("You repeat your password!"),
    PASSWORD_UPDATED("You updated your password!"),
    DELETE_USER_SUCESS("You successfully deleted the user: %s"),
    ADMINS_ONLY("Forbidden! Admins only!"),
    USER_NOT_FOUND_ID("User not found with ID: %s!"),
    USER_NOT_FOUND_USERNAME("User not found with username: %s!"),
    USERNAME_CONFIRM_MISMATCH("Username confirmation does not match!"),
    USER_UPDATED("User updated with ID: %s"),
    DELETE_USER_MISMATCH("Wrong username entered. \nYou entered: %s, expected: %s"),
    ENTER_NAME("You must enter a name"),
    RESPONSIBILITY_EXISTS("Responsibility with the name %s already exists!"),

    ALREADY_TAKEN("Shift has already been taken!"),
    SHIFT_NOT_FOUND("Shift not found with ID: %s"),
    SHIFT_NOT_OWNED("You must own the shift!"),
    SHIFT_REQUEST_CREATE_FAILED("Creation of shift request failed!"),
    RESPONSE_CREATION_FAILED("Response creation failed for user: %s!"),
    DELETE_FAILED("Delete of %s failed for ID: %s!"),
    SHIFT_TAKEN("Shift with ID: %s has been taken by %s"),
    NO_SHIFTS("No shifts for user with id: %s!"),
    GET_BY_DATE_EMPTY("No shifts for date: %s!"),
    RESPONSIBILITY_DELETED("Responsibility deleted with ID: %s"),
    RESPONSIBILITY_NOT_FOUND("Responsibility %s was not found!"),
    RESPONSIBILITY_ADDED_USER("Responsibiltiy %s was added to %s"),
    RESPONSIBILITY_REMOVED_USER("Responsibiltiy %s was removed from %s"),
    RESPONSE_ACCEPTED("You accepted response with ID: %s"),
    RESPONSE_REJECTED("You rejected response with ID: %s"),
    CANCEL_RESPONSE("You took back your answer on response with ID: %s"),

    NOT_ALLOWED("You're not allowed to do this!"),
    SHIFT_REQUEST_DELETED("Shift request with ID: %s has been successfully deleted!"),
    GET_SHIFTREQUEST_DATE("You fetched %s shift requests by date: %s"),

    MUST_ENTER_TITLE("You must enter a title!"),

    ROLE_NOT_FOUND("Role %s was not found"),
    ROLE_ADDED_USER("Role %s was added to %s"),
    ROLE_REMOVED_USER("Role %s was removed from %s"),

    ANNOUNCEMENT_CREATED("Announcement created"),
    ANNOUNCEMENT_UPDATED("Announcement updated"),
    ANNOUNCEMENT_DELETED("Announcement deleted"),

    HOLIDAY_APPROVED("Holiday approved!"),
    HOLIDAY_REJECT("Holiday rejected!"),
    HOLIDAY_EMPTY_RESPONSIBILITY("No holidays found for responsibility: %s"),

    GET_USERS_RESPONSIBILITY("You fetched all users with responsibility: %s"),
    GET_USERS_ROLE("You fetched all users with role: %s"),

    BODY_EMPTY("Body is empty or invalid!"),
    APP_CLOSING("TEAM PLANNER SHUTTING DOWN..."),
    APP_CLOSED("TEAM PLANNER HAS BEEN SHUTTED DOWN...")

    ;

    // ________________________________________________________

    private final String displayName;

    // ________________________________________________________

    Notifications(String displayName) {
        this.displayName = displayName;
    }

    // ________________________________________________________

}
