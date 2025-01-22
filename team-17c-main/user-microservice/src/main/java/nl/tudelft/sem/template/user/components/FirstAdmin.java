package nl.tudelft.sem.template.user.components;

import nl.tudelft.sem.template.user.model.UserAuth;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.services.LogService;
import nl.tudelft.sem.template.user.services.UserProfileService;
import nl.tudelft.sem.template.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class FirstAdmin implements CommandLineRunner {
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final LogService logService;

    /**
     * Construct FirstAdmin Class.
     *
     * @param userService User Service
     * @param userProfileService UserProfile Service
     * @param logService LogService
     */
    @Autowired
    public FirstAdmin(UserService userService,
                      UserProfileService userProfileService,
                      LogService logService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.logService = logService;
    }

    /**
     * Create a default admin at startup, prints the user_id in console.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        logService.recordActivity("User API started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logService.recordActivity("User API shut down")));

        String email = "admin17c@tudelft.nl";
        // Check if email is already assigned to a user.
        if (userService.existsByEmail(email)) {
            System.out.println("First admin already exists:");
            System.out.println("    " + userService.findUserByEmail(email).getUserId());
            return;
        }

        String username = "admin";
        String password = "admin";

        UserProfile admin = userService.createAccount(new UserAuth(username, email, password));
        admin.setRole(UserProfile.RoleEnum.ADMIN);
        UserProfile savedProfile = userProfileService.save(admin);

        System.out.println("First admin created:");
        System.out.println("    " + savedProfile.getUserId());
    }
}
