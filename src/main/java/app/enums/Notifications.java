package app.enums;

import lombok.Getter;

@Getter
public enum Notifications {
    // =================
    //  Announcement
    // =================
    ANNOUNCEMENT_CREATED("Announcement created"),
    ANNOUNCEMENT_UPDATED("Announcement updated"),
    ANNOUNCEMENT_DELETED("Announcement deleted"),
    ANNOUNCEMENT_NOT_FOUND_BY_AUTHOR("Announcement not found with author: %s"),
    ANNOUNCEMENT_FOUND_WITH_AUTHOR("You fetched an announcement with author: %s"),
    ANNOUNCEMENTS_FOUND_WITH_AUTHOR("You fetched %s announcements with author: %s"),

    // =================
    //  Holiday
    // =================
    HOLIDAY_APPROVED("Holiday approved!"),
    HOLIDAY_REJECT("Holiday rejected!"),
    HOLIDAY_EMPTY_RESPONSIBILITY("No holidays found for responsibility: %s"),

    // =================
    //  RESPONSE
    // =================
    RESPONSE_CREATION_FAILED("Response creation failed for user: %s!"),
    RESPONSE_ACCEPTED("You accepted response with ID: %s"),
    RESPONSE_REJECTED("You rejected response with ID: %s"),
    CANCEL_RESPONSE("You took back your answer on response with ID: %s"),

    // =================
    //  RESPONSIBILITY
    // =================
    RESPONSIBILITY_EXISTS("Responsibility with the name %s already exists!"),
    RESPONSIBILITY_DELETED("Responsibility deleted with ID: %s"),
    RESPONSIBILITY_NOT_FOUND("Responsibility %s was not found!"),
    RESPONSIBILITY_ADDED_USER("Responsibiltiy %s was added to %s"),
    RESPONSIBILITY_REMOVED_USER("Responsibiltiy %s was removed from %s"),
    GET_RESPONSIBILITY_NAME("%s is not a valid responsibility!"),
    GET_USERS_RESPONSIBILITY("You fetched all users with responsibility: %s"),

    // =================
    //  SHIFT
    // =================
    SHIFT_CREATED("Shift created for: %s Date: %s Time:%s - %s"),
    SHIFT_DELETED("Shift with ID: %s got deleted"),
    SHIFT_UPDATED("Shift with ID: %s got updated!"),
    SHIFT_NOT_FOUND("Shift not found with ID: %s"),
    SHIFT_NOT_OWNED("You must own the shift!"),
    SHIFT_TAKEN("Shift with ID: %s has been taken by %s"),
    ALREADY_TAKEN("Shift has already been taken!"),
    NO_SHIFTS("No shifts for user with id: %s!"),
    GET_BY_DATE_EMPTY("No shifts for date: %s!"),

    MUST_BE_DATETIME_FORMAT("Wrong date time format. Must be yyyy-MM-dd HH:mm:ss! You entered: %s"),
    MUST_BE_DATE_FORMAT("Wrong date format. Must be yyyy-mm-dd! You entered: %s"),
    MUST_BE_TIME_FORMAT("Wrong time format. Must be 00:00! You entered: %s "),

    SCHEDULE_CREATED("You've created a schedule for: %s \n" +
            "Monday: %s \n" + "Tuesday: %s \n" + "Wednesday: %s \n" + "Thursday: %s \n" +
            "Friday: %s \n" + "Saturday: %s \n" + "Sunday: %s"
    ),

    // =================
    //  SHIFT REQUEST
    // =================
    SHIFT_REQUEST_CREATE_FAILED("Creation of shift request failed!"),
    SHIFT_REQUEST_DELETED("Shift request with ID: %s has been successfully deleted!"),
    GET_SHIFTREQUEST_DATE("You fetched %s shift requests by date: %s"),
    DELETING_OUTDATED_SHIFTREQUESTS("Deleting outdated shift requests has started!"),

    // =================
    //  USER
    // =================
    LOGGED_IN("Welcome back %s!"),
    LOGGED_OUT("See you later!"),
    NOT_LOGGED_IN("Not logged in..."),
    LOGIN_FAILED("Login failed.."),
    WRONG_PASSWORD("You entered the wrong password!"),
    WRONG_USERNAME("You did not enter a valid username!"),

