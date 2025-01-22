package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.model.UserProfile;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FollowServiceTest {

    @Test
    void testFollowing(){
        UserProfile profile1 = new UserProfile("profile1");
        profile1.setUserId(new UUID(1, 1));

        UserProfile profile2 = new UserProfile("profile1");
        profile2.setUserId(new UUID(2, 2));

        UserProfile profile3 = new UserProfile("profile1");
        profile3.setUserId(new UUID(3, 3));

        FollowService.followUser(profile3, profile3);
        // make sure the lists are not null
        assertEquals(List.of(), profile3.getFollowing());
        assertEquals(List.of(), profile3.getFollowers());
        assertEquals(List.of(), profile3.getFriends());

        FollowService.followUser(profile1, profile2);
        assertEquals(List.of(), profile2.getFriends());

        assertEquals(List.of(profile2.getUserId()), profile1.getFollowing());
        assertEquals(List.of(), profile1.getFollowers());
        assertEquals(List.of(profile1.getUserId()), profile2.getFollowers());
        assertEquals(List.of(), profile2.getFollowing());

        FollowService.followUser(profile2, profile1);
        assertEquals(List.of(profile2.getUserId()), profile1.getFollowing());
        assertEquals(List.of(profile2.getUserId()), profile1.getFollowers());
        assertEquals(List.of(profile2.getUserId()), profile1.getFriends());
        assertEquals(List.of(profile1.getUserId()), profile2.getFollowers());
        assertEquals(List.of(profile1.getUserId()), profile2.getFollowing());
        assertEquals(List.of(profile1.getUserId()), profile2.getFriends());
    }

    @Test
    void testUnfollow(){
        UserProfile profile1 = new UserProfile("profile1");
        profile1.setUserId(new UUID(1, 1));

        UserProfile profile2 = new UserProfile("profile1");
        profile2.setUserId(new UUID(2, 2));

        assertFalse(FollowService.unfollowUser(profile1, profile2));
        FollowService.followUser(profile1, profile2);
        assertTrue(FollowService.unfollowUser(profile1, profile2));
        assertEquals(List.of(), profile1.getFollowing());
        assertEquals(List.of(), profile1.getFollowers());

        FollowService.followUser(profile1, profile2);
        FollowService.followUser(profile2, profile1);
        assertTrue(FollowService.unfollowUser(profile2, profile1));
        assertFalse(FollowService.unfollowUser(profile1, profile1));

        assertEquals(List.of(), profile1.getFriends());
        assertEquals(List.of(), profile2.getFriends());
    }
}
