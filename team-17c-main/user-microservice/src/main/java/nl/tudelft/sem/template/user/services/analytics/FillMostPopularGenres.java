package nl.tudelft.sem.template.user.services.analytics;

import java.util.Arrays;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserAnalytics;
import nl.tudelft.sem.template.user.services.QueryStringUtils;

public class FillMostPopularGenres implements UserAnalyticsHandler {
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
     * Fill the mostPopularGenres attribute of UserAnalytics.
     *
     * @param analytics   UserAnalytics object
     * @param profileRepo ProfileRepository
     * @param logRepo     LogItemRepository
     * @return UserAnalytics object with newly filled attribute
     */
    public UserAnalytics handle(UserAnalytics analytics, UserProfileRepository profileRepo, LogItemRepository logRepo) {
        // Take the most popular genres, capped at 3.
        // Modify the inputs to readable text in a stream, add it to analytics
        String[] mostPopularGenres = profileRepo.findMostPopularGenres();
        int lenGenres = Math.min(mostPopularGenres.length, 3);
        analytics.setMostPopularGenres(Arrays.stream(Arrays.copyOf(mostPopularGenres, lenGenres))
            .map(x -> QueryStringUtils.modifyString(x, "favorites"))
            .collect(Collectors.toList()));

        if (nextHandler != null) {
            return nextHandler.handle(analytics, profileRepo, logRepo);
        } else {
            return analytics;
        }
    }
}
