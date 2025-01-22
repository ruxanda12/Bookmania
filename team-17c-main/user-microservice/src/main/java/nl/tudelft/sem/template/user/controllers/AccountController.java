package nl.tudelft.sem.template.user.controllers;

import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.template.user.api.AccountApi;
import nl.tudelft.sem.template.user.model.User;
import nl.tudelft.sem.template.user.model.UserAuth;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.model.UserWithoutPassword;
import nl.tudelft.sem.template.user.services.UserProfileService;
import nl.tudelft.sem.template.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/account")
public class AccountController implements AccountApi {
    private final UserService userService;
    private final UserProfileService profileService;

    /**
     * constructor.
     *
     * @param userService    user service
     * @param profileService profile service
     */
    @Autowired
    public AccountController(UserService userService,
                             UserProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    /**
     * Add a new user to the database.
     *
     * @param userAuth the user log-in data
     * @return the profile of the added user
     */
    @Override
    @PostMapping(path = {"/create"})
    public ResponseEntity<UserProfile> createAccount(@RequestBody UserAuth userAuth) {
        boolean isEntryDataValid = userService.validateAccountData(userAuth);
        if (!isEntryDataValid) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }

        boolean existsByEmail = userService.existsByEmail(userAuth.getEmail());
        if (existsByEmail) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); //403
        }

        UserProfile savedProfile = userService.createAccount(userAuth);
        return ResponseEntity.ok(savedProfile);
    }

    /**
     * Delete a user from the database.
     *
     * @param userId the id of the user to be deleted
     * @return appropriate response
     */
    @Override
    @DeleteMapping(path = {"/delete"})
    public ResponseEntity<Void> deleteAccount(@RequestHeader("user_id") UUID userId) {
        Optional<User> foundUser = userService.findById(userId);
        if (foundUser.isEmpty()) {
            //this user is not in the database
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<UserProfile> foundProfile = profileService.findById(userId);
        if (foundProfile.isEmpty()) {
            //this profile is not in the database
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        userService.deleteAccount(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * View your own User Account (without password).
     *
     * @param userId Your User ID (required)
     * @return userId's UserWithoutPassword
     */
    @Override
    @GetMapping(path = {"/view"})
    public ResponseEntity<UserWithoutPassword> viewAccount(@RequestHeader("user_id") UUID userId) {
        Optional<User> found = userService.findById(userId);
        if (found.isEmpty()) {
            //this user is not in the database
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserWithoutPassword noPasswordUser = userService.viewAccount(userId);
        return ResponseEntity.ok(noPasswordUser);
    }

    /**
     * Modify your own User Account.
     *
     * @param userId Your User ID (required)
     * @param user Your User object with updated values
     * @return userId's UserWithoutPassword
     */
    @Override
    @PutMapping(path = "/modify")
    public ResponseEntity<UserWithoutPassword> updateAccount(@RequestHeader("user_id") UUID userId, @RequestBody User user) {
        //user contains the updated information. if an attribute is the same as the one of the database object,
        //that means that it's not changed.

        //check if new info is valid first
        boolean validData = userService.validateNewData(user.getEmail(), user.getPassword());
        if (!validData) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }

        if (!userId.equals(user.getUserId())) {
            //user trying to modify details of another user
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }

        Optional<User> existingUserOptional = userService.findById(userId);
        if (existingUserOptional.isEmpty()) {
            //the user is not in the database
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
        }

        boolean existsUserSameEmail = userService.existsUserSameEmail(userId, user);
        if (existsUserSameEmail) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED); //405
        }

        UserWithoutPassword result = userService.modifyAccount(user);
        return ResponseEntity.ok(result);
    }

}