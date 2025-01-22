package nl.tudelft.sem.template.user.services;

import java.util.List;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    private final UserProfileRepository profileRepo;

    /**
     * Initialise the AuthorService Class.
     *
     * @param profileRepo the profile repository
     */
    @Autowired
    public AuthorService(UserProfileRepository profileRepo) {
        this.profileRepo = profileRepo;
    }

    /**
     * Return all author profiles.
     *
     * @return filtered authors
     */
    public List<UserProfile> findAuthors() {
        return profileRepo.findAllByRole(UserProfile.RoleEnum.AUTHOR);
    }
}
