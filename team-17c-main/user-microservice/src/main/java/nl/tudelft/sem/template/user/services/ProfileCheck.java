package nl.tudelft.sem.template.user.services;

import java.util.UUID;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.springframework.stereotype.Service;

/**
 * Put anything related to profiles, which may be used across different classes.
 */
@Service
public class ProfileCheck {
    /**
     * Check whether a user is an admin.
     *
     * @param profile profile of user
     * @return boolean
     */
    public static boolean isAdmin(UserProfile profile) {
        if (profile == null) {
            return false;
        }
        return profile.getRole() == UserProfile.RoleEnum.ADMIN;
    }

    /**
     * Check whether a user is an author.
     *
     * @param profile profile of user
     * @return boolean
     */
    public static boolean isAuthor(UserProfile profile) {
        if (profile == null) {
            return false;
        }
        return profile.getRole() == UserProfile.RoleEnum.AUTHOR;
    }

    /**
     * Check whether user is active (active = unbanned too).
     *
     * @param profile profile of user
     * @return boolean
     */
    public static boolean isActive(UserProfile profile) {
        if (profile == null) {
            return false;
        }
        return profile.getState() == UserProfile.StateEnum.ACTIVE;
    }

    /**
     * Get the user profile by id.
     *
     * @param profileRepo UserProfile repository
     * @param userId      user id of profile
     * @return UserProfile or null
     */
    public static UserProfile getUserProfile(UserProfileRepository profileRepo, UUID userId) {
        if (userId == null) {
            return null;
        }
        return profileRepo.findById(userId).orElse(null);
    }
}
