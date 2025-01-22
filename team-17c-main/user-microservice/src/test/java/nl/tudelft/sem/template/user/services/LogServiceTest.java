package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.models.LogItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LogServiceTest {

    LogService logService;

    @Mock
    LogItemRepository logRepo;

    UserProfile user1 = new UserProfile("user1");
    UserProfile user2 = new UserProfile("user2");

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        logService = new LogService(logRepo);
    }

    @Test
    void testRecordActivities1(){
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);

        ArgumentCaptor<LogItem> argumentCaptor = ArgumentCaptor.forClass(LogItem.class);
        logService.recordActivity("This is a test");

        LocalDateTime then = LocalDateTime.now().plusMinutes(1);

        verify(logRepo, times(1)).save(argumentCaptor.capture());

        LogItem logItem = argumentCaptor.getValue();
        assertTrue(logItem.getTimestamp().isAfter(now));
        assertTrue(logItem.getTimestamp().isBefore(then));
        assertEquals("This is a test", argumentCaptor.getValue().getLogEntry());
    }

    @Test
    void testRecordActivities2(){
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);

        ArgumentCaptor<LogItem> argumentCaptor = ArgumentCaptor.forClass(LogItem.class);
        logService.recordActivity(user1, "tested alone");

        LocalDateTime then = LocalDateTime.now().plusMinutes(1);

        verify(logRepo, times(1)).save(argumentCaptor.capture());

        LogItem logItem = argumentCaptor.getValue();
        assertTrue(logItem.getTimestamp().isAfter(now));
        assertTrue(logItem.getTimestamp().isBefore(then));
        assertEquals("user1 tested alone", argumentCaptor.getValue().getLogEntry());
    }

    @Test
    void testRecordActivities3(){
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);

        ArgumentCaptor<LogItem> argumentCaptor = ArgumentCaptor.forClass(LogItem.class);
        logService.recordActivity(user1, "tested on", user2);

        LocalDateTime then = LocalDateTime.now().plusMinutes(1);

        verify(logRepo, times(1)).save(argumentCaptor.capture());

        LogItem logItem = argumentCaptor.getValue();
        assertTrue(logItem.getTimestamp().isAfter(now));
        assertTrue(logItem.getTimestamp().isBefore(then));
        assertEquals("user1 tested on user2", argumentCaptor.getValue().getLogEntry());
    }
}