    USERNAME_EXISTS("Username: %s already exists! Choose another username"),
    USERNAME_UPDATED("You updated your username to %s!"),
    USERNAME_CONFIRM_MISMATCH("Username confirmation does not match!"),

    REGISTER_SUCCESS("Welcome to this site %s! We hope you will enjoy the site"),
    REGISTER_NO_USERNAME("You must enter a username"),
    REGISTER_NO_PASSWORD("You must enter a password"),
    REGISTER_NO_PASSWORD_REPEAT("You must verify your password"),
    REGISTER_PASSWORD_MISMATCH("Password confirmation does not match"),
    REGISTER_NO_FIRSTNAME("You must enter your first name"),
    REGISTER_NO_LASTNAME("You must enter your last name"),

    UPDATE_PASSWORD_NO_NEWPASSWORD("You must enter a new password!"),
    UPDATE_PASSWORD_NO_NEWPASSWORD_REPEAT("You repeat your password!"),
    PASSWORD_UPDATED("You updated your password!"),

    DELETE_USER_SUCESS("You successfully deleted the user: %s"),
    DELETE_USER_MISMATCH("Wrong username entered. \nYou entered: %s, expected: %s"),

    USER_NOT_FOUND_ID("User not found with ID: %s!"),
    USER_NOT_FOUND_USERNAME("User not found with username: %s!"),
    USER_UPDATED("User updated with ID: %s"),

    ADMINS_ONLY("Forbidden! Admins only!"),
    GET_USERS_ROLE("You fetched all users with role: %s"),
    ROLE_NOT_FOUND("%s role was not found!"),
    ROLE_ADDED_USER("Role %s was added to %s"),
    ROLE_REMOVED_USER("Role %s was removed from %s"),

    // =================
    //  GENERICS
    // =================
    MUST_BE_INT("Input must be a number! You entered: %s"),
    FIELD_EMPTY("Field must not be empty!"),
    STRING_EMPTY("You must enter context, cannot be empty!"),
    BODY_EMPTY("Body is empty or invalid!"),

    ENUM_NOT_FOUND("Enum not found!"),
    CLASS_IS_NULL("Class %s is null!"),

    CREATED("%s was successfully created"),
    UPDATED("%s was successfully updated"),
    DELETE_FAILED("Delete of %s failed for ID: %s!"),
    DELETED_WITH_ID("%s deleted with ID: %s"),

    NOT_FOUND_ID("%s not found with ID: %s"),
    NOT_FOUND_WITH_NAME("%s not found with name: %s"),
    OBJECT_WITH_ID_NOT_FOUND("%s with ID: %s was not found..."),
    OBJECT_WITH_NAME_NOT_FOUND("%s with name: %s was not found..."),

    NOT_OWNED("You dont own %s with ID: %s"),
    NOT_ALLOWED("You're not allowed to do this!"),

    APP_CLOSING("TEAM PLANNER SHUTTING DOWN..."),
    APP_CLOSED("TEAM PLANNER HAS BEEN SHUTTED DOWN..."),

    // =================
    //  INPUT / MISC
    // =================
    GET_ALL("You fetched %s %ss"),
    GET_ALL_EMPTY("No data was fetched because %s was empty!"),
    GET_BY_ID("You fetched %s with ID: %s"),
    GET_BY_NAME("You fetched %s with name: %s"),
    GET_BY_USER("You fetched %s by user ID: %s"),

    GET_CONVERSATION("You fetched conversation between %s and %s"),

    MUST_ENTER_TITLE("You must enter a title!"),
    MUST_ENTER_CONTENT("You must enter the context!"),
    MUST_ENTER_USERID("You must enter a user ID!"),

    ENTER_NAME("You must enter a name"),

    // =================
    //  TEST
    // =================
    TEST_NOTIFICATION_1("This is with %s arg!"),
    TEST_NOTIFICATION_2("This is with %s args! This is the other %s arg!"),


    // =================
    //  VERSION
    // =================
    UP_TO_DATE("TEAMPLANNER: ALL UP TO DATE"),
    NEW_UPDATE("New update available: %s \nYour version: %s");

    // ________________________________________________________

    private final String displayName;

    // ________________________________________________________

    Notifications(String displayName) {
        this.displayName = displayName;
    }

    // ________________________________________________________

}
