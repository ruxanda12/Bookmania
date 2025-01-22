package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfileCheckTest {
    UserProfile admin;
    UserProfile author;
    UserProfile user1;
    UserProfile user2;

    @Mock
    UserProfileRepository profileRepo;

    @BeforeAll
    public void setup() {
        admin = new UserProfile("admin");
        admin.setRole(UserProfile.RoleEnum.ADMIN);

        author = new UserProfile("author");
        author.setRole(UserProfile.RoleEnum.AUTHOR);

        user1 = new UserProfile("user1");

        user2 = new UserProfile("user2");
        user2.setState(UserProfile.StateEnum.INACTIVE);

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRoles() {
        assertTrue(ProfileCheck.isAdmin(admin));
        assertTrue(ProfileCheck.isAuthor(author));

        assertFalse(ProfileCheck.isAdmin(author));
        assertFalse(ProfileCheck.isAdmin(user1));
        assertFalse(ProfileCheck.isAuthor(admin));

        assertFalse(ProfileCheck.isAuthor(null));
        assertFalse(ProfileCheck.isAdmin(null));
    }

    @Test
    public void testStates() {
        assertTrue(ProfileCheck.isActive(admin));
        assertTrue(ProfileCheck.isActive(user1));
        assertFalse(ProfileCheck.isActive(user2));
        assertFalse(ProfileCheck.isActive(null));
    }

    @Test
    void testGetProfile(){
        assertNull(ProfileCheck.getUserProfile(profileRepo, null));
        verify(profileRepo, never()).findById(any());

        UUID uuid = new UUID(10, 100);
        UserProfile user = new UserProfile("user1");
        user.setUserId(uuid);

        when(profileRepo.findById(any())).thenReturn(Optional.empty());
        when(profileRepo.findById(uuid)).thenReturn(Optional.of(user));

        assertNull(ProfileCheck.getUserProfile(profileRepo, new UUID(100, 10)));
        assertEquals(user, ProfileCheck.getUserProfile(profileRepo, uuid));

        verify(profileRepo, times(2)).findById(any());
    }
}
