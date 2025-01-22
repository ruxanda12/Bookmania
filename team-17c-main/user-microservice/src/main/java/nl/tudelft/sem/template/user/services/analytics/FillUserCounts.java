package nl.tudelft.sem.template.user.services.analytics;

import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserAnalytics;
import nl.tudelft.sem.template.user.model.UserProfile;

public class FillUserCounts implements UserAnalyticsHandler {
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
     * Fill the userCount, activeUsers and authorCount attribute of UserAnalytics.
     *
     * @param analytics   UserAnalytics object
     * @param profileRepo ProfileRepository
     * @param logRepo     LogItemRepository
     * @return UserAnalytics object with newly filled attributes
     */
    public UserAnalytics handle(UserAnalytics analytics, UserProfileRepository profileRepo, LogItemRepository logRepo) {
        // Simple queries, set userCount, activeUsers and authorCount
        // Might need to change OpenAPI so these attributes are longs by default
        analytics.setUserCount((int) profileRepo.count());
        analytics.setActiveUsers((int) profileRepo.countUserProfileByState(UserProfile.StateEnum.ACTIVE));
        analytics.setAuthorCount((int) profileRepo.countUserProfileByRole(UserProfile.RoleEnum.AUTHOR));

        if (nextHandler != null) {
            return nextHandler.handle(analytics, profileRepo, logRepo);
        } else {
            return analytics;
        }
    }
}
