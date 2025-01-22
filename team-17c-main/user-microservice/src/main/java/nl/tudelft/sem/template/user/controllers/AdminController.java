package nl.tudelft.sem.template.user.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import nl.tudelft.sem.template.user.api.AdminApi;
import nl.tudelft.sem.template.user.database.AdminKeyRepository;
import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserAnalytics;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.models.AdminKey;
import nl.tudelft.sem.template.user.services.AdminKeyGenerator;
import nl.tudelft.sem.template.user.services.LogService;
import nl.tudelft.sem.template.user.services.ProfileCheck;
import nl.tudelft.sem.template.user.services.ResponseStatus;
import nl.tudelft.sem.template.user.services.UserService;
import nl.tudelft.sem.template.user.services.analytics.UserAnalyticsFiller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/admin")
public class AdminController implements AdminApi {
    private final UserProfileRepository profileRepo;
    private final AdminKeyRepository adminKeyRepo;
    private final LogItemRepository logRepo;
    private final LogService logService;
    private final UserService userService;

    /**
     * Construct the AdminController class.
     *
     * @param profileRepo  The User Profile Repository
     * @param adminKeyRepo The Admin Repository
     * @param logRepo      The Log Repository
     * @param logService   The Log Service
     * @param userService  The User Service
     */
    @Autowired
    public AdminController(UserProfileRepository profileRepo,
                           AdminKeyRepository adminKeyRepo,
                           LogItemRepository logRepo,
                           LogService logService,
                           UserService userService) {
        this.profileRepo = profileRepo;
        this.adminKeyRepo = adminKeyRepo;
        this.logRepo = logRepo;
        this.logService = logService;
        this.userService = userService;
    }

