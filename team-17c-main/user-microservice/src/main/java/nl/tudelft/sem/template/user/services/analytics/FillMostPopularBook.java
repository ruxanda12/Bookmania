package nl.tudelft.sem.template.user.services.analytics;

import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserAnalytics;
import nl.tudelft.sem.template.user.services.QueryStringUtils;

public class FillMostPopularBook implements UserAnalyticsHandler {
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
     * Fill the mostPopularBook attribute of UserAnalytics.
     *
     * @param analytics   UserAnalytics object
     * @param profileRepo ProfileRepository
     * @param logRepo     LogItemRepository
     * @return UserAnalytics object with newly filled attribute
     */
    public UserAnalytics handle(UserAnalytics analytics, UserProfileRepository profileRepo, LogItemRepository logRepo) {
        // Take the most popular books, take the first entry and put this in analytics
        // If empty -> no book as favorite
        String[] mostPopularBooks = profileRepo.findMostPopularBooks();
        if (mostPopularBooks.length == 0) {
            analytics.setMostPopularBook("There is no most popular book");
        } else {
            analytics.setMostPopularBook(QueryStringUtils.modifyString(mostPopularBooks[0], "favorites"));
        }

        if (nextHandler != null) {
            return nextHandler.handle(analytics, profileRepo, logRepo);
        } else {
            return analytics;
        }
    }
}
