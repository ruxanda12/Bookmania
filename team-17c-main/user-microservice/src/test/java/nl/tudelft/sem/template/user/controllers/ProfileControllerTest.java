package nl.tudelft.sem.template.user.controllers;

import nl.tudelft.sem.template.user.database.AdminKeyRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.services.FollowService;
import nl.tudelft.sem.template.user.services.LogService;
import nl.tudelft.sem.template.user.services.UserProfileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

public class ProfileControllerTest {
    @Mock
    private UserProfileRepository profileRepo;
    @Mock
    private AdminKeyRepository adminKeyRepo;

    @Mock
    private UserProfileService profileService;
    @Mock
    private LogService logService;

    @Mock RestTemplate restTemplate;
    @InjectMocks
    private ProfileController controller;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach()
    void cleanUp() {
        try {
            closeable.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void getOwnProfileTest() {
        UUID uuid1 = UUID.randomUUID();
        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);

        UUID uuid2 = UUID.randomUUID();
        UserProfile testProfile2 = new UserProfile("username2");
        testProfile2.setUserId(uuid2);

        when(profileRepo.findById(uuid1)).thenReturn(Optional.of(testProfile1));
        when(profileRepo.findById(uuid2)).thenReturn(Optional.of(testProfile2));

        ResponseEntity<UserProfile> response1 = controller.getOwnProfile(uuid1);
        ResponseEntity<UserProfile> response2 = controller.getOwnProfile(uuid2);

        verify(profileRepo, times(1)).findById(uuid1);
        verify(profileRepo, times(1)).findById(uuid2);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(testProfile1, response1.getBody());

        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(testProfile2, response2.getBody());
    }

    @Test
    void getOwnProfileInvalidUUIDTest() {
        UUID uuid1 = UUID.randomUUID();
        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);

        UUID uuid2 = UUID.randomUUID();
        UserProfile testProfile2 = new UserProfile("username2");
        testProfile2.setUserId(uuid2);

        UUID invalidUUID = UUID.randomUUID();

        when(profileRepo.findById(uuid1)).thenReturn(Optional.of(testProfile1));
        when(profileRepo.findById(uuid2)).thenReturn(Optional.of(testProfile2));

        ResponseEntity<UserProfile> response1 = controller.getOwnProfile(uuid1);
        ResponseEntity<UserProfile> response2 = controller.getOwnProfile(invalidUUID);

        verify(profileRepo, times(1)).findById(uuid1);
        verify(profileRepo, times(1)).findById(invalidUUID);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

        assertEquals(testProfile1, response1.getBody());
        assertNull(response2.getBody());
    }

    @Test
    void updateOwnProfileTest() {
        UUID uuid1 = UUID.randomUUID();

        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);

