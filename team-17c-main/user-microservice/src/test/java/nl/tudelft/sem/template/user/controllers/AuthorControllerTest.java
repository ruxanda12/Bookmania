package nl.tudelft.sem.template.user.controllers;

import nl.tudelft.sem.template.user.services.AuthorService;
import nl.tudelft.sem.template.user.services.UserProfileService;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthorControllerTest {

    @Mock
    private UserProfileService profileService;

    @Mock
    private AuthorService authorService;
    @InjectMocks
    private AuthorController authorController;

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
    void testGetRequest() {
        Optional optional = Optional.empty();
        assertEquals(authorController.getRequest(), optional);
    }

    @Test
    void testGetAuthorByIDValid() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile1.setRole(UserProfile.RoleEnum.fromValue(1));

        UserProfile testProfile2 = new UserProfile("username2");
        testProfile2.setUserId(uuid2);
        testProfile2.setBio("Test Bio 2");
        testProfile2.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile2.setRole(UserProfile.RoleEnum.fromValue(1));


        when(profileService.findById(uuid1)).thenReturn(Optional.of(testProfile1));
        when(profileService.findById(uuid2)).thenReturn(Optional.of(testProfile2));

        ResponseEntity<List<UserProfile>> response1 = authorController.getAuthorByID(uuid1);
        ResponseEntity<List<UserProfile>> response2 = authorController.getAuthorByID(uuid2);

        verify(profileService, times(1)).findById(uuid1);
        verify(profileService, times(1)).findById(uuid2);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        assertEquals(List.of(testProfile1), response1.getBody());
        assertEquals(List.of(testProfile2), response2.getBody());
    }
    @Test
    void testGetAuthorByIDDifferentRole() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile1.setRole(UserProfile.RoleEnum.fromValue(0));

        UserProfile testProfile2 = new UserProfile("username2");
        testProfile2.setUserId(uuid2);
        testProfile2.setBio("Test Bio 2");
        testProfile2.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile2.setRole(UserProfile.RoleEnum.fromValue(2));


        when(profileService.findById(uuid1)).thenReturn(Optional.of(testProfile1));
        when(profileService.findById(uuid2)).thenReturn(Optional.of(testProfile2));

        ResponseEntity<List<UserProfile>> response1 = authorController.getAuthorByID(uuid1);
        ResponseEntity<List<UserProfile>> response2 = authorController.getAuthorByID(uuid2);

        verify(profileService, times(1)).findById(uuid1);
        verify(profileService, times(1)).findById(uuid2);

        assertEquals(HttpStatus.NOT_FOUND, response1.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

    @Test
    void testGetAuthorByIDInvalid() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        UUID uuid3 = UUID.randomUUID();
        UUID uuid4 = UUID.randomUUID();

        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile1.setRole(UserProfile.RoleEnum.fromValue(0));

        UserProfile testProfile2 = new UserProfile("username2");
        testProfile2.setUserId(uuid2);
        testProfile2.setBio("Test Bio 2");
        testProfile2.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile2.setRole(UserProfile.RoleEnum.fromValue(2));

        when(profileService.findById(uuid1)).thenReturn(Optional.of(testProfile1));
        when(profileService.findById(uuid2)).thenReturn(Optional.of(testProfile2));
        when(profileService.findById(uuid3)).thenReturn(Optional.empty());
        when(profileService.findById(uuid4)).thenReturn(Optional.empty());

        ResponseEntity<List<UserProfile>> response1 = authorController.getAuthorByID(uuid3);
        ResponseEntity<List<UserProfile>> response2 = authorController.getAuthorByID(uuid4);

        verify(profileService, times(1)).findById(uuid3);
        verify(profileService, times(1)).findById(uuid4);

        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    void testGetAuthorsValid() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        UUID uuid3 = UUID.randomUUID();
        UUID uuid4 = UUID.randomUUID();


        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile1.setRole(UserProfile.RoleEnum.fromValue(0));

        UserProfile testProfile2 = new UserProfile("username2");
        testProfile2.setUserId(uuid2);
        testProfile2.setBio("Test Bio 2");
        testProfile2.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile2.setRole(UserProfile.RoleEnum.fromValue(1));

        UserProfile testProfile3 = new UserProfile("username3");
        testProfile3.setUserId(uuid3);
        testProfile3.setBio("Test Bio 3");
        testProfile3.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile3.setRole(UserProfile.RoleEnum.fromValue(2));

        UserProfile testProfile4 = new UserProfile("username4");
        testProfile4.setUserId(uuid4);
        testProfile4.setBio("Test Bio 4");
        testProfile4.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile4.setRole(UserProfile.RoleEnum.fromValue(1));

        ArrayList<UserProfile> list = new ArrayList<UserProfile>();
        list.add(testProfile2);
        list.add(testProfile4);

        ArrayList<UserProfile> list1 = new ArrayList<>();
        list1.add(testProfile1);
        list1.add(testProfile2);
        list1.add(testProfile3);
        list1.add(testProfile4);
        when(profileService.findAll()).thenReturn(list1);
        when(authorService.findAuthors()).thenReturn(list);


        ResponseEntity<List<UserProfile>> response = authorController.getAuthors();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(list, response.getBody());
    }

    @Test
    void testGetAuthorsInvalid() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        UUID uuid3 = UUID.randomUUID();
        UUID uuid4 = UUID.randomUUID();


        UserProfile testProfile1 = new UserProfile("username1");
        testProfile1.setUserId(uuid1);
        testProfile1.setBio("Test Bio 1");
        testProfile1.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile1.setRole(UserProfile.RoleEnum.fromValue(0));

        UserProfile testProfile2 = new UserProfile("username2");
        testProfile2.setUserId(uuid2);
        testProfile2.setBio("Test Bio 2");
        testProfile2.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile2.setRole(UserProfile.RoleEnum.fromValue(2));

        UserProfile testProfile3 = new UserProfile("username3");
        testProfile3.setUserId(uuid3);
        testProfile3.setBio("Test Bio 3");
        testProfile3.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile3.setRole(UserProfile.RoleEnum.fromValue(2));

        UserProfile testProfile4 = new UserProfile("username4");
        testProfile4.setUserId(uuid4);
        testProfile4.setBio("Test Bio 4");
        testProfile4.setPrivacy(UserProfile.PrivacyEnum.PUBLIC);
        testProfile4.setRole(UserProfile.RoleEnum.fromValue(0));

        when(profileService.findById(uuid1)).thenReturn(Optional.of(testProfile1));
        when(profileService.findById(uuid2)).thenReturn(Optional.of(testProfile2));
        when(profileService.findById(uuid3)).thenReturn(Optional.of(testProfile3));
        when(profileService.findById(uuid4)).thenReturn(Optional.of(testProfile4));

        ResponseEntity<List<UserProfile>> response = authorController.getAuthors();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}