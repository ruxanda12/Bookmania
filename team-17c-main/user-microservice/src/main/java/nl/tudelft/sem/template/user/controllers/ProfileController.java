package nl.tudelft.sem.template.user.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import nl.tudelft.sem.template.user.api.ProfileApi;
import nl.tudelft.sem.template.user.database.AdminKeyRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.services.AuthorVerification;
import nl.tudelft.sem.template.user.services.FollowService;
import nl.tudelft.sem.template.user.services.LogService;
import nl.tudelft.sem.template.user.services.ProfileCheck;
import nl.tudelft.sem.template.user.services.ResponseStatus;
import nl.tudelft.sem.template.user.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user/profile")
public class ProfileController implements ProfileApi {
    private final UserProfileRepository profileRepo;
    private final AdminKeyRepository adminKeyRepo;
    private final LogService logService;

    private final UserProfileService profileService;

    /**
     * Constructor.
     *
     * @param profileRepo    UserProfile Repository
     * @param adminKeyRepo   AdminKey Repository
     * @param logService     LogService
     * @param profileService ProfileService
     */
    @Autowired
    public ProfileController(UserProfileRepository profileRepo, AdminKeyRepository adminKeyRepo,
                             LogService logService, UserProfileService profileService) {
        this.profileRepo = profileRepo;
        this.adminKeyRepo = adminKeyRepo;
        this.logService = logService;
        this.profileService = profileService;
    }

    /**
     * Returns all user profiles in the database.
     *
     * @return list of all profiles
     */
    @Override
    @GetMapping(path = {"/all"})
    public ResponseEntity<List<UserProfile>> viewAllProfiles() {
        return ResponseEntity.ok(profileRepo.findAll());
    }

