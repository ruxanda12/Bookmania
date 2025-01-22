package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthorServiceTest {

    @Mock
    private UserProfileRepository profileRepo;
    @InjectMocks
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAuthorsTrue() {
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

        when(profileRepo.findAllByRole(UserProfile.RoleEnum.AUTHOR)).thenReturn(list);

        assertEquals(list, authorService.findAuthors());
    }


    @Test
    void findAuthorsFalse() {
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
        list.add(testProfile1);
        list.add(testProfile3);

        ArrayList<UserProfile> list1 = new ArrayList<>();
        list1.add(testProfile1);
        list1.add(testProfile2);
        list1.add(testProfile3);
        list1.add(testProfile4);

        assertNotEquals(list, authorService.findAuthors());
    }
}