        UserProfile updateProfile1 = new UserProfile("username1");
        updateProfile1.setUserId(uuid1);
        updateProfile1.setBio("Updated Bio");
        updateProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);

        when(profileRepo.findById(uuid1)).thenReturn(Optional.of(testProfile1));

        ResponseEntity<Void> response1 = controller.updateOwnProfile(uuid1, updateProfile1);
        verify(profileRepo, times(1)).findById(uuid1);
        verify(profileRepo, times(1)).save(updateProfile1);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
    }

    @Test
    void updateOwnProfileInvalidUUIDTest() {
        UUID uuid1 = UUID.randomUUID();
        UUID invalidUUID = UUID.randomUUID();

        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);

        UserProfile updateProfile1 = new UserProfile("username1");
        updateProfile1.setUserId(uuid1);
        updateProfile1.setBio("Updated Bio");
        updateProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);

        when(profileRepo.findById(uuid1)).thenReturn(Optional.of(testProfile1));

        ResponseEntity<Void> response1 = controller.updateOwnProfile(invalidUUID, updateProfile1);
        verify(profileRepo, times(1)).findById(invalidUUID);
        verify(profileRepo, never()).save(any(UserProfile.class));

        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
    }

    @Test
    void viewOtherProfilePublicTest() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);

        UserProfile testProfile2 = new UserProfile("username1");
        testProfile2.setUserId(uuid2);
        testProfile2.setBio("Test Bio 2");
        testProfile2.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);

        when(profileRepo.findById(uuid1)).thenReturn(Optional.of(testProfile1));
        when(profileRepo.findById(uuid2)).thenReturn(Optional.of(testProfile2));

        ResponseEntity<UserProfile> response1 = controller.viewProfile(uuid1);
        ResponseEntity<UserProfile> response2 = controller.viewProfile(uuid2);
        verify(profileRepo, times(1)).findById(uuid1);
        verify(profileRepo, times(1)).findById(uuid2);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        assertEquals(testProfile1, response1.getBody());
        assertEquals(testProfile2, response2.getBody());
    }

    @Test
    void viewOtherProfilePrivateTest() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);

        UserProfile testProfile2 = new UserProfile("username1");
        testProfile2.setUserId(uuid2);
        testProfile2.setBio("Test Bio 2");
        testProfile2.setPrivacy(UserProfile.PrivacyEnum.PRIVATE);

        when(profileRepo.findById(uuid1)).thenReturn(Optional.of(testProfile1));
        when(profileRepo.findById(uuid2)).thenReturn(Optional.of(testProfile2));

        ResponseEntity<UserProfile> response1 = controller.viewProfile(uuid1);
        ResponseEntity<UserProfile> response2 = controller.viewProfile(uuid2);
        verify(profileRepo, times(1)).findById(uuid1);
        verify(profileRepo, times(1)).findById(uuid2);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());

        assertEquals(testProfile1, response1.getBody());
        assertNull(response2.getBody());
    }

    @Test
    void viewOtherProfileInvalidUUIDTest() {
        UUID uuid1 = UUID.randomUUID();
        UUID invalidUUID = UUID.randomUUID();

        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);

        when(profileRepo.findById(uuid1)).thenReturn(Optional.of(testProfile1));

        ResponseEntity<UserProfile> response1 = controller.viewProfile(uuid1);
        ResponseEntity<UserProfile> response2 = controller.viewProfile(invalidUUID);
        verify(profileRepo, times(1)).findById(uuid1);
        verify(profileRepo, times(1)).findById(invalidUUID);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

        assertEquals(testProfile1, response1.getBody());
        assertNull(response2.getBody());
    }

    @Test
    void testFollow(){
        UserProfile validProfile = new UserProfile("validProfile");
        validProfile.setState(UserProfile.StateEnum.ACTIVE);
        validProfile.setRole(UserProfile.RoleEnum.USER);
        validProfile.setUserId(new UUID(1, 1));

        UserProfile validProfile2 = new UserProfile("validProfile");
        validProfile2.setState(UserProfile.StateEnum.ACTIVE);
        validProfile2.setRole(UserProfile.RoleEnum.USER);
        validProfile2.setUserId(new UUID(1, 2));

        UserProfile privateAccount = new UserProfile("privateAccount");
        privateAccount.setState(UserProfile.StateEnum.ACTIVE);
        privateAccount.setRole(UserProfile.RoleEnum.USER);
        privateAccount.setPrivacy(UserProfile.PrivacyEnum.PRIVATE);
        privateAccount.setUserId(new UUID(2, 1));

        UserProfile inactiveProfile = new UserProfile("inactiveProfile");
        inactiveProfile.setState(UserProfile.StateEnum.INACTIVE);
        inactiveProfile.setRole(UserProfile.RoleEnum.USER);
        inactiveProfile.setUserId(new UUID(2, 2));

        when(profileRepo.findById(any())).thenReturn(Optional.empty());
        when(profileRepo.findById(validProfile.getUserId())).thenReturn(Optional.of(validProfile));
        when(profileRepo.findById(inactiveProfile.getUserId())).thenReturn(Optional.of(inactiveProfile));
        when(profileRepo.findById(validProfile2.getUserId())).thenReturn(Optional.of(validProfile2));
        when(profileRepo.findById(privateAccount.getUserId())).thenReturn(Optional.of(privateAccount));

        when(profileRepo.save(validProfile2)).thenReturn(validProfile2);

        assertEquals(HttpStatus.BAD_REQUEST, controller.followUser(new UUID(0, 0), validProfile.getUserId()).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, controller.followUser(validProfile.getUserId(), validProfile.getUserId()).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, controller.followUser(validProfile.getUserId(), new UUID(0, 0)).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, controller.followUser(validProfile.getUserId(), privateAccount.getUserId()).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, controller.followUser(inactiveProfile.getUserId(), validProfile2.getUserId()).getStatusCode());

        ResponseEntity<UserProfile> successResponse = controller.followUser(validProfile.getUserId(), validProfile2.getUserId());
        assertEquals(HttpStatus.OK, successResponse.getStatusCode());
        assertEquals(validProfile2, successResponse.getBody());

        verify(profileRepo, times(1)).save(validProfile);
        verify(profileRepo, times(1)).save(validProfile2);
        verify(logService, times(1)).recordActivity(any(), anyString(), any());
        assertEquals(List.of(validProfile2.getUserId()), validProfile.getFollowing());
    }

    @Test
    void testProveAuthor() {
        UserProfile validProfile = new UserProfile("validProfile");
        validProfile.setState(UserProfile.StateEnum.ACTIVE);
        validProfile.setRole(UserProfile.RoleEnum.USER);
        validProfile.setUserId(new UUID(1, 1));

        UserProfile inactiveProfile = new UserProfile("inactiveProfile");
        inactiveProfile.setState(UserProfile.StateEnum.INACTIVE);
        inactiveProfile.setRole(UserProfile.RoleEnum.USER);
        inactiveProfile.setUserId(new UUID(2, 2));

        UserProfile admin = new UserProfile("admin");
        admin.setState(UserProfile.StateEnum.ACTIVE);
        admin.setRole(UserProfile.RoleEnum.ADMIN);
        admin.setUserId(new UUID(3, 3));

        when(profileRepo.findById(any())).thenReturn(Optional.empty());
        when(profileRepo.findById(validProfile.getUserId())).thenReturn(Optional.of(validProfile));
        when(profileRepo.findById(inactiveProfile.getUserId())).thenReturn(Optional.of(inactiveProfile));
        when(profileRepo.findById(admin.getUserId())).thenReturn(Optional.of(admin));

        String keyPhrase = "Yessir, I am an author indeed.";

        assertEquals(HttpStatus.BAD_REQUEST, controller.proveAuthor(inactiveProfile.getUserId(), keyPhrase).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, controller.proveAuthor(admin.getUserId(), keyPhrase).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, controller.proveAuthor(new UUID(0, 0), keyPhrase).getStatusCode());
        assertEquals(HttpStatus.OK, controller.proveAuthor(validProfile.getUserId(), keyPhrase).getStatusCode());

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(profileRepo).save(captor.capture());
        assertEquals(captor.getValue().getRole(), UserProfile.RoleEnum.AUTHOR);
        verify(logService, times(1)).recordActivity(any(), anyString());

        assertEquals(HttpStatus.UNAUTHORIZED, controller.proveAuthor(validProfile.getUserId(), "not valid").getStatusCode());
    }


    @Test
    void testUnfollow(){
        UserProfile validProfile = new UserProfile("validProfile");
        validProfile.setState(UserProfile.StateEnum.ACTIVE);
        validProfile.setRole(UserProfile.RoleEnum.USER);
        validProfile.setUserId(new UUID(1, 1));

        UserProfile validProfile2 = new UserProfile("validProfile");
        validProfile2.setState(UserProfile.StateEnum.ACTIVE);
        validProfile2.setRole(UserProfile.RoleEnum.USER);
        validProfile2.setUserId(new UUID(1, 2));

        UserProfile inactiveProfile = new UserProfile("inactiveProfile");
        inactiveProfile.setState(UserProfile.StateEnum.INACTIVE);
        inactiveProfile.setRole(UserProfile.RoleEnum.USER);
        inactiveProfile.setUserId(new UUID(2, 2));

        when(profileRepo.findById(any())).thenReturn(Optional.empty());
        when(profileRepo.findById(validProfile.getUserId())).thenReturn(Optional.of(validProfile));
        when(profileRepo.findById(inactiveProfile.getUserId())).thenReturn(Optional.of(inactiveProfile));
        when(profileRepo.findById(validProfile2.getUserId())).thenReturn(Optional.of(validProfile2));

        assertEquals(HttpStatus.BAD_REQUEST, controller.unfollowUser(new UUID(0, 0), validProfile.getUserId()).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, controller.unfollowUser(validProfile.getUserId(), validProfile.getUserId()).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, controller.unfollowUser(validProfile.getUserId(), new UUID(0, 0)).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, controller.unfollowUser(inactiveProfile.getUserId(), validProfile2.getUserId()).getStatusCode());

        when(profileRepo.save(validProfile2)).thenReturn(validProfile2);

        assertEquals(HttpStatus.NOT_FOUND, controller.unfollowUser(validProfile.getUserId(), validProfile2.getUserId()).getStatusCode());
        FollowService.followUser(validProfile, validProfile2);
        ResponseEntity<UserProfile> successResponse = controller.unfollowUser(validProfile.getUserId(), validProfile2.getUserId());
        assertEquals(HttpStatus.OK, successResponse.getStatusCode());
        assertEquals(validProfile2, successResponse.getBody());

        verify(profileRepo, times(1)).save(validProfile);
        verify(profileRepo, times(1)).save(validProfile2);
        verify(logService, times(1)).recordActivity(any(), anyString(), any());
    }

    @Test
    void testProveAdmin() {
        UserProfile validProfile = new UserProfile("validProfile");
        validProfile.setState(UserProfile.StateEnum.ACTIVE);
        validProfile.setRole(UserProfile.RoleEnum.USER);
        validProfile.setUserId(new UUID(1, 1));

        UserProfile inactiveProfile = new UserProfile("inactiveProfile");
        inactiveProfile.setState(UserProfile.StateEnum.INACTIVE);
        inactiveProfile.setRole(UserProfile.RoleEnum.USER);
        inactiveProfile.setUserId(new UUID(2, 2));

        UserProfile admin = new UserProfile("admin");
        admin.setState(UserProfile.StateEnum.ACTIVE);
        admin.setRole(UserProfile.RoleEnum.ADMIN);
        admin.setUserId(new UUID(3, 3));

        when(profileRepo.findById(any())).thenReturn(Optional.empty());
        when(profileRepo.findById(validProfile.getUserId())).thenReturn(Optional.of(validProfile));
        when(profileRepo.findById(inactiveProfile.getUserId())).thenReturn(Optional.of(inactiveProfile));
        when(profileRepo.findById(admin.getUserId())).thenReturn(Optional.of(admin));

        UUID adminKey = new UUID(10, 10);

        when(adminKeyRepo.existsById(any())).thenReturn(false);
        when(adminKeyRepo.existsById(adminKey)).thenReturn(true);

        assertEquals(HttpStatus.BAD_REQUEST, controller.proveAdmin(inactiveProfile.getUserId(), adminKey).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, controller.proveAdmin(new UUID(0, 0), adminKey).getStatusCode());
        assertEquals(HttpStatus.OK, controller.proveAdmin(validProfile.getUserId(), adminKey).getStatusCode());

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(profileRepo).save(captor.capture());
        assertEquals(captor.getValue().getRole(), UserProfile.RoleEnum.ADMIN);

        assertEquals(HttpStatus.OK, controller.proveAdmin(admin.getUserId(), adminKey).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, controller.proveAdmin(validProfile.getUserId(), new UUID(0, 0)).getStatusCode());

        verify(adminKeyRepo, times(2)).existsById(adminKey);
        verify(adminKeyRepo, times(3)).existsById(any());
        verify(adminKeyRepo, times(2)).deleteById(adminKey);
        verify(adminKeyRepo, times(2)).deleteById(any());
        verify(logService, times(2)).recordActivity(any(), anyString());
    }

    @Test
    void testFindNull(){
        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(null);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);

        UserProfile testProfile2 = new UserProfile("username1");
        testProfile2.setUserId(null);
        testProfile2.setBio("Test Bio 2");
        testProfile2.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);

        ResponseEntity<List<UserProfile>> response1 = controller.findProfiles(testProfile1);
        ResponseEntity<List<UserProfile>> response2 = controller.findProfiles(testProfile2);

        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    void testFindEmpty(){
        UUID uuid1 = UUID.randomUUID();

        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile1.setRole(UserProfile.RoleEnum.fromValue(0));

        when(profileRepo.findAll()).thenReturn(List.of());

        ResponseEntity<List<UserProfile>> response = controller.findProfiles(testProfile1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testFindGood(){
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        UUID uuid3 = UUID.randomUUID();
        UUID uuid4 = UUID.randomUUID();


        UserProfile testProfile1 = new UserProfile("username");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile1.setRole(UserProfile.RoleEnum.fromValue(0));

        UserProfile testProfile2 = new UserProfile("username");
        testProfile2.setUserId(uuid2);
        testProfile2.setBio("Test Bio 2");
        testProfile2.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile2.setRole(UserProfile.RoleEnum.fromValue(0));

        UserProfile testProfile3 = new UserProfile("3");
        testProfile3.setUserId(uuid3);
        testProfile3.setBio("Test Bio 3");
        testProfile3.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile3.setRole(UserProfile.RoleEnum.fromValue(2));

        UserProfile testProfile4 = new UserProfile("username");
        testProfile4.setUserId(uuid4);
        testProfile4.setBio("Test Bio 4");
        testProfile4.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile4.setRole(UserProfile.RoleEnum.fromValue(0));

        when(profileRepo.findAll(profileService.matchProfiles(testProfile1))).thenReturn(List.of(testProfile1, testProfile2, testProfile4));

        ResponseEntity<List<UserProfile>> response = controller.findProfiles(testProfile1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(testProfile1, testProfile2, testProfile4), response.getBody());
    }

    // FAVORITE BOOK TESTS /////////////////////////////////////////////////////////////////////////////////////////////

//    @Test
//    void favoriteBookTest() {
//        UUID uuid = UUID.randomUUID();
//        String bookId = "123";
//        UserProfile userProfile = new UserProfile("username");
//        userProfile.setUserId(uuid);
//
//        // set-up
//        when(restTemplate.getForEntity(anyString(), eq(Void.class), eq(bookId)))
//                .thenReturn(ResponseEntity.ok().build());
//
//        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
//        ProfileController controller = new ProfileController(profileRepo, adminKeyRepo, logService,
//                profileService, restTemplate);
//
//        when(profileRepo.findById(uuid)).thenReturn(Optional.of(userProfile));
//        String bookUrl = "http://localhost:8080/bookshelves/book/{bookId}";
//        server.expect(requestTo(bookUrl))
//                .andExpect(method(org.springframework.http.HttpMethod.GET))
//                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON));
//
//        // verifications
//        ResponseEntity<Void> responseEntity = controller.favoriteBook(uuid, bookId);
//        verify(profileService, times(1)).findById(uuid);
//        verify(profileService, times(1)).save(userProfile);
//        assertEquals(bookId, userProfile.getFavoriteBook());
//        verify(restTemplate, times(1)).getForEntity(eq(bookUrl), eq(Void.class), eq(bookId));
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }

    @Test
    void favoriteBookTest() {
        UUID uuid = UUID.randomUUID();
        String bookId = "123";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // set-up
        when(profileService.bookExists(bookId)).thenReturn(true);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.favoriteBook(uuid, bookId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, times(1)).save(userProfile);
        assertEquals(bookId, userProfile.getFavoriteBook());
        verify(profileService, times(1)).bookExists(bookId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void favoriteBookTest_UserNotFound() {
        UUID uuid = UUID.randomUUID();
        String bookId = "123";

        // set-up
        when(profileService.bookExists(bookId)).thenReturn(true);
        when(profileService.findById(uuid)).thenReturn(Optional.empty());

        // verifications
        ResponseEntity<Void> responseEntity = controller.favoriteBook(uuid, bookId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, never()).bookExists(any());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void favoriteBookTest_InvalidBookId() {
        UUID uuid = UUID.randomUUID();
        String bookId = null;
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // set-up
        when(profileService.bookExists(bookId)).thenReturn(true);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // Verifications
        ResponseEntity<Void> responseEntity = controller.favoriteBook(uuid, bookId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, never()).bookExists(any());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
    @Test
    void favoriteBookTest_BookNotFound() {
        UUID uuid = UUID.randomUUID();
        String bookId = "123";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // set-up
        when(profileService.bookExists(bookId)).thenReturn(false);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.favoriteBook(uuid, bookId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, times(1)).bookExists(bookId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    // UNFAVORITE BOOK TESTS ///////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void unfavoriteBookTest() {
        UUID uuid = UUID.randomUUID();
        String bookId = "123";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);
        userProfile.setFavoriteBook(bookId);

        // set-up
        when(profileService.bookExists(bookId)).thenReturn(true);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteBook(uuid, bookId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, times(1)).save(userProfile);
        assertNull(userProfile.getFavoriteBook());
        verify(profileService, times(1)).bookExists(bookId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void unfavoriteBookTest_UserNotFound() {
        UUID uuid = UUID.randomUUID();
        String bookId = "123";

        // set-up
        when(profileService.bookExists(bookId)).thenReturn(true);
        when(profileRepo.findById(uuid)).thenReturn(Optional.empty());

        // verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteBook(uuid, bookId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, never()).bookExists(any());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void unfavoriteBookTest_InvalidBookId() {
        UUID uuid = UUID.randomUUID();
        String bookId = null;
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);
        userProfile.setFavoriteBook(bookId);

        // set-up
        when(profileService.bookExists(bookId)).thenReturn(true);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // Verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteBook(uuid, bookId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, never()).bookExists(any());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void unfavoriteBookTest_BookNotFound() {
        UUID uuid = UUID.randomUUID();
        String bookId = "123";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);
        userProfile.setFavoriteBook(bookId);

        // set-up
        when(profileService.bookExists(bookId)).thenReturn(false);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteBook(uuid, bookId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, times(1)).bookExists(bookId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void unfavoriteNotFavoriteBookTest() {
        UUID uuid = UUID.randomUUID();
        String bookId = "123";
        String wrongBookId = "banana";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);
        userProfile.setFavoriteBook(wrongBookId);

        // set-up
        when(profileService.bookExists(bookId)).thenReturn(true);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteBook(uuid, bookId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, times(1)).bookExists(bookId);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    // FAVORITE GENRE TESTS ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void favoriteGenreTest() {
        UUID uuid = UUID.randomUUID();
        String genreId = "romance";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = Arrays.asList("romance", "mystery", "fantasy");
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.favoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, times(1)).save(userProfile);
        assertEquals(favoriteGenres, userProfile.getFavoriteGenres());
        verify(profileService, times(1)).availableGenre();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void favoriteGenreTest_AlreadyFavoriteGenre() {
        UUID uuid = UUID.randomUUID();
        String genreId = "romance";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = Arrays.asList("romance", "mystery", "fantasy");
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("romance");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.favoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, times(1)).availableGenre();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void favoriteGenreTest_UserNotFound() {
        UUID uuid = UUID.randomUUID();
        String genreId = "romance";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = Arrays.asList("romance", "mystery", "fantasy");
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.empty());

        // verifications
        ResponseEntity<Void> responseEntity = controller.favoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, never()).availableGenre();
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void favoriteGenreTest_GenreNotFound() {
        UUID uuid = UUID.randomUUID();
        String genreId = "romance";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = Arrays.asList("mystery", "fantasy");
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.favoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, times(1)).availableGenre();
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void favoriteGenreTest_InvalidGenreId() {
        UUID uuid = UUID.randomUUID();
        String genreId = null;
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = Arrays.asList("romance", "mystery", "fantasy");
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.favoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, never()).availableGenre();
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void favoriteGenreTest_BadRequest() {
        UUID uuid = UUID.randomUUID();
        String genreId = "romance";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = new ArrayList<>();
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.favoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, times(1)).availableGenre();
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    // UNFAVORITE GENRE TESTS //////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void unfavoriteGenreTest() {
        UUID uuid = UUID.randomUUID();
        String genreId = "mystery";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = Arrays.asList("romance", "mystery", "fantasy");
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        favoriteGenres.add("romance");
        userProfile.setFavoriteGenres(favoriteGenres);

        List<String> newFavouriteGenre = new ArrayList<>();
        newFavouriteGenre.add("romance");

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, times(1)).save(userProfile);
        assertEquals(newFavouriteGenre, userProfile.getFavoriteGenres());
        verify(profileService, times(1)).availableGenre();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void unfavoriteGenreTest_GenreNotInFavorites() {
        UUID uuid = UUID.randomUUID();
        String genreId = "romance";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = Arrays.asList("romance", "mystery", "fantasy");
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, times(1)).availableGenre();
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void unfavoriteGenreTest_UserNotFound() {
        UUID uuid = UUID.randomUUID();
        String genreId = "mystery";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = Arrays.asList("romance", "mystery", "fantasy");
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.empty());

        // verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, never()).availableGenre();
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void unfavoriteGenreTest_InvalidGenreId() {
        UUID uuid = UUID.randomUUID();
        String genreId = null;
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = Arrays.asList("romance", "mystery", "fantasy");
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, never()).availableGenre();
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void unfavoriteGenreTest_GenreNotFound() {
        UUID uuid = UUID.randomUUID();
        String genreId = "romance";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = Arrays.asList("mystery", "fantasy");
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, times(1)).availableGenre();
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void unfavoriteGenreTest_BadRequest() {
        UUID uuid = UUID.randomUUID();
        String genreId = "mystery";
        UserProfile userProfile = new UserProfile("username");
        userProfile.setUserId(uuid);

        // initialize favorite genre list
        List<String> allGenres = new ArrayList<>();
        List<String> favoriteGenres = new ArrayList<>();
        favoriteGenres.add("mystery");
        favoriteGenres.add("romance");
        userProfile.setFavoriteGenres(favoriteGenres);

        // set-up
        when(profileService.availableGenre()).thenReturn(allGenres);
        when(profileService.findById(uuid)).thenReturn(Optional.of(userProfile));

        // verifications
        ResponseEntity<Void> responseEntity = controller.unfavoriteGenre(uuid, genreId);
        verify(profileService, times(1)).findById(uuid);
        verify(profileService, never()).save(any());
        verify(profileService, times(1)).availableGenre();
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}