    /**
     * View your own profile.
     *
     * @param userId the user id of user calling this path
     * @return the UserProfile of userId
     */
    @Override
    @GetMapping(path = {"/view"})
    public ResponseEntity<UserProfile> getOwnProfile(@RequestHeader("user_id") UUID userId) {
        Optional<UserProfile> u = profileRepo.findById(userId);
        return u.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    /**
     * Update your own User Profile.
     *
     * @param userId      the user id of user calling this path
     * @param userProfile the new profile information for current user
     * @return BAD_REQUEST or OK
     */
    @Override
    @PutMapping(path = {"/modify"})
    public ResponseEntity<Void> updateOwnProfile(@RequestHeader("user_id") UUID userId,
                                                 @RequestBody() UserProfile userProfile) {
        Optional<UserProfile> u = profileRepo.findById(userId);
        if (u.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!userProfile.getUserId().equals(u.get().getUserId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        profileRepo.save(userProfile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * View other users' profile, if they have their privacy set to public.
     *
     * @param userId User ID of profile to view (required)
     * @return the UserProfile of userId
     */
    @Override
    @GetMapping(path = {"/view/{user_id}"})
    public ResponseEntity<UserProfile> viewProfile(@PathVariable("user_id") UUID userId) {
        Optional<UserProfile> u = profileRepo.findById(userId);
        if (u.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (u.get().getPrivacy() == UserProfile.PrivacyEnum.PRIVATE) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(u.get());
    }

    /**
     * Verify an author with an author keyphrase.
     *
     * @param userId    the user id of user calling this path
     * @param keyPhrase author keyphrase to be verified
     * @return response status
     */
    @Override
    @PostMapping(path = {"/proveAuthor"})
    public ResponseEntity<Void> proveAuthor(@RequestHeader("user_id") UUID userId, @RequestBody String keyPhrase) {
        UserProfile u = ProfileCheck.getUserProfile(profileRepo, userId);

        // For now, we won't let admins become authors, since admins are already authors but with
        // even more functionalities.
        if (u == null || ProfileCheck.isAdmin(u) || !ProfileCheck.isActive(u)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (AuthorVerification.verifyAuthorKeyPhrase(keyPhrase)) {
            u.setRole(UserProfile.RoleEnum.AUTHOR);
            profileRepo.save(u);
            logService.recordActivity(u, "became an author");

            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Verify an admin with an admin key.
     *
     * @param userId the user id of user calling this path
     * @param key    admin key to be verified
     * @return response status
     */
    @Override
    @PostMapping(path = {"/proveAdmin"})
    public ResponseEntity<Void> proveAdmin(@RequestHeader("user_id") UUID userId, @RequestBody UUID key) {
        UserProfile u = ProfileCheck.getUserProfile(profileRepo, userId);
        if (!ProfileCheck.isActive(u)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (adminKeyRepo.existsById(key)) {
            adminKeyRepo.deleteById(key);
            u.setRole(UserProfile.RoleEnum.ADMIN);
            profileRepo.save(u);
            logService.recordActivity(u, "became an admin");

            return ResponseEntity.status(HttpStatus.OK).build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Finds user(s) based on certain criteria.
     *
     * @param userProfile (optional) the user profile to compare to
     * @return list of JSON objects should only contain the user profiles with matching parameters (criteria)
     */
    @Override
    @PostMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserProfile>> findProfiles(@Valid @RequestBody(required = false) UserProfile userProfile) {
        if (userProfile.getUserId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<UserProfile> filteredProfiles = profileRepo.findAll(profileService.matchProfiles(userProfile));

        if (filteredProfiles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(filteredProfiles);
    }

    @Override
    @PutMapping(value = "/follow/{user_id_other}")
    public ResponseEntity<UserProfile> followUser(@RequestHeader(value = "user_id") UUID userId,
                                                  @PathVariable("user_id_other") UUID userIdOther) {
        UserProfile user = ProfileCheck.getUserProfile(profileRepo, userId);
        UserProfile otherUser = ProfileCheck.getUserProfile(profileRepo, userIdOther);
        if (user == null || userId.equals(userIdOther)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (otherUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (otherUser.getPrivacy() == UserProfile.PrivacyEnum.PRIVATE || !ProfileCheck.isActive(user)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        FollowService.followUser(user, otherUser);
        profileRepo.save(user);
        UserProfile savedOtherProfile = profileRepo.save(otherUser);
        logService.recordActivity(user, "followed", otherUser);
        return ResponseEntity.of(Optional.of(savedOtherProfile));
    }

    @Override
    @PutMapping(value = "/unfollow/{user_id_other}")
    public ResponseEntity<UserProfile> unfollowUser(@RequestHeader(value = "user_id") UUID userId,
                                                    @PathVariable("user_id_other") UUID userIdOther) {
        UserProfile user = ProfileCheck.getUserProfile(profileRepo, userId);
        UserProfile otherUser = ProfileCheck.getUserProfile(profileRepo, userIdOther);
        if (user == null || userId.equals(userIdOther)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (otherUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (!ProfileCheck.isActive(user)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (!FollowService.unfollowUser(user, otherUser)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            profileRepo.save(user);
            UserProfile savedOtherProfile = profileRepo.save(otherUser);
            logService.recordActivity(user, "unfollowed", otherUser);
            return ResponseEntity.of(Optional.of(savedOtherProfile));
        }
    }

    @Override
    @PutMapping(path = {"/activate"})
    public ResponseEntity<Void> activateAccount(@RequestHeader("user_id") UUID userId) {
        Optional<UserProfile> userProfile = profileRepo.findById(userId);
        if (userProfile.isEmpty() || userProfile.get().getState() != UserProfile.StateEnum.INACTIVE) {
            return ResponseStatus.badRequest();
        }

        userProfile.get().setState(UserProfile.StateEnum.ACTIVE);
        profileRepo.save(userProfile.get());

        return ResponseStatus.success();
    }

    @Override
    @PutMapping(path = {"/deactivate"})
    public ResponseEntity<Void> deactivateAccount(@RequestHeader("user_id") UUID userId) {
        Optional<UserProfile> userProfile = profileRepo.findById(userId);
        if (userProfile.isEmpty() || userProfile.get().getState() != UserProfile.StateEnum.ACTIVE) {
            return ResponseStatus.badRequest();
        }

        userProfile.get().setState(UserProfile.StateEnum.INACTIVE);
        profileRepo.save(userProfile.get());

        return ResponseStatus.success();
    }


    /**
     * Add your favorite book, specified by book_id, as a user, replacing the previous entry.
     *
     * @param userId the user id of user calling this path
     * @param bookId the book id of the book you are favoring
     * @return BAD_REQUEST or NOT_FOUND or OK
     */
    @Override
    @PutMapping(path = {"/favoriteBook/{book_id}"})
    public ResponseEntity<Void> favoriteBook(@RequestHeader("user_id") UUID userId,
                                             @PathVariable("book_id") String bookId) {
        Optional<UserProfile> u = profileService.findById(userId);

        if (u.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check if the bookId exists
        if (bookId == null || bookId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // check if the book exists in the system
        if (profileService.bookExists(bookId)) {
            u.get().setFavoriteBook(bookId);
            profileService.save(u.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete your favorite book as a user, specified by book_id.
     *
     * @param userId the user id of the user calling this path
     * @param bookId the book id of the book you are un-favoring
     * @return BAD_REQUEST or NOT_FOUND or OK
     */
    @Override
    @DeleteMapping(path = {"/unfavoriteBook/{book_id}"})
    public ResponseEntity<Void> unfavoriteBook(@RequestHeader("user_id") UUID userId,
                                               @PathVariable("book_id") String bookId) {
        Optional<UserProfile> u = profileService.findById(userId);

        if (u.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check if the bookId exists
        if (bookId == null || bookId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // check if the book exists in the system
        if (profileService.bookExists(bookId)) {
            // check if the book the user is trying to un-favor is already his favorite book
            // if so, the book is removed
            if (u.get().getFavoriteBook().equals(bookId)) {
                u.get().setFavoriteBook(null);
                profileService.save(u.get());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                // book_id does not match the user's favorite book
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Add your favorite genre as a user, specified by genre_id.
     *
     * @param userId  the user id of the user calling this path
     * @param genreId the genre id of the genre you are favoring
     * @return BAD_REQUEST or NOT_FOUND or OK
     */
    @Override
    @PutMapping(path = {"/favoriteGenre/{genre_id}"})
    public ResponseEntity<Void> favoriteGenre(@RequestHeader(name = "user_id") UUID userId,
                                              @PathVariable("genre_id") String genreId) {
        Optional<UserProfile> u = profileService.findById(userId);

        if (u.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check if the genreId exists
        if (genreId == null || genreId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // get the list of genres available in the system
        List<String> allGenres = profileService.availableGenre();

        if (!allGenres.isEmpty()) {

            // check if the genre exists in the database
            if (allGenres.contains(genreId)) {

                // check if the genre is already in the favorites list
                if (!u.get().getFavoriteGenres().contains(genreId)) {
                    List<String> favoriteGenres = u.get().getFavoriteGenres();
                    favoriteGenres.add(genreId);
                    u.get().setFavoriteGenres(favoriteGenres);
                    profileService.save(u.get());
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete one of your favorite genres as a user, specified by genre_id.
     *
     * @param userId  the user id of the user calling this path
     * @param genreId the genre id of the genre you are un-favoring
     * @return BAD_REQUEST or NOT_FOUND or OK
     */
    @Override
    @DeleteMapping(path = {"/unfavoriteGenre/{genre_id}"})
    public ResponseEntity<Void> unfavoriteGenre(@RequestHeader(name = "user_id") UUID userId,
                                                @PathVariable("genre_id") String genreId) {
        Optional<UserProfile> u = profileService.findById(userId);

        if (u.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check if the genreId exists
        if (genreId == null || genreId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // get the list of genres available in the system
        List<String> allGenres = profileService.availableGenre();

        if (!allGenres.isEmpty()) {

            // check if the genre exists in the list
            if (allGenres.contains(genreId)) {
                List<String> favoriteGenres = u.get().getFavoriteGenres();

                // check if the genre is in the favorites list
                if (favoriteGenres.contains(genreId)) {
                    favoriteGenres.remove(genreId);
                    u.get().setFavoriteGenres(favoriteGenres);
                    profileService.save(u.get());
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
