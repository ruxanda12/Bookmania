package nl.tudelft.sem.template.user.models;

import nl.tudelft.sem.template.user.model.User;
import nl.tudelft.sem.template.user.model.UserWithoutPassword;

public class UserProxy implements UserInterface {
    private final User realUser;

    public UserProxy(User realUser) {
        this.realUser = realUser;
    }

    public UserWithoutPassword viewAccount() {
        // Check if the requesting user is the same as the target user
        return this.convertUserToNoPassword(realUser);
    }

    private UserWithoutPassword convertUserToNoPassword(User createdUser) {
        UserWithoutPassword noPasswordUser = new UserWithoutPassword(createdUser.getEmail());
        noPasswordUser.setUserId(createdUser.getUserId());
        //noPasswordUser.setProfile(createdUser.getProfile());
        return noPasswordUser;
    }
}