    /**
     * Generate a new AdminKey, and store this new key.
     *
     * @param userId Your User ID (required)
     * @return the UUID of the AdminKey, i.e. the actual key
     */
    @Override
    @GetMapping(path = {"/generateAdminKey"})
    public ResponseEntity<UUID> generateAdminKey(@RequestHeader("user_id") UUID userId) {
        UserProfile u = ProfileCheck.getUserProfile(profileRepo, userId);

        if (u == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!ProfileCheck.isAdmin(u)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // key might receive an existing id (chances are extremely low, but not 0)
        AdminKey key;
        do {
            key = AdminKeyGenerator.generateKey(new Random());
        } while (adminKeyRepo.existsById(key.getKey()));

        adminKeyRepo.save(key);

        return ResponseEntity.of(Optional.of(key.getKey()));
    }

    /**
     * View all AdminKeys in the system by their UUID's.
     *
     * @param userId Your User ID (required)
     * @return the UUID of the AdminKey, i.e. the actual key
     */
    @Override
    @GetMapping(path = {"/viewAdminKeys"})
    public ResponseEntity<List<UUID>> viewAdminKeys(@RequestHeader("user_id") UUID userId) {
        UserProfile u = ProfileCheck.getUserProfile(profileRepo, userId);

        if (u == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!ProfileCheck.isAdmin(u)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<AdminKey> adminKeys = adminKeyRepo.findAll();
        List<UUID> uuids = new ArrayList<>();
        for (AdminKey key : adminKeys) {
            uuids.add(key.getKey());
        }

        return ResponseEntity.of(Optional.of(uuids));
    }

    /**
     * View all AdminKeys in the system by their UUID's.
     *
     * @param userId Your User ID (required)
     * @return the UUID of the AdminKey, i.e. the actual key
     */
    @Override
    @GetMapping(path = {"/analytics"})
    public ResponseEntity<UserAnalytics> viewAnalytics(@RequestHeader("user_id") UUID userId) {
        UserProfile u = ProfileCheck.getUserProfile(profileRepo, userId);

        if (u == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!ProfileCheck.isAdmin(u)) {
            return ResponseStatus.unauthorized();
        }

        return ResponseEntity.of(Optional.of(UserAnalyticsFiller.getAnalytics(profileRepo, logRepo)));
    }

    /**
     * Activates a given User as an Admin, by their userId.
     *
     * @param userId      Your User ID (required)
     * @param userIdOther ID of user to activate (required)
     * @return the UserProfile of the activated user
     */
    @Override
    @PutMapping(path = {"/activate/{userIdOther}"})
    public ResponseEntity<UserProfile> activateUser(@RequestHeader("user_id") UUID userId,
                                                    @PathVariable UUID userIdOther) {
        UserProfile u = ProfileCheck.getUserProfile(profileRepo, userId);
        if (!ProfileCheck.isAdmin(u)) {
            return ResponseStatus.unauthorized();
        }

        Optional<UserProfile> u2 = profileRepo.findById(userIdOther);
        if (u2.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        u2.get().setState(UserProfile.StateEnum.ACTIVE);
        profileRepo.save(u2.get());

        return ResponseEntity.of(u2);
    }

    /**
     * Deactivates a given User as an Admin, by their userId.
     *
     * @param userId      Your User ID (required)
     * @param userIdOther ID of user to deactivate (required)
     * @return the UserProfile of the deactivated user
     */
    @Override
    @PutMapping(path = {"/deactivate/{userIdOther}"})
    public ResponseEntity<UserProfile> deactivateUser(@RequestHeader("user_id") UUID userId,
                                                      @PathVariable UUID userIdOther) {
        UserProfile u = ProfileCheck.getUserProfile(profileRepo, userId);
        if (!ProfileCheck.isAdmin(u)) {
            return ResponseStatus.unauthorized();
        }

        Optional<UserProfile> u2 = profileRepo.findById(userIdOther);
        if (u2.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        u2.get().setState(UserProfile.StateEnum.INACTIVE);
        profileRepo.save(u2.get());

        return ResponseEntity.of(u2);
    }

    /**
     * Bans a given User as an Admin, by their userId.
     *
     * @param userId      Your User ID (required)
     * @param userIdOther ID of user to ban (required)
     * @return the UserProfile of the banned user
     */
    @Override
    @PutMapping(path = {"/ban/{userIdOther}"})
    public ResponseEntity<UserProfile> banUser(@RequestHeader("user_id") UUID userId, @PathVariable UUID userIdOther) {
        UserProfile u = ProfileCheck.getUserProfile(profileRepo, userId);
        if (!ProfileCheck.isAdmin(u)) {
            return ResponseStatus.unauthorized();
        }

        Optional<UserProfile> u2 = profileRepo.findById(userIdOther);
        if (u2.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        u2.get().setState(UserProfile.StateEnum.BANNED);
        profileRepo.save(u2.get());

        logService.recordActivity(u, "banned", u2.get());

        return ResponseEntity.of(u2);
    }

    /**
     * View a user's profile as Admin, specified by user_id.
     *
     * @param adminId Your User ID (required)
     * @return the profile of the user_id in the path
     */
    @Override
    @GetMapping(path = {"/view/{user_id_other}"})
    public ResponseEntity<UserProfile> adminViewUser(@RequestHeader("user_id") UUID adminId,
                                                     @PathVariable("user_id_other") UUID userId) {
        UserProfile admin = ProfileCheck.getUserProfile(profileRepo, adminId);
        if (!ProfileCheck.isAdmin(admin)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UserProfile> u = profileRepo.findById(userId);
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Edit a user's profile as Admin, specified by user_id.
     *
     * @param adminId     Your User ID (required)
     * @param userProfile The profile of the user in the path
     * @return the profile of the user_id in the path
     */

    @Override
    @PutMapping(path = {"/modify/{user_id_other}"})
    public ResponseEntity<UserProfile> adminModifyUser(@RequestHeader("user_id") UUID adminId,
                                                       @PathVariable("user_id_other") UUID userId,
                                                       @RequestBody() UserProfile userProfile) {
        UserProfile admin = ProfileCheck.getUserProfile(profileRepo, adminId);
        if (!ProfileCheck.isAdmin(admin)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UserProfile> u = profileRepo.findById(userProfile.getUserId());
        if (u.isEmpty() || !userProfile.getUserId().equals(u.get().getUserId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        profileRepo.save(userProfile);
        return ResponseEntity.ok(userProfile);
    }

    /**
     * Delete a user as Admin, specified by user_id.
     *
     * @param adminId Your User ID (required)
     * @return BAD_REQUEST or NOT_FOUND or OK
     */
    @Override
    @DeleteMapping(path = {"/delete/{user_id_other}"})
    public ResponseEntity<Void> adminDeleteUser(@RequestHeader("user_id") UUID adminId,
                                                @PathVariable("user_id_other") UUID userId) {
        UserProfile admin = ProfileCheck.getUserProfile(profileRepo, adminId);
        if (!ProfileCheck.isAdmin(admin)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<UserProfile> u = profileRepo.findById(userId);
        if (u.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userService.deleteAccount(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
