package nl.tudelft.sem.template.user.models;

import nl.tudelft.sem.template.user.model.User;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.model.UserWithoutPassword;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserProxyTest {

    @Test
    void viewAccount() {
        // Arrange
        User realUser = new User("email@example.com", "password" );
        realUser.setUserId(UUID.randomUUID());
        UserProfile profile = new UserProfile("username");
        //realUser.setProfile(profile);
        UserProxy proxyUser = new UserProxy(realUser);

        // Act
        UserWithoutPassword result = proxyUser.viewAccount();

        // Assert
        assertNotNull(result);
        assertEquals("email@example.com", result.getEmail());
        //assertEquals(profile, result.getProfile());
    }

}