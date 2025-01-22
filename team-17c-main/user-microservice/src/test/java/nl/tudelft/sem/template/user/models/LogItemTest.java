package nl.tudelft.sem.template.user.models;

import nl.tudelft.sem.template.user.model.UserProfile;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LogItemTest {
    UserProfile user1 = new UserProfile("user1");
    UserProfile user2 = new UserProfile("user2");
    UserProfile user3 = new UserProfile("user1");

    private static final String INVALID_ENTRY = "Invalid Log Entry";

    @Test
    public void testConstructor() {
        assertEquals((new LogItem("")).getLogEntry(), INVALID_ENTRY);
        assertEquals((new LogItem(null)).getLogEntry(), INVALID_ENTRY);

        assertNull(new LogItem().getLogEntry());
        assertEquals((new LogItem("System booted")).getLogEntry(), "System booted");
        assertEquals((new LogItem(user1, "ate an apple")).getLogEntry(), "user1 ate an apple");
        assertEquals((new LogItem(user1, "")).getLogEntry(), INVALID_ENTRY);

        assertEquals((new LogItem(user1, "ate an apple with", user2)).getLogEntry(), "user1 ate an apple with user2");

        // same usernames will return equal logItems, no work-around for this.
        // Only solution is storing the user_id's but that's redundant and unnecessary
        assertEquals((new LogItem(user3, "ate an apple with", user2)).getLogEntry(), "user1 ate an apple with user2");

        assertEquals((new LogItem(user1, "ate an apple with", null)).getLogEntry(), INVALID_ENTRY);
        assertEquals((new LogItem(null, "ate an apple with", user2)).getLogEntry(), INVALID_ENTRY);
    }

    @Test
    public void testEquality() {
        LogItem log1 = new LogItem(user1, "ate an apple with", user2);
        LogItem log2 = new LogItem("user1 ate an apple with user2");
        log2.setTimestamp(log1.getTimestamp().plusMinutes(1));

        assertNotEquals(log1, log2);
        assertEquals(log1.getLogEntry(), log2.getLogEntry());
        assertNotEquals(log1.hashCode(), log2.hashCode());

        log1.setTimestamp(LocalDateTime.now());
        assertTrue(log1.equals(log1));
        assertNotEquals(log1, null);
        assertNotEquals(log1, "String");

        LogItem log3 = new LogItem(user1, "ate an apple with", user2);
        log3.setTimestamp(LocalDateTime.now());
        log1.setTimestamp(log3.getTimestamp());
        log3.setUuid(new UUID(0, 1));
        log1.setUuid(new UUID(1, 2));

        assertNotEquals(log1, log3);
    }

    @Test
    public void testEqualityWithDate() {
        LogItem log1 = new LogItem(user1, "ate an apple with", user2);
        log1.setTimestamp(LocalDateTime.now());

        LogItem log2 = new LogItem(user1, "ate an apple with", user2);
        log2.setTimestamp(LocalDateTime.now().plusMinutes(1));

        assertNotEquals(log1, log2);
        assertNotEquals(log1.hashCode(), log2.hashCode());

        // time of when I made this test
        LocalDateTime now = LocalDateTime.of(2023, 12, 15, 15, 50, 58, 123);
        LogItem log3 = new LogItem(user1, "ate an apple with", user2);
        log3.setTimestamp(now);
        log2.setTimestamp(now);

        assertEquals(log2, log3);
        assertEquals(log2.hashCode(), log3.hashCode());
        assertEquals("December 15, 2023, 15:50 | user1 ate an apple with user2", log2.toString());
    }
}
