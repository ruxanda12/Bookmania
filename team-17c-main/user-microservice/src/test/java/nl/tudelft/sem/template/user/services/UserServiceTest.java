package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.database.UserRepository;
import nl.tudelft.sem.template.user.model.User;
import nl.tudelft.sem.template.user.model.UserAuth;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.model.UserWithoutPassword;
import nl.tudelft.sem.template.user.models.UserProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserProfileService profileService;
    @Mock
    private LogService logService;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount() {
        UserAuth userAuth = new UserAuth("username", "test@example.com", "password");
        User user = new User(userAuth.getEmail(), userAuth.getPassword());
        UserProfile userProfile = new UserProfile(userAuth.getUsername());
        userProfile.setFollowing(new ArrayList<>());
        userProfile.setFollowers(new ArrayList<>());
        userProfile.setFriends(new ArrayList<>());

        //Setup mock behaviour
        when(profileService.save(any(UserProfile.class))).thenReturn(userProfile);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(logService).recordActivity(userProfile, "created their account");

        UserProfile resultProfile = userService.createAccount(userAuth);

        //Verify mock behaviour
        verify(profileService, times(1)).save(userProfile);
        verify(userRepository, times(1)).save(user);
        verify(logService, times(1)).recordActivity(userProfile, "created their account");

        assertEquals(userProfile, resultProfile);
        assertEquals(userProfile.getUserId(), user.getUserId());
    }

    @Test
    void validateAccountData() {
        UserAuth userAuth = new UserAuth("username", "test@example.com", "password");
        boolean result = userService.validateAccountData(userAuth);
        assertTrue(result);
    }

    @Test
    void validateAccountData_InvalidUsername() {
        UserAuth userAuth = new UserAuth("1Username", "test@example.com", "password");
        boolean result = userService.validateAccountData(userAuth);
        assertFalse(result);
    }

    @Test
    void validateAccountData_EmptyOrNullEmail() {
        UserAuth userAuth = new UserAuth("username", "", "password");
        boolean result = userService.validateAccountData(userAuth);
        assertFalse(result);
    }

    @Test
    void validateAccountData_EmptyOrNullPassword() {
        UserAuth userAuth = new UserAuth("username", "email", null);
        boolean result = userService.validateAccountData(userAuth);
        assertFalse(result);
    }

    @Test
    void validateNewData() {
        boolean result = userService.validateNewData("email", "password");
        assertTrue(result);
    }

    @Test
    void validateNewData_InvalidEmail() {
        boolean result = userService.validateNewData(null, "password");
        assertFalse(result);
    }

    @Test
    void validateNewData_InvalidPassword() {
        boolean result = userService.validateNewData("email", "");
        assertFalse(result);
    }

    @Test
    void existsByEmail() {
        String email = "email@gmail.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        boolean result = userService.existsByEmail(email);
        verify(userRepository, times(1)).existsByEmail(email);
        assertTrue(result);
    }

    @Test
    void existsByEmail_False() {
        String email = "email@gmail.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        boolean result = userService.existsByEmail(email);
        verify(userRepository, times(1)).existsByEmail(email);
        assertFalse(result);
    }

    @Test
    void findById() {
        UUID userId = UUID.randomUUID();
        User user = new User("email", "pass");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findById(userId);
        verify(userRepository, times(1)).findById(userId);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findById_False() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Optional<User> result = userService.findById(userId);
        verify(userRepository, times(1)).findById(userId);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAccount() {
        UUID userId = UUID.randomUUID();
        UserProfile profile = new UserProfile("username");

        UserProfile otherUser = new UserProfile("other");
        UUID otherId = UUID.randomUUID();
        otherUser.setUserId(otherId);
        List<UUID> followerList = new ArrayList<>();
        followerList.add(userId);
        otherUser.setFollowers(followerList);

        List<UUID> followingList = new ArrayList<>();
        followingList.add(otherId);
        profile.setFollowing(followingList);

        User user = new User("email", "pass");
        user.setUserId(userId);
        profile.setUserId(userId);
        String microserviceUrl =
                "http://localhost:8080/bookshelves/bookshelf/{userId}/removeAll/requestBy/{requesterId}";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileService.findById(userId)).thenReturn(Optional.of(profile));
        when(profileService.findById(otherId)).thenReturn(Optional.of(otherUser));
        doNothing().when(userRepository).delete(user);
        doNothing().when(restTemplate).delete(anyString(), any(), any(Void.class));

        userService.deleteAccount(userId);

        verify(logService, times(1)).recordActivity(profile, "deleted their account");
        verify(userRepository, times(1)).findById(userId);
        verify(profileService, times(1)).findById(userId);
        verify(profileService, times(1)).save(otherUser);
        verify(userRepository, times(1)).delete(user);
        verify(restTemplate, times(1)).delete(microserviceUrl, userId, userId, Void.class);
        verify(profileService, times(1)).deleteById(userId);
    }

    @Test
    void viewAccount() {
        UUID userId = UUID.randomUUID();
        User user = new User("email", "pass");
        UserProxy proxy = new UserProxy(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserWithoutPassword result = userService.viewAccount(userId);

        verify(userRepository, times(1)).findById(userId);
        assertEquals(proxy.viewAccount(), result);
    }

    @Test
    void existsUserSameEmail() {
        UUID userId = UUID.randomUUID();
        User user = new User("email", "pass");
        User existingUser = new User("email1", "anotherPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("email")).thenReturn(true);

        boolean result = userService.existsUserSameEmail(userId,user);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByEmail("email");

        assertTrue(result);
    }

    @Test
    void existsUserSameEmail_sameInitialEmails() {
        UUID userId = UUID.randomUUID();
        User user = new User("email", "pass");
        User existingUser = new User("email", "anotherPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        boolean result = userService.existsUserSameEmail(userId,user);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).existsByEmail("email");

        assertFalse(result);
    }

    @Test
    void existsUserSameEmail_DoesNotExist() {
        UUID userId = UUID.randomUUID();
        User user = new User("email", "pass");
        User existingUser = new User("email1", "anotherPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("email")).thenReturn(false);

        boolean result = userService.existsUserSameEmail(userId,user);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByEmail("email");

        assertFalse(result);
    }

    @Test
    void modifyAccount() {
        User user = new User("email", "pass");
        UserProxy proxy = new UserProxy(user);

        when(userRepository.save(user)).thenReturn(user);

        UserWithoutPassword result = userService.modifyAccount(user);

        verify(userRepository, times(1)).save(user);
        assertEquals(proxy.viewAccount(), result);
    }
}