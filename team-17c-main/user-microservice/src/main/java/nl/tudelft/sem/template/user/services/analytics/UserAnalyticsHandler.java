package nl.tudelft.sem.template.user.services.analytics;

import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserAnalytics;

// The handler interface, this defines the methods we need for the chain
public interface UserAnalyticsHandler {
    void setNext(UserAnalyticsHandler handler);

    UserAnalytics handle(UserAnalytics analytics, UserProfileRepository profileRepo, LogItemRepository logRepo);
}
