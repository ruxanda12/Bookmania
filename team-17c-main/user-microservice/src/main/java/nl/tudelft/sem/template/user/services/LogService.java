package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.models.LogItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * See the LogItem model for the different constructors.
 * This service is here to provide flexibility in recording log entries.
 */
@Service
public class LogService {
    private final LogItemRepository logRepo;

    @Autowired
    public LogService(LogItemRepository logRepo) {
        this.logRepo = logRepo;
    }

    /**
     * Record log entry.
     *
     * @param activity activity
     */
    public void recordActivity(String activity) {
        logRepo.save(new LogItem(activity));
    }

    /**
     * Record log entry.
     *
     * @param user     user performing action
     * @param activity activity
     */
    public void recordActivity(UserProfile user, String activity) {
        logRepo.save(new LogItem(user, activity));
    }

    /**
     * Record log entry.
     *
     * @param user1    user performing action
     * @param activity activity
     * @param user2    user which action is being performed on
     */
    public void recordActivity(UserProfile user1, String activity, UserProfile user2) {
        logRepo.save(new LogItem(user1, activity, user2));
    }
}
