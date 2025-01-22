package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class UserProfileServiceTest {

    @Mock
    private UserProfileRepository profileRepo;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private UserProfileService profileService;

    private final String bookshelvesServiceUrl = "http://localhost:8080/bookshelves";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        UUID userId = UUID.randomUUID();
        UserProfile profile = new UserProfile("user");
        when(profileRepo.findById(userId)).thenReturn(Optional.of(profile));
        Optional<UserProfile> result = profileService.findById(userId);
        verify(profileRepo, times(1)).findById(userId);
        assertTrue(result.isPresent());
        assertEquals(profile, result.get());
    }

    @Test
    void findById_False() {
        UUID userId = UUID.randomUUID();
        when(profileRepo.findById(userId)).thenReturn(Optional.empty());
        Optional<UserProfile> result = profileService.findById(userId);
        verify(profileRepo, times(1)).findById(userId);
        assertTrue(result.isEmpty());
    }

    @Test
    void save() {
        UserProfile profile = new UserProfile("user");
        when(profileRepo.save(profile)).thenReturn(profile);
        UserProfile result = profileService.save(profile);
        verify(profileRepo, times(1)).save(profile);
        assertEquals(profile, result);
    }

    @Test
    void deleteById() {
        UUID userId = UUID.randomUUID();
        doNothing().when(profileRepo).deleteById(userId);
        profileService.deleteById(userId);
        verify(profileRepo, times(1)).deleteById(userId);
    }

    @Test
    void testFindAll(){
        UserProfile profile1 = new UserProfile("user1");
        UserProfile profile2 = new UserProfile("user2");
        UserProfile profile3 = new UserProfile("user3");
        when(profileRepo.findAll()).thenReturn(List.of(profile1, profile2, profile3));
        List<UserProfile> result = profileService.findAll();
        verify(profileRepo, times(1)).findAll();
        assertTrue(result.containsAll(List.of(profile1, profile2, profile3)));
        assertEquals(profile1, result.get(0));
        assertEquals(profile2, result.get(1));
        assertEquals(profile3, result.get(2));
    }

    @Test
    void testMatchProfiles(){
        UserProfile profile1 = new UserProfile("user");
        Example<UserProfile> example = profileService.matchProfiles(profile1);

        assertNotNull(example, "Example should not be null");

        ExampleMatcher matcher = example.getMatcher();
        assertNotNull(matcher, "ExampleMatcher should not be null");

        assertEquals(ExampleMatcher.NullHandler.IGNORE, matcher.getNullHandler(),
                "Null handling should be set to IGNORE");

        assertEquals(ExampleMatcher.StringMatcher.CONTAINING, matcher.getDefaultStringMatcher(),
                "String matching should be set to CONTAINING");

        assertEquals(true, matcher.isIgnoreCaseEnabled(), "Ignore case should be set to true");
    }

    @Test
    void testBookExists() {
        String bookId = "123";
        String bookUrl = bookshelvesServiceUrl + "/book/{bookId}";

        //existent book
        when(restTemplate.getForEntity(eq(bookUrl), eq(Void.class), eq(bookId)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        assertTrue(profileService.bookExists(bookId));

        // non-existent book
        when(restTemplate.getForEntity(eq(bookUrl), eq(Void.class), eq(bookId)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        assertFalse(profileService.bookExists(bookId));
    }

    @Test
    void testAvailableGenre() {
        String genreUrl = bookshelvesServiceUrl + "/genre/all";
        List<String> expectedGenres = List.of("Fiction", "Romance", "Mystery");
        ResponseEntity<String[]> responseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedGenres.toArray(new String[0]));

        // existent genre
        when(restTemplate.getForEntity(eq(genreUrl), eq(String[].class)))
                .thenReturn(responseEntity);

        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(expectedGenres, profileService.availableGenre());

        // non-existent genre
        ResponseEntity<String[]> badResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(restTemplate.getForEntity(eq(genreUrl), eq(String[].class)))
                .thenReturn(badResponse);
        List<String> emptyGenres = profileService.availableGenre();

        assertFalse(badResponse.getStatusCode().is2xxSuccessful());
        assertTrue(emptyGenres.isEmpty(), "The returned list should not be empty");
        assertEquals(new ArrayList<>(), emptyGenres, "The returned list should be an empty ArrayList");
    }
}