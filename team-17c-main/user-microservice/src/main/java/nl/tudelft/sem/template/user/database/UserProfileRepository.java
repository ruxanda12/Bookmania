package nl.tudelft.sem.template.user.database;

import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    long countUserProfileByState(UserProfile.StateEnum state);

    long countUserProfileByRole(UserProfile.RoleEnum role);

    // Query explanation:
    // take all favoriteBooks, group them accordingly with their count,
    // sort the count on descending. books get ordered on alphabetical order in case of tie.
    @Query("SELECT favoriteBook, COUNT(favoriteBook) as favCount FROM UserProfile "
        + "WHERE favoriteBook != NULL "
        + "GROUP BY favoriteBook "
        + "ORDER BY favoriteBook, favCount DESC")
    String[] findMostPopularBooks();

    // Take all favorite genres, group them
    // Order on the count descending.
    @Query("SELECT elements(favoriteGenres) as favGenres, COUNT(elements(favoriteGenres)) FROM UserProfile "
        + "GROUP BY favGenres ORDER BY 1, 2 DESC")
    String[] findMostPopularGenres();

    // Take all 'following' from all users and group them
    // Order on the count descending.
    // The correctness of this query relies heavily on the correctness
    // of the /follow and /unfollow implementation
    @Query("SELECT elements(following) as following, COUNT(elements(following)) FROM UserProfile "
        + "GROUP BY following ORDER BY 2 DESC")
    String[] findMostFollowedUsers();

    List<UserProfile> findAllByRole(UserProfile.RoleEnum role);
}


