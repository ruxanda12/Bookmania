package nl.tudelft.sem.template.user.services.analytics;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserAnalytics;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.services.ProfileCheck;

public class FillMostPopularUsers implements UserAnalyticsHandler {
    private UserAnalyticsHandler nextHandler;

    /**
     * Set a next handler.
     *
     * @param handler next handler
     */
    public void setNext(UserAnalyticsHandler handler) {
        nextHandler = handler;
    }

    /**
     * Fill the mostFollowedUsers (not yet implemented) attribute of UserAnalytics.
     *
     * @param analytics   UserAnalytics object
     * @param profileRepo ProfileRepository
     * @param logRepo     LogItemRepository
     * @return UserAnalytics object with newly filled attribute
     */
    public UserAnalytics handle(UserAnalytics analytics, UserProfileRepository profileRepo, LogItemRepository logRepo) {
        // Take the most popular users, capped at 3 as their usernames.
        // Add it to analytics
        String[] mostFollowedUsers = profileRepo.findMostFollowedUsers();
        int lenFollowers = Math.min(mostFollowedUsers.length, 3);
        analytics.setMostFollowedUsers(Arrays.stream(Arrays.copyOf(mostFollowedUsers, lenFollowers))
            .map(x -> {
                UUID id = UUID.fromString(x.split(",")[0]);
                UserProfile user = ProfileCheck.getUserProfile(profileRepo, id);
                if (user == null) {
                    return "ERROR";
                }
                return user.getUsername() + " - " + x.split(",")[1] + " followers";
            })
            .collect(Collectors.toList()));
        if (nextHandler != null) {
            return nextHandler.handle(analytics, profileRepo, logRepo);
        } else {
            return analytics;
        }
    }
}
