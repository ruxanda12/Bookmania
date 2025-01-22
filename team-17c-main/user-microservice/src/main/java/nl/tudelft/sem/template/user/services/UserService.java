package nl.tudelft.sem.template.user.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.template.user.database.UserRepository;
import nl.tudelft.sem.template.user.model.User;
import nl.tudelft.sem.template.user.model.UserAuth;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.model.UserWithoutPassword;
import nl.tudelft.sem.template.user.models.UserProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final LogService logService;
    private final UserProfileService profileService;
    private final RestTemplate restTemplate;

    /**
     * constructor.
     *
     * @param userRepo       user repository
     * @param profileService profile service
     * @param logService     log service
     * @param restTemplate   rest template
     */
    @Autowired
    public UserService(UserRepository userRepo,
                       UserProfileService profileService,
                       LogService logService,
                       RestTemplate restTemplate) {
        this.userRepo = userRepo;
        this.profileService = profileService;
        this.logService = logService;
        this.restTemplate = restTemplate;
    }

    /**
     * adds a new user to the database.
     *
     * @param userAuth user authentication details
     * @return the profile of the new user
     */
    public UserProfile createAccount(UserAuth userAuth) {
        String username = userAuth.getUsername();

        //first save profile to database(with username and id), so we can save the user
        UserProfile userProfile = new UserProfile(username);
        userProfile.setFollowing(new ArrayList<>());
        userProfile.setFollowers(new ArrayList<>());
        userProfile.setFriends(new ArrayList<>());
        UserProfile savedProfile = profileService.save(userProfile);

        String email = userAuth.getEmail();
        String password = userAuth.getPassword();
        User createdUser = new User(email, password);
        //add the user to the database
        //make sure they have the same ids for simplicity
        createdUser.setUserId(savedProfile.getUserId());
        userRepo.save(createdUser);

        // Example log entry
        logService.recordActivity(savedProfile, "created their account");

        return savedProfile;
    }

    /**
     * validates authentication data.
     *
     * @param userAuth user authentication details
     * @return true iff data is valid, false otherwise
     */
    public boolean validateAccountData(UserAuth userAuth) {
        String email = userAuth.getEmail();
        String password = userAuth.getPassword();
        String username = userAuth.getUsername();

        return !InputCheck.isNullOrEmpty(email) && !InputCheck.isNullOrEmpty(password)
            && InputCheck.isValidUsername(username);
    }

    /**
     * validates data used to update user account.
     *
     * @param email    new email of user
     * @param password new password of user
     * @return true iff data is valid, false otherwise
     */
    public boolean validateNewData(String email, String password) {
        return !InputCheck.isNullOrEmpty(email) && !InputCheck.isNullOrEmpty(password);
    }

    /**
     * checks if user with this email exists in database.
     *
     * @param email email of user we are searching by
     * @return true if there is a user with this email, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    /**
     * retrieves user by its id.
     *
     * @param userId id – must not be null
     * @return the entity with the given id or Optional#empty() if none found.
     */
    public Optional<User> findById(UUID userId) {
        return userRepo.findById(userId);
    }

    /**
     * Unfollow all users, which a user is following.
     *
     * @param profile the profile of the user which is unfollowing.
     */
    private void unfollowUsers(UserProfile profile) {
        if (profile.getFollowing() != null) {
            List<UserProfile> usersToUnfollow = new ArrayList<>();

            for (UUID followingUserId : profile.getFollowing()) {
                Optional<UserProfile> followingUserOptional = profileService.findById(followingUserId);

                if (followingUserOptional.isPresent()) {
                    UserProfile followingUser = followingUserOptional.get();
                    usersToUnfollow.add(followingUser);
                }
            }

            //removing users outside the loop to avoid ConcurrentModificationException
            for (UserProfile followingUser : usersToUnfollow) {
                followingUser.getFollowers().remove(profile.getUserId());
                FollowService.unfollowUser(profile, followingUser);
                profileService.save(followingUser);
            }
        }
    }

    /**
     * Remove the followers of a user.
     *
     * @param profile the user of which the followers need to be removed.
     */
    private void removeFollowers(UserProfile profile) {
        if (profile.getFollowers() != null) {
            List<UserProfile> usersToUnfollow = new ArrayList<>();

            for (UUID followerUserId : profile.getFollowers()) {
                Optional<UserProfile> followerUserOptional = profileService.findById(followerUserId);

                if (followerUserOptional.isPresent()) {
                    UserProfile followerUser = followerUserOptional.get();
                    usersToUnfollow.add(followerUser);
                }
            }

            //removing users outside the loop to avoid ConcurrentModificationException
            for (UserProfile followerUser : usersToUnfollow) {
                followerUser.getFollowing().remove(profile.getUserId());
                FollowService.unfollowUser(followerUser, profile);
                profileService.save(followerUser);
            }
        }
    }

    /**
     * deletes user by its id.
     *
     * @param userId id of the user we want to delete
     */
    public void deleteAccount(UUID userId) {
        User user = this.findById(userId).get();
        UserProfile profile = profileService.findById(userId).get();
        logService.recordActivity(profile, "deleted their account");

        //this user is in the database, we can delete it
        userRepo.delete(user);

        unfollowUsers(profile);
        removeFollowers(profile);

        //should also go through "following" list and, for each followedUser, remove this user from the list of
        // "followers" of followedUser, and from its list of friends, if necessary

        //when deleting profile, we should delete the bookshelves associated with the profile
        String bookshelvesServiceUrl = "http://localhost:8080/bookshelves";
        String bookshelvesUrl = bookshelvesServiceUrl + "/bookshelf/{userId}/removeAll/requestBy/{requesterId}";
        try {
            restTemplate.delete(bookshelvesUrl, userId, userId, Void.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Bookshelf microservice cannot be found.");
        }

        //delete actual profile
        profileService.deleteById(userId);
    }

    /**
     * view the account of a user by its id.
     *
     * @param userId id of user we want to view
     * @return the account details of the user
     */
    public UserWithoutPassword viewAccount(UUID userId) {
        User user = this.findById(userId).get();
        UserProxy userProxy = new UserProxy(user);
        return userProxy.viewAccount();
    }

    /**
     * checks if there is a user with the same email.
     *
     * @param userId id of current user that want to be modified
     * @param user   changed user details
     * @return true iff another user with the same email exists, false otherwise
     */
    public boolean existsUserSameEmail(UUID userId, User user) {
        User existingUser = this.findById(userId).get();
        if (!existingUser.getEmail().equals(user.getEmail())) {
            //different emails => the email is updated
            //check that there is no user with this email already.
            String newEmail = user.getEmail();
            //this email is already assigned to another user, not allowed to change it to this
            return userRepo.existsByEmail(newEmail);
        }
        return false;
    }

    /**
     * modify the account details of the user.
     *
     * @param user new details of user that we want to save
     * @return the details of the saved user account
     */
    public UserWithoutPassword modifyAccount(User user) {
        //there is no user with this email, we can update the email and other fields
        userRepo.save(user);
        UserProxy proxy = new UserProxy(user);
        return proxy.viewAccount();
    }

    /**
     * Find a User by email.
     *
     * @param email the email
     * @return the User with said email
     */
    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }
}
