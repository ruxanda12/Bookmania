package nl.tudelft.sem.template.user.services.analytics;

import java.util.stream.Collectors;
import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserAnalytics;
import nl.tudelft.sem.template.user.models.LogItem;

public class FillUserLog implements UserAnalyticsHandler {
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
     * Fill the userLog attribute of UserAnalytics.
     *
     * @param analytics   UserAnalytics object
     * @param profileRepo ProfileRepository
     * @param logRepo     LogItemRepository
     * @return UserAnalytics object with newly filled attribute
     */
    public UserAnalytics handle(UserAnalytics analytics, UserProfileRepository profileRepo, LogItemRepository logRepo) {
        analytics.setUserLog(logRepo.findTop10ByOrderByTimestampDesc().stream()
            .map(LogItem::toString).collect(Collectors.toList()));

        if (nextHandler != null) {
            return nextHandler.handle(analytics, profileRepo, logRepo);
        } else {
            return analytics;
        }
    }
}
