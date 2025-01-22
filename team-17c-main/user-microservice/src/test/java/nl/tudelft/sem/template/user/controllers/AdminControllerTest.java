package nl.tudelft.sem.template.user.controllers;

import nl.tudelft.sem.template.user.database.AdminKeyRepository;
import nl.tudelft.sem.template.user.database.LogItemRepository;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.database.UserRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.models.AdminKey;
import nl.tudelft.sem.template.user.models.LogItem;
import nl.tudelft.sem.template.user.services.LogService;
import nl.tudelft.sem.template.user.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdminControllerTest {
    @Mock
    private UserRepository userRepo;
    @Mock
    private UserService userService;
    @Mock
    private UserProfileRepository profileRepo;
    @Mock
    private AdminKeyRepository adminKeyRepo;
    @Mock
    private LogItemRepository logRepo;
    @Mock
    private LogService logService;
    @InjectMocks
    private AdminController controller;
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
    void testGenerateAdminKey(){
        UserProfile regularProfile = new UserProfile("regularProfile");
        regularProfile.setState(UserProfile.StateEnum.ACTIVE);
        regularProfile.setRole(UserProfile.RoleEnum.USER);
        regularProfile.setUserId(new UUID(1, 1));

        UserProfile admin = new UserProfile("admin");
        admin.setState(UserProfile.StateEnum.ACTIVE);
        admin.setRole(UserProfile.RoleEnum.ADMIN);
        admin.setUserId(new UUID(3, 3));

        UserProfile inactiveAdmin = new UserProfile("admin");
        inactiveAdmin.setState(UserProfile.StateEnum.INACTIVE);
        inactiveAdmin.setRole(UserProfile.RoleEnum.ADMIN);
        inactiveAdmin.setUserId(new UUID(3, 3));

        when(profileRepo.findById(any())).thenReturn(Optional.empty());
        when(profileRepo.findById(regularProfile.getUserId())).thenReturn(Optional.of(regularProfile));
        when(profileRepo.findById(inactiveAdmin.getUserId())).thenReturn(Optional.of(inactiveAdmin));
        when(profileRepo.findById(admin.getUserId())).thenReturn(Optional.of(admin));

        // This endpoint is based on randomness, cannot test it fully
        when(adminKeyRepo.existsById(any())).thenReturn(false);

        assertEquals(HttpStatus.UNAUTHORIZED, controller.generateAdminKey(regularProfile.getUserId()).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, controller.generateAdminKey(new UUID(0, 0)).getStatusCode());
        assertEquals(HttpStatus.OK, controller.generateAdminKey(admin.getUserId()).getStatusCode());
        assertEquals(HttpStatus.OK, controller.generateAdminKey(inactiveAdmin.getUserId()).getStatusCode());
        assertNotNull(controller.generateAdminKey(admin.getUserId()).getBody());

        verify(adminKeyRepo, times(3)).existsById(any());
        verify(adminKeyRepo, times(3)).save(any());
    }

    @Test
    void testViewAdminKeys(){
        UserProfile regularProfile = new UserProfile("regularProfile");
        regularProfile.setState(UserProfile.StateEnum.ACTIVE);
        regularProfile.setRole(UserProfile.RoleEnum.USER);
        regularProfile.setUserId(new UUID(1, 1));

        UserProfile admin = new UserProfile("admin");
        admin.setState(UserProfile.StateEnum.ACTIVE);
        admin.setRole(UserProfile.RoleEnum.ADMIN);
        admin.setUserId(new UUID(3, 3));

        UserProfile inactiveAdmin = new UserProfile("admin");
        inactiveAdmin.setState(UserProfile.StateEnum.INACTIVE);
        inactiveAdmin.setRole(UserProfile.RoleEnum.ADMIN);
        inactiveAdmin.setUserId(new UUID(3, 3));

        when(profileRepo.findById(any())).thenReturn(Optional.empty());
        when(profileRepo.findById(regularProfile.getUserId())).thenReturn(Optional.of(regularProfile));
        when(profileRepo.findById(inactiveAdmin.getUserId())).thenReturn(Optional.of(inactiveAdmin));
        when(profileRepo.findById(admin.getUserId())).thenReturn(Optional.of(admin));

        AdminKey key1 = new AdminKey();
        key1.setKey(new UUID(1, 2));

        AdminKey key2 = new AdminKey();
        key2.setKey(new UUID(2, 3));

        AdminKey key3 = new AdminKey();
        key3.setKey(new UUID(3, 4));

        List<AdminKey> list = List.of(key1, key2, key3);
        List<UUID> resultingList = List.of(key1.getKey(), key2.getKey(), key3.getKey());

        // This endpoint is based on randomness, cannot test it fully
        when(adminKeyRepo.findAll()).thenReturn(list);

        assertEquals(HttpStatus.UNAUTHORIZED, controller.viewAdminKeys(regularProfile.getUserId()).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, controller.viewAdminKeys(new UUID(0, 0)).getStatusCode());
        assertEquals(HttpStatus.OK, controller.viewAdminKeys(admin.getUserId()).getStatusCode());
        assertEquals(HttpStatus.OK, controller.viewAdminKeys(inactiveAdmin.getUserId()).getStatusCode());
        assertEquals(resultingList, controller.viewAdminKeys(admin.getUserId()).getBody());

        verify(adminKeyRepo, times(3)).findAll();
    }

    @Test
    void testAnalytics(){
        // I will not test the entire UserAnalytics again, check the UserAnalyticsFiller tests for those

        UserProfile regularProfile = new UserProfile("regularProfile");
        regularProfile.setState(UserProfile.StateEnum.ACTIVE);
        regularProfile.setRole(UserProfile.RoleEnum.USER);
        regularProfile.setUserId(new UUID(1, 1));

        UserProfile admin = new UserProfile("admin");
        admin.setState(UserProfile.StateEnum.ACTIVE);
        admin.setRole(UserProfile.RoleEnum.ADMIN);
        admin.setUserId(new UUID(3, 3));

        UserProfile inactiveAdmin = new UserProfile("admin");
        inactiveAdmin.setState(UserProfile.StateEnum.INACTIVE);
        inactiveAdmin.setRole(UserProfile.RoleEnum.ADMIN);
        inactiveAdmin.setUserId(new UUID(3, 3));

        when(profileRepo.findById(any())).thenReturn(Optional.empty());
        when(profileRepo.findById(regularProfile.getUserId())).thenReturn(Optional.of(regularProfile));
        when(profileRepo.findById(inactiveAdmin.getUserId())).thenReturn(Optional.of(inactiveAdmin));
        when(profileRepo.findById(admin.getUserId())).thenReturn(Optional.of(admin));

        when(profileRepo.findMostFollowedUsers()).thenReturn(new String[0]);
        when(profileRepo.findMostPopularBooks()).thenReturn(new String[0]);
        when(profileRepo.findMostPopularGenres()).thenReturn(new String[0]);

        assertEquals(HttpStatus.UNAUTHORIZED, controller.viewAnalytics(regularProfile.getUserId()).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, controller.viewAnalytics(new UUID(0, 0)).getStatusCode());
        assertEquals(HttpStatus.OK, controller.viewAnalytics(admin.getUserId()).getStatusCode());
        assertEquals(HttpStatus.OK, controller.viewAnalytics(inactiveAdmin.getUserId()).getStatusCode());

        // test whether the analytics go through

        List<LogItem> log = List.of(
                new LogItem("Tested the system"),
                new LogItem("Tested the system twice")
        );

        List<String> resultingList = log.stream().map(LogItem::toString).collect(Collectors.toList());
        when(logRepo.findTop10ByOrderByTimestampDesc()).thenReturn(log);

        assertNotNull(controller.viewAnalytics(admin.getUserId()).getBody().getUserLog());
        assertEquals(resultingList, controller.viewAnalytics(admin.getUserId()).getBody().getUserLog());
    }

    @Test
    void testAdminViewUser() {
        UserProfile admin = new UserProfile("admin");
        admin.setState(UserProfile.StateEnum.ACTIVE);
        admin.setRole(UserProfile.RoleEnum.ADMIN);
        admin.setUserId(UUID.randomUUID());

        UserProfile regularUser = new UserProfile("regularUser");
        regularUser.setState(UserProfile.StateEnum.ACTIVE);
        regularUser.setRole(UserProfile.RoleEnum.USER);
        regularUser.setUserId(UUID.randomUUID());

        // set-up
        when(profileRepo.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(profileRepo.findById(regularUser.getUserId())).thenReturn(Optional.of(regularUser));

        // admin
        ResponseEntity<UserProfile> adminResponse = controller.adminViewUser(admin.getUserId(), regularUser.getUserId());
        assertEquals(HttpStatus.OK, adminResponse.getStatusCode());
        assertNotNull(adminResponse.getBody());

        // user
        ResponseEntity<UserProfile> regularUserResponse = controller.adminViewUser(regularUser.getUserId(), admin.getUserId());
        assertEquals(HttpStatus.UNAUTHORIZED, regularUserResponse.getStatusCode());

        // non-existing user
        UUID nonExistingUserId = UUID.randomUUID();
        ResponseEntity<UserProfile> notFoundResponse = controller.adminViewUser(admin.getUserId(), nonExistingUserId);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
        assertEquals(ResponseEntity.notFound().build(), notFoundResponse);

    }

    @Test
    void testAdminModifyUser() {
        UserProfile admin = new UserProfile("admin");
        admin.setState(UserProfile.StateEnum.ACTIVE);
        admin.setRole(UserProfile.RoleEnum.ADMIN);
        admin.setUserId(UUID.randomUUID());

        UserProfile existingUser = new UserProfile("existingUser");
        existingUser.setState(UserProfile.StateEnum.ACTIVE);
        existingUser.setRole(UserProfile.RoleEnum.USER);
        existingUser.setUserId(UUID.randomUUID());

        UserProfile modifiedUser = new UserProfile("modifiedUser");
        modifiedUser.setState(UserProfile.StateEnum.ACTIVE);
        modifiedUser.setRole(UserProfile.RoleEnum.USER);
        modifiedUser.setUserId(existingUser.getUserId());

        UserProfile nonExistingUser = new UserProfile("nonExistingUser");
        nonExistingUser.setState(UserProfile.StateEnum.ACTIVE);
        nonExistingUser.setRole(UserProfile.RoleEnum.USER);
        nonExistingUser.setUserId(UUID.randomUUID());

        UserProfile incorrectUser = new UserProfile("incorrectUser");
        incorrectUser.setState(UserProfile.StateEnum.ACTIVE);
        incorrectUser.setRole(UserProfile.RoleEnum.USER);
        incorrectUser.setUserId(UUID.randomUUID());

        // set-up
        when(profileRepo.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(profileRepo.findById(existingUser.getUserId())).thenReturn(Optional.of(existingUser));
        when(profileRepo.findById(modifiedUser.getUserId())).thenReturn(Optional.of(modifiedUser));
        when(profileRepo.findById(incorrectUser.getUserId())).thenReturn(Optional.of(modifiedUser));

        // admin
        ResponseEntity<UserProfile> adminResponse = controller.adminModifyUser(admin.getUserId(), modifiedUser.getUserId(), modifiedUser);
        assertEquals(HttpStatus.OK, adminResponse.getStatusCode());
        assertEquals(modifiedUser, adminResponse.getBody());
        verify(profileRepo).save(modifiedUser);

        // user
        ResponseEntity<UserProfile> regularUserResponse = controller.adminModifyUser(existingUser.getUserId(), modifiedUser.getUserId(), modifiedUser);
        assertEquals(HttpStatus.UNAUTHORIZED, regularUserResponse.getStatusCode());

        // non-existing user
        ResponseEntity<UserProfile> badRequestResponse = controller.adminModifyUser(admin.getUserId(), nonExistingUser.getUserId(), nonExistingUser);
        assertEquals(HttpStatus.BAD_REQUEST, badRequestResponse.getStatusCode());

        // incorrect user ID
        ResponseEntity<UserProfile> incorrectUserResponse = controller.adminModifyUser(admin.getUserId(), incorrectUser.getUserId(), incorrectUser);
        assertEquals(HttpStatus.BAD_REQUEST, incorrectUserResponse.getStatusCode());
    }

    @Test
    void testAdminDeleteUser() {
        UserProfile admin = new UserProfile("admin");
        admin.setState(UserProfile.StateEnum.ACTIVE);
        admin.setRole(UserProfile.RoleEnum.ADMIN);
        admin.setUserId(UUID.randomUUID());

        UserProfile regularUser = new UserProfile("regularUser");
        regularUser.setState(UserProfile.StateEnum.ACTIVE);
        regularUser.setRole(UserProfile.RoleEnum.USER);
        regularUser.setUserId(UUID.randomUUID());

        UUID nonExistingUserId = UUID.randomUUID();

        // set-up
        when(profileRepo.findById(admin.getUserId())).thenReturn(Optional.of(admin));
        when(profileRepo.findById(regularUser.getUserId())).thenReturn(Optional.of(regularUser));
        when(profileRepo.findById(nonExistingUserId)).thenReturn(Optional.empty());

        // admin
        ResponseEntity<Void> adminResponse = controller.adminDeleteUser(admin.getUserId(), regularUser.getUserId());
        assertEquals(HttpStatus.OK, adminResponse.getStatusCode());
        verify(userService, times(1)).deleteAccount(regularUser.getUserId());

        // unauthorized
        ResponseEntity<Void> unauthorizedResponse = controller.adminDeleteUser(regularUser.getUserId(), admin.getUserId());
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedResponse.getStatusCode());

        // non-existing user
        ResponseEntity<Void> notFoundResponse = controller.adminDeleteUser(admin.getUserId(), nonExistingUserId);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }
}
