package nl.tudelft.sem.template.user.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.services.InputCheck;


/**
 * LogItems will be stored in a JPA Repo, id will be timestamps allowing us
 * to easily implement a log-based analytics system. Spring supports sorts based on id's.
 */
@Getter
@Setter
@Entity
public class LogItem {
    private static final String INVALID_ENTRY = "Invalid Log Entry";
    @Id
    @GeneratedValue
    private UUID uuid;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String logEntry;

    /**
     * Default constructor.
     */
    public LogItem() {
        // Ignore this
    }

    /**
     * Constructor, specifying a custom action.
     *
     * @param action arbitrary action
     */
    public LogItem(String action) {
        if (InputCheck.isNullOrEmpty(action)) {
            this.logEntry = INVALID_ENTRY;
        } else {
            this.logEntry = action;
        }
        // Examples:
        // System start-up
        // User-count reached 100
    }

    /**
     * Constructor, specifying 2 users and an action.
     *
     * @param user1  user performing the action
     * @param action arbitrary action regarding 2 users
     * @param user2  user which the action is performed on
     */
    public LogItem(UserProfile user1, String action, UserProfile user2) {
        if (user1 == null || InputCheck.isNullOrEmpty(action) || user2 == null) {
            this.logEntry = INVALID_ENTRY;
        } else {
            this.logEntry = user1.getUsername() + " " + action + " " + user2.getUsername();
        }
        // Examples:
        // User26626 banned User727737237
        // User82823737 viewed User821010
    }

    /**
     * Constructor, specifying a user and an action.
     *
     * @param user   user performing the action
     * @param action arbitrary action
     */
    public LogItem(UserProfile user, String action) {
        if (user == null || InputCheck.isNullOrEmpty(action)) {
            this.logEntry = INVALID_ENTRY;
        } else {
            this.logEntry = user.getUsername() + " " + action;
        }
        // Examples:
        // User764 modified their account
        // User8910 created their account
        // User818190 became an author
    }

    /**
     * toString method, making a nice format to represent the log entry.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy, HH:mm");
        // Example output: October 21, 2023 15:23 | user626 banned user882
        return timestamp.format(formatter) + " | " + logEntry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LogItem logItem = (LogItem) o;
        return Objects.equals(uuid, logItem.uuid) && Objects.equals(timestamp, logItem.timestamp)
            && Objects.equals(logEntry, logItem.logEntry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, timestamp, logEntry);
    }
}
