package nl.tudelft.sem.template.user.controllers;

import nl.tudelft.sem.template.user.model.*;
import nl.tudelft.sem.template.user.models.UserProxy;
import nl.tudelft.sem.template.user.services.LogService;
import nl.tudelft.sem.template.user.services.UserProfileService;
import nl.tudelft.sem.template.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private UserProfileService profileService;

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private LogService logService;

    @InjectMocks
    private AccountController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    User exampleUserSetup(UserAuth auth, UUID setupId){
        User addedUser = new User(auth.getEmail(), auth.getPassword());
        UserProfile addedProfile = new UserProfile(auth.getUsername());
        addedProfile.setUserId(setupId);
        //addedUser.setProfile(addedProfile);
        addedUser.setUserId(addedProfile.getUserId());
        return addedUser;
    }

    @Test
    public void createAccount_Success() {
        //Mock data
        UserAuth userAuth = new UserAuth("username", "test@example.com", "password");
        User createdUser = new User(userAuth.getEmail(), userAuth.getPassword());
        UserProfile userProfile = new UserProfile(userAuth.getUsername());
        UUID id = UUID.randomUUID();
        userProfile.setUserId(id);
        createdUser.setUserId(id);

        //Mock service behavior
        when(userService.validateAccountData(userAuth)).thenReturn(true);
        when(userService.existsByEmail(userAuth.getEmail())).thenReturn(false);
        when(userService.createAccount(userAuth)).thenReturn(userProfile);

        //Call the method
        ResponseEntity<UserProfile> responseEntity = userController.createAccount(userAuth);

        //Verify interactions
        verify(userService, times(1)).validateAccountData(userAuth);
        verify(userService, times(1)).existsByEmail(userAuth.getEmail());
        verify(userService, times(1)).createAccount(userAuth);


        //Verify response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userProfile, responseEntity.getBody());
        assertEquals(userProfile.getUserId(), createdUser.getUserId());
    }

    @Test
    public void testCreateAccount_InvalidData() {
        //Mock data
        UserAuth userAuth = new UserAuth("username", "test@example.com", "password");

        //Mock service behavior
        when(userService.validateAccountData(userAuth)).thenReturn(false);

        //Call the method
        ResponseEntity<UserProfile> responseEntity = userController.createAccount(userAuth);

        //Verify interactions
        verify(userService, times(1)).validateAccountData(userAuth);
        verify(userService, never()).existsByEmail(userAuth.getEmail());
        verify(userService, never()).createAccount(any(UserAuth.class));

        //Verify response
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testCreateAccount_UserAlreadyExists() {
        //Mock data
        UserAuth userAuth = new UserAuth("1username", "test@example.com", "password");

        //Mock service behavior
        when(userService.validateAccountData(userAuth)).thenReturn(true);
        when(userService.existsByEmail(userAuth.getEmail())).thenReturn(true);

        //Call the method
        ResponseEntity<UserProfile> responseEntity = userController.createAccount(userAuth);

        //Verify interactions
        verify(userService, times(1)).validateAccountData(userAuth);
        verify(userService, times(1)).existsByEmail(userAuth.getEmail());
        verify(userService, never()).createAccount(any(UserAuth.class));

        //Verify response
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void deleteAccount_Success() {
        //Mock data
        UUID userId = UUID.randomUUID();
        UserAuth auth = new UserAuth("username", "email@example.com","password");

        User user = new User(auth.getEmail(), auth.getPassword());
        UserProfile profile = new UserProfile(auth.getUsername());
        user.setUserId(userId);

        //Mock service behavior
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(profileService.findById(userId)).thenReturn(Optional.of(profile));
        doNothing().when(userService).deleteAccount(userId);

        //Call the method
        ResponseEntity<Void> responseEntity = userController.deleteAccount(userId);

        //Verify interactions
        verify(userService, times(1)).findById(userId);
        verify(profileService, times(1)).findById(userId);
        verify(userService, times(1)).deleteAccount(userId);

        //Verify response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void deleteAccount_UserNotFound() {
        //Mock data
        UUID userId = UUID.randomUUID();

        //Mock service behavior
        when(userService.findById(userId)).thenReturn(Optional.empty());

        //Call the method
        ResponseEntity<Void> responseEntity = userController.deleteAccount(userId);

        //Verify interactions
        verify(userService, times(1)).findById(userId);
        verify(profileService, never()).findById(any());
        verify(userService, never()).deleteAccount(any());

        //Verify response
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void deleteAccount_ProfileNotFound() {
        //Mock data
        UUID userId = UUID.randomUUID();
        UserAuth auth = new UserAuth("username", "email@example.com","password");

        User user = new User(auth.getEmail(), auth.getPassword());
        UserProfile profile = new UserProfile(auth.getUsername());
        user.setUserId(userId);

        //Mock service behavior
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(profileService.findById(userId)).thenReturn(Optional.empty());

        //Call the method
        ResponseEntity<Void> responseEntity = userController.deleteAccount(userId);

        //Verify interactions
        verify(userService, times(1)).findById(userId);
        verify(profileService, times(1)).findById(userId);
        verify(userService, never()).deleteAccount(any());

        //Verify response
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void viewOwnAccount_valid(){
        //Mock data
        UUID userId = UUID.randomUUID();
        UserAuth auth = new UserAuth("username", "email@example.com","password");
        User user = exampleUserSetup(auth,userId);

        UserProxy proxy = new UserProxy(user);

        //Mock repository behavior
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(userService.viewAccount(userId)).thenReturn(proxy.viewAccount());

        //Call the method
        ResponseEntity<UserWithoutPassword> responseEntity = userController.viewAccount(userId);

        //Verify interactions
        verify(userService, times(1)).findById(userId);
        verify(userService, times(1)).viewAccount(userId);

        //Verify response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(proxy.viewAccount(), responseEntity.getBody());
    }

    @Test
    public void viewOwnAccount_noAccount(){
        //Mock data
        UUID userId = UUID.randomUUID();

        //Mock repository behavior
        when(userService.findById(userId)).thenReturn(Optional.empty());

        //Call the method
        ResponseEntity<UserWithoutPassword> responseEntity = userController.viewAccount(userId);

        //Verify interactions
        verify(userService, times(1)).findById(userId);
        verify(userService, never()).viewAccount(any());


        //Verify response
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void updateAccount() {
        UUID userId = UUID.randomUUID();
        UserAuth userAuth = new UserAuth("username", "test@example.com", "password");
        User oldUser = exampleUserSetup(userAuth, userId); //already in database

        UserAuth newUserAuth = new UserAuth("username", "test2@example.com", "password3");
        User updatedUser = exampleUserSetup(newUserAuth, userId);
        UserProxy proxy = new UserProxy(updatedUser);

        //Setup mock behaviour
        when(userService.validateNewData(newUserAuth.getEmail(), newUserAuth.getPassword())).thenReturn(true);
        when(userService.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userService.existsUserSameEmail(userId, updatedUser)).thenReturn(false);
        when(userService.modifyAccount(updatedUser)).thenReturn(proxy.viewAccount());

        ResponseEntity<UserWithoutPassword> response = userController.updateAccount(userId, updatedUser);
        UserWithoutPassword newUserData = response.getBody();

        // Verify mock behaviour
        verify(userService, times(1))
                .validateNewData(newUserAuth.getEmail(), newUserAuth.getPassword());
        verify(userService, times(1)).findById(userId);
        verify(userService, times(1)).existsUserSameEmail(userId, updatedUser);
        verify(userService, times(1)).modifyAccount(updatedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(proxy.viewAccount(), newUserData);
    }

    @Test
    void updateAccount_invalidInput() {
        UUID userId = UUID.randomUUID();
        UserAuth newUserAuth = new UserAuth("username", "", "password3");
        User updatedUser = exampleUserSetup(newUserAuth, userId);

        //Setup mock behaviour
        when(userService.validateNewData(newUserAuth.getEmail(), newUserAuth.getPassword())).thenReturn(false);

        ResponseEntity<UserWithoutPassword> response = userController.updateAccount(userId, updatedUser);

        // Verify mock behaviour
        verify(userService, times(1))
                .validateNewData(newUserAuth.getEmail(), newUserAuth.getPassword());
        verify(userService, never()).findById(userId);
        verify(userService, never()).existsUserSameEmail(userId, updatedUser);
        verify(userService, never()).modifyAccount(updatedUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateAccount_notInDatabase() {
        UUID userId = UUID.randomUUID();
        UserAuth userAuth = new UserAuth("username", "test@example.com", "password");
        User oldUser = exampleUserSetup(userAuth, userId); //already in database

        UserAuth newUserAuth = new UserAuth("username", "test3@example.com", "password3");
        User updatedUser = exampleUserSetup(newUserAuth, userId);

        //Setup mock behaviour
        when(userService.validateNewData(newUserAuth.getEmail(), newUserAuth.getPassword())).thenReturn(true);
        when(userService.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<UserWithoutPassword> response = userController.updateAccount(userId, updatedUser);

        // Verify mock behaviour
        verify(userService, times(1))
                .validateNewData(newUserAuth.getEmail(), newUserAuth.getPassword());
        verify(userService, times(1)).findById(userId);
        verify(userService, never()).existsUserSameEmail(userId, updatedUser);
        verify(userService, never()).modifyAccount(updatedUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateAccount_changingOtherUser() {
        UUID userId = UUID.randomUUID();
        UserAuth userAuth = new UserAuth("username", "test@example.com", "password");
        User oldUser = exampleUserSetup(userAuth, userId); //already in database

        UserAuth newUserAuth = new UserAuth("username", "test3@example.com", "password3");
        User updatedUser = exampleUserSetup(newUserAuth, userId);

        when(userService.validateNewData(newUserAuth.getEmail(), newUserAuth.getPassword())).thenReturn(true);

        //check with different Ids
        ResponseEntity<UserWithoutPassword> response = userController.updateAccount(UUID.randomUUID(), updatedUser);

        verify(userService, times(1))
                .validateNewData(newUserAuth.getEmail(), newUserAuth.getPassword());
        verify(userService, never()).findById(userId);
        verify(userService, never()).existsUserSameEmail(userId, updatedUser);
        verify(userService, never()).modifyAccount(updatedUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateAccount_existingEmail() {
        UUID userId = UUID.randomUUID();
        UserAuth userAuth = new UserAuth("username", "test@example.com", "password");
        User oldUser = exampleUserSetup(userAuth, userId); //already in database

        UserAuth newUserAuth = new UserAuth("username", "test2@example.com", "password3");
        User updatedUser = exampleUserSetup(newUserAuth, userId);

        //Mock behaviour
        when(userService.validateNewData(newUserAuth.getEmail(), newUserAuth.getPassword())).thenReturn(true);
        when(userService.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userService.existsUserSameEmail(userId, updatedUser)).thenReturn(true);

        ResponseEntity<UserWithoutPassword> response = userController.updateAccount(userId, updatedUser);

        // Verify mock behaviour
        verify(userService, times(1))
                .validateNewData(newUserAuth.getEmail(), newUserAuth.getPassword());
        verify(userService, times(1)).findById(userId);
        verify(userService, times(1)).existsUserSameEmail(userId, updatedUser);
        verify(userService, never()).modifyAccount(updatedUser);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }
}