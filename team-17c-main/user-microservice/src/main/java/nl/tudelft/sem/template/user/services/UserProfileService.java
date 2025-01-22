package nl.tudelft.sem.template.user.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.template.user.database.UserProfileRepository;
import nl.tudelft.sem.template.user.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserProfileService {
    private final UserProfileRepository profileRepo;
    private final RestTemplate restTemplate;
    private final String bookshelvesServiceUrl = "http://localhost:8080/bookshelves";

    @Autowired
    public UserProfileService(UserProfileRepository profileRepo, RestTemplate restTemplate) {
        this.profileRepo = profileRepo;
        this.restTemplate = restTemplate;
    }

    public List<UserProfile> findAll() {
        return profileRepo.findAll();
    }

    public Optional<UserProfile> findById(UUID userId) {
        return profileRepo.findById(userId);
    }

    public UserProfile save(UserProfile userProfile) {
        return profileRepo.save(userProfile);
    }

    public void deleteById(UUID userId) {
        profileRepo.deleteById(userId);
    }

    /**
     * Return list of profiles matching with certain conditions.
     *
     * @param userProfile the profile to compare to
     * @return the example list to use in the profileController
     */
    public Example<UserProfile> matchProfiles(UserProfile userProfile) {
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withIgnoreCase()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        return Example.of(userProfile, matcher);
    }


    /**
     * Check if a book exists by calling the book endpoint for the bookshelf microservice.
     *
     * @param bookId the ID of the book to check
     * @return whether the book exists
     */
    public boolean bookExists(String bookId) {
        // find the book using the Bookshelf microservice
        String bookUrl = bookshelvesServiceUrl + "/book/{bookId}";
        ResponseEntity<Void> response;
        try {
            response = restTemplate.getForEntity(bookUrl, Void.class, bookId);
        } catch (HttpClientErrorException e) {
            System.out.println("Bookshelf microservice cannot be found.");
            return false;
        }

        // check if the book exists in the system
        return response.getStatusCode().is2xxSuccessful();
    }

    /**
     * Get available genres.
     *
     * @return the ArrayList of available genres.
     */
    public List<String> availableGenre() {
        // find the genre using the Bookshelf microservice
        String genreUrl = bookshelvesServiceUrl + "/genre/all";
        ResponseEntity<String[]> response;
        try {
            response = restTemplate.getForEntity(genreUrl, String[].class);
        } catch (HttpClientErrorException e) {
            System.out.println("Bookshelf microservice cannot be found.");
            return new ArrayList<>();
        }

        // check if the genre exists in the system
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // get the list of genres available in the system
            return Arrays.asList(response.getBody());
        } else {
            return new ArrayList<>();
        }
    }
}

