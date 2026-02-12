package app;

import app.entities.User;

import static app.Main.setup;

public class testSetup {

    public static void testSetup() {
        setup.getUserDAO().create(new User("Alice", app.enums.Role.USER));

        User alice = setup.getUserDAO().getAll().stream().filter(u -> u.getName().equals("Alice")).findFirst().orElseThrow();

        alice.setName("Alice Updated");
        alice.setRole(app.enums.Role.CHEF);

        setup.getUserDAO().update(alice);

        User updated = setup.getUserDAO().getAll().stream().filter(u -> u.getName().equals("Alice Updated")).findFirst().orElseThrow();

        System.out.println("Updated user: " + updated.getName() + " - Role: " + updated.getRole().getDisplayName());
    }
}
