package nl.tudelft.sem.template.user.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthorVerificationTest {
    @Test
    public void testKeyphraseTrue() {
        assertTrue(AuthorVerification.verifyAuthorKeyPhrase("I am an author"));
        assertTrue(AuthorVerification.verifyAuthorKeyPhrase("Author I am"));
        assertTrue(AuthorVerification.verifyAuthorKeyPhrase("bla blaAutHoRbeeeee"));
        assertTrue(AuthorVerification.verifyAuthorKeyPhrase("AUTHOR"));
    }

    @Test
    public void testKeyphraseFalse() {
        assertFalse(AuthorVerification.verifyAuthorKeyPhrase("I am not really an autho...."));
        assertFalse(AuthorVerification.verifyAuthorKeyPhrase("Can't believe I am not an ... auth... or..."));
        assertFalse(AuthorVerification.verifyAuthorKeyPhrase(null));
        assertFalse(AuthorVerification.verifyAuthorKeyPhrase(""));
    }
}
