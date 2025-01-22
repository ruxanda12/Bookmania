package nl.tudelft.sem.template.user.services.analytics;

import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserAnalytics;
import org.springframework.stereotype.Service;

/**
 * Fills the UserAnalytics object.
 * Further explanations are in comments below
 */
@Service
public class UserAnalyticsFiller {

    /**
     * Fill a new UserAnalytics object through a chain.
     *
     * @param profileRepo UserProfileRepository
     * @param logRepo     LogItemRepository
     * @return filled UserAnalytics object
     */
    public static UserAnalytics getAnalytics(UserProfileRepository profileRepo, LogItemRepository logRepo) {
        // Firstly, create all handlers
        FillUserCounts userCountsFiller = new FillUserCounts();

        FillMostPopularUsers usersFiller = new FillMostPopularUsers();
        userCountsFiller.setNext(usersFiller);

        FillUserLog logFiller = new FillUserLog();
        usersFiller.setNext(logFiller);

        FillMostPopularBook bookFiller = new FillMostPopularBook();
        logFiller.setNext(bookFiller);

        FillMostPopularGenres genresFiller = new FillMostPopularGenres();
        bookFiller.setNext(genresFiller);

        UserAnalytics analytics = new UserAnalytics();

        // 'analytics' will get filled throughout the chain, see below for implementations
        return userCountsFiller.handle(analytics, profileRepo, logRepo);
    }
}

