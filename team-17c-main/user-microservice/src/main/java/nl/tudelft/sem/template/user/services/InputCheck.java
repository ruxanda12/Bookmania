package nl.tudelft.sem.template.user.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class InputCheck {

    /**
     * check whether string s in null or empty.
     *
     * @param s the string to be checked
     * @return true if null/empty, false otherwise
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * check whether username is valid.
     *
     * @param username the username to be checked
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        // Regular expression for the username pattern
        String regex = "^[a-zA-Z][a-zA-Z0-9]*$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher object
        Matcher matcher = pattern.matcher(username);

        // Check if the username matches the pattern
        return matcher.matches();
    }
}
