package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.models.LogItem;
import nl.tudelft.sem.template.user.model.UserAnalytics;
import nl.tudelft.sem.template.user.services.analytics.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserAnalyticsFillerTest {

    @Mock
    UserProfileRepository profileRepo;
    @Mock
    LogItemRepository logRepo;

    String[] defaultBooks = new String[]{"Harry Potter,100", "LOTR,28", "HUH!,20"};

    String[] defaultUsers = new String[]{(new UUID(0, 0)).toString()+",10",
            (new UUID(1, 1)).toString()+",5",
            (new UUID(2, 2)).toString()+",1"};

    String[] defaultGenres = new String[]{"Genre1,3", "Genre2,2", "Genre3,1"};

    List<LogItem> defaultLogs = List.of(
            new LogItem("log1"),
            new LogItem("log2"),
            new LogItem("log3"),
            new LogItem("log4"),
            new LogItem("log5"),
            new LogItem("log6"),
            new LogItem("log7"),
            new LogItem("log8"),
            new LogItem("log9"),
            new LogItem("log10")
    );

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        // SETUP DEFAULT MOCKS
        // 58 users, 50 active, 10 authors
        when(profileRepo.count()).thenReturn(58L);
        when(profileRepo.countUserProfileByState(UserProfile.StateEnum.ACTIVE)).thenReturn(50L);
        when(profileRepo.countUserProfileByRole(UserProfile.RoleEnum.AUTHOR)).thenReturn(10L);

        when(profileRepo.findMostPopularBooks()).thenReturn(defaultBooks);
        when(profileRepo.findMostPopularGenres()).thenReturn(defaultGenres);
        when(profileRepo.findMostFollowedUsers()).thenReturn(defaultUsers);

        UserProfile profile1 = new UserProfile("profile1");
        UserProfile profile2 = new UserProfile("profile2");
        when(profileRepo.findById(new UUID(0, 0))).thenReturn(Optional.of(profile1));
        when(profileRepo.findById(new UUID(1, 1))).thenReturn(Optional.of(profile2));
        when(profileRepo.findById(new UUID(2, 2))).thenReturn(Optional.empty());

        when(logRepo.findTop10ByOrderByTimestampDesc()).thenReturn(defaultLogs);

        // TODO mock for followed users
    }

    @Test
    void testRegular(){
        UserAnalytics analytics = UserAnalyticsFiller.getAnalytics(profileRepo, logRepo);

        assertEquals(58, analytics.getUserCount());
        assertEquals(50, analytics.getActiveUsers());
        assertEquals(10, analytics.getAuthorCount());

        assertEquals(QueryStringUtils.modifyString("Harry Potter,100", "favorites"),
                analytics.getMostPopularBook());

        assertEquals(Arrays.stream(new String[]{"Genre1,3", "Genre2,2", "Genre3,1"})
                        .map(x -> QueryStringUtils.modifyString(x, "favorites"))
                        .collect(Collectors.toList()),
                analytics.getMostPopularGenres());

        assertEquals(List.of("profile1 - 10 followers", "profile2 - 5 followers", "ERROR"), analytics.getMostFollowedUsers());

        assertEquals(defaultLogs.stream().map(LogItem::toString).collect(Collectors.toList()),
                analytics.getUserLog());
    }

    @Test
    void testNoBooks(){
        String[] books = new String[0];
        when(profileRepo.findMostPopularBooks()).thenReturn(books);

        UserAnalytics analytics = UserAnalyticsFiller.getAnalytics(profileRepo, logRepo);
        assertEquals("There is no most popular book", analytics.getMostPopularBook());
    }

    @Test
    void testNoGenres(){
        String[] genres = new String[0];
        when(profileRepo.findMostPopularGenres()).thenReturn(genres);

        UserAnalytics analytics = UserAnalyticsFiller.getAnalytics(profileRepo, logRepo);
        assertEquals(List.of(), analytics.getMostPopularGenres());
    }

    @Test
    void tooManyGenres(){
        String[] genres = new String[]{"Genre1,4", "Genre2,3", "Genre3,2", "Genre4,1"};
        when(profileRepo.findMostPopularGenres()).thenReturn(genres);

        UserAnalytics analytics = UserAnalyticsFiller.getAnalytics(profileRepo, logRepo);
        assertEquals(Arrays.stream(new String[]{"Genre1,4", "Genre2,3", "Genre3,2"})
                .map(x -> QueryStringUtils.modifyString(x, "favorites"))
                .collect(Collectors.toList()), analytics.getMostPopularGenres());
    }

    @Test
    void testPopualarUsers(){
        FillMostPopularUsers usersFiller = new FillMostPopularUsers();
        UserAnalytics analytics = usersFiller.handle(new UserAnalytics(), profileRepo, logRepo);

        assertEquals(List.of("profile1 - 10 followers", "profile2 - 5 followers", "ERROR"), analytics.getMostFollowedUsers());
    }
    @Test
    void testHandlers(){
        FillUserCounts userCountsFiller = new FillUserCounts();
        FillMostPopularUsers usersFiller = new FillMostPopularUsers();
        FillUserLog logFiller = new FillUserLog();
        FillMostPopularBook bookFiller = new FillMostPopularBook();
        FillMostPopularGenres genresFiller = new FillMostPopularGenres();

        // for every handler, test the next(), and test independently, UserAnalyticsFiller already
        // handlers next() for all except for genres.
        // Genres is also only handler which does return analytics alone.

        UserAnalytics counts = userCountsFiller.handle(new UserAnalytics(), profileRepo, logRepo);
        assertEquals(counts.getUserCount(), 58);
        assertEquals(counts.getActiveUsers(), 50);
        assertEquals(counts.getAuthorCount(), 10);

        UserAnalytics popularUsers = usersFiller.handle(new UserAnalytics(), profileRepo, logRepo);
        // MostPopularUsers is tested elsewhere

        UserAnalytics log = logFiller.handle(new UserAnalytics(), profileRepo, logRepo);
        assertEquals(defaultLogs.stream().map(LogItem::toString).collect(Collectors.toList()), log.getUserLog());

        genresFiller.setNext(bookFiller);
        UserAnalytics genreAndBook = genresFiller.handle(new UserAnalytics(), profileRepo, logRepo);

        assertEquals(QueryStringUtils.modifyString("Harry Potter,100", "favorites"),
                genreAndBook.getMostPopularBook());

        assertEquals(Arrays.stream(new String[]{"Genre1,3", "Genre2,2", "Genre3,1"})
                        .map(x -> QueryStringUtils.modifyString(x, "favorites"))
                        .collect(Collectors.toList()),
                genreAndBook.getMostPopularGenres());
    }
}
