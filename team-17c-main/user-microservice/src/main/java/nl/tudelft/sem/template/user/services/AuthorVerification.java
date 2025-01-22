package nl.tudelft.sem.template.user.services;

import org.springframework.stereotype.Service;

@Service
public class AuthorVerification {

    /**
     * Verify whether a given key-phrase contains "author", case-insensitive.
     *
     * @param keyPhrase key-phrase given
     * @return a boolean for verification
     */
    public static boolean verifyAuthorKeyPhrase(String keyPhrase) {
        if (InputCheck.isNullOrEmpty(keyPhrase)) {
            return false;
        }
        return keyPhrase.toLowerCase().contains("author");
    }
}
