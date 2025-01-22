package nl.tudelft.sem.template.user.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.template.user.api.AuthorApi;
import nl.tudelft.sem.template.user.model.UserProfile;
import nl.tudelft.sem.template.user.services.AuthorService;
import nl.tudelft.sem.template.user.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

@RestController
@RequestMapping("/user/author")
public class AuthorController implements AuthorApi {
    private final UserProfileService profileService;

    private final AuthorService authorService;

    /**
     * Initialises the AuthorController class.
     *
     * @param profileService the required profile service
     * @param authorService  the required author service
     */
    @Autowired
    public AuthorController(UserProfileService profileService, AuthorService authorService) {
        this.profileService = profileService;
        this.authorService = authorService;
    }

    /**
     * Returns an empty optional.
     *
     * @return an empty optional
     */
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * Returns a list of all authors in the DB.
     *
     * @return list of all authors
     */
    @Override
    @GetMapping(path = {"/all"})
    public ResponseEntity<List<UserProfile>> getAuthors() {
        List<UserProfile> authors;
        authors = authorService.findAuthors();
        if (authors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(authors);
    }

    /**
     * Returns the Author from DB with the given ID.
     *
     * @param authorId ID of author to find (required)
     * @return the specific author in a single-element list
     */
    @Override
    @GetMapping(path = {"/view/{author_id}"})
    public ResponseEntity<List<UserProfile>> getAuthorByID(@PathVariable("author_id") UUID authorId) {
        Optional<UserProfile> u = profileService.findById(authorId);
        if (u.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (u.get().getRole() != UserProfile.RoleEnum.AUTHOR) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<UserProfile> author = new ArrayList<>();
        author.add(u.get());
        return ResponseEntity.ok(author);
    }

}
