package nl.tudelft.sem.template.user.services;

import java.util.ArrayList;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
    /**
     * Follow another user, handles friendships too.
     *
     * @param user      user calling this function
     * @param otherUser user to be followed
     */
    public static void followUser(UserProfile user, UserProfile otherUser) {
        initializeLists(user);
        initializeLists(otherUser);

        if (user.getUserId() == otherUser.getUserId()) {
            return;
        }

        if (!user.getFollowing().contains(otherUser.getUserId())) {
            user.getFollowing().add(otherUser.getUserId());
        }
        if (!otherUser.getFollowers().contains(user.getUserId())) {
            otherUser.getFollowers().add(user.getUserId());
        }
        if (otherUser.getFollowing().contains(user.getUserId())
            && !otherUser.getFriends().contains(user.getUserId())) {
            otherUser.getFriends().add(user.getUserId());
            user.getFriends().add(otherUser.getUserId());
        }
    }

    /**
     * Unfollow a user, handles friendships too.
     *
     * @param user      user calling this function
     * @param otherUser user to be unfollowed
     * @return boolean indicating success
     */
    public static boolean unfollowUser(UserProfile user, UserProfile otherUser) {
        initializeLists(user);
        initializeLists(otherUser);

        if (user.getUserId() == otherUser.getUserId()) {
            return false;
        }

        otherUser.getFollowers().remove(user.getUserId());
        otherUser.getFriends().remove(user.getUserId());

        user.getFriends().remove(otherUser.getUserId());
        return user.getFollowing().remove(otherUser.getUserId());
    }


    /**
     * Make sure none of the attributes are null, it's a double check.
     *
     * @param user User 'calling' this
     */
    private static void initializeLists(UserProfile user) {
        if (user.getFollowers() == null) {
            user.setFollowers(new ArrayList<>());
        }
        if (user.getFollowing() == null) {
            user.setFollowing(new ArrayList<>());
        }
        if (user.getFriends() == null) {
            user.setFriends(new ArrayList<>());
        }
    }

}
