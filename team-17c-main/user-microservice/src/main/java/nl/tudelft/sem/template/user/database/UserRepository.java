package nl.tudelft.sem.template.user.database;

import java.util.UUID;
import nl.tudelft.sem.template.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Boolean existsByEmail(String email); //Checks if there are any users with given email

    User findUserByEmail(String email);
}
