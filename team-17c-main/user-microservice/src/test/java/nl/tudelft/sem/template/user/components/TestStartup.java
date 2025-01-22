package nl.tudelft.sem.template.user.components;

import nl.tudelft.sem.template.user.model.User;
import nl.tudelft.sem.template.user.model.UserAuth;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.services.LogService;
import nl.tudelft.sem.template.user.services.UserProfileService;
import nl.tudelft.sem.template.user.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Tests the correctness of the startup component / FirstAdmin
public class TestStartup {
    @Mock
    private LogService logService;
    @Mock
    private UserService userService;
    @Mock
    private UserProfileService userProfileService;
    @InjectMocks
    private FirstAdmin firstAdmin;
    private AutoCloseable closeable;

    private final PrintStream originalSystemOut = System.out;

    // Custom PrintStream to capture output
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach()
    void cleanUp() {
        try {
            closeable.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.setOut(originalSystemOut);
    }

    @Test
    void test(){
        String email = "admin17c@tudelft.nl";
        when(userService.existsByEmail(email)).thenReturn(true);

        User dummyUser = new User(email, "admin");
        dummyUser.setUserId(new UUID(10, 20));
        when(userService.findUserByEmail(email)).thenReturn(dummyUser);

        firstAdmin.run();

        long lineCount = outputStreamCaptor.toString().lines().count();
        assertEquals(2, lineCount);

        verify(logService, times(1)).recordActivity(any());

        when(userService.existsByEmail(email)).thenReturn(false);
        UserProfile dummyProfile = new UserProfile("admin");
        dummyProfile.setUserId(new UUID(10, 20));
        when(userService.createAccount(new UserAuth("admin", email, "admin")))
                .thenReturn(dummyProfile);

        when(userProfileService.save(dummyProfile)).thenReturn(dummyProfile);

        firstAdmin.run();

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userProfileService, times(1)).save(captor.capture());
        assertEquals(captor.getValue().getRole(), UserProfile.RoleEnum.ADMIN);

        ArgumentCaptor<UserAuth> captor2 = ArgumentCaptor.forClass(UserAuth.class);
        verify(userService, times(1)).createAccount(captor2.capture());
        assertEquals(captor2.getValue().getEmail(), email);

        verify(logService, times(2)).recordActivity(any());
        assertEquals(dummyProfile.getRole(), UserProfile.RoleEnum.ADMIN);

        long lineCount2 = outputStreamCaptor.toString().lines().count();
        assertEquals(4, lineCount2);
    }
}
