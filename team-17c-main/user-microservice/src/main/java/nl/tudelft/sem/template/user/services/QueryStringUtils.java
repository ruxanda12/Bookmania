package nl.tudelft.sem.template.user.services;


import org.springframework.stereotype.Service;

/**
 * Custom queries with counts will return
 * Something like [name],[count]
 * We only want the name part
 * Only used in Analytics part for now.
 */
@Service
public class QueryStringUtils {

    private static final String ERROR_MSG = "ERROR";

    /**
     * Retrieve the string before a last character.
     *
     * @param str string to be modified
     * @return the required part of the string with message or error message
     */
    public static String modifyString(String str, String message) {
        if (InputCheck.isNullOrEmpty(str) || InputCheck.isNullOrEmpty(message)) {
            return ERROR_MSG;
        }
        int lastIndex = str.lastIndexOf(',');
        if (lastIndex == -1) {
            return ERROR_MSG;
        }

        // for example: Genre2 - 23 favorites
        return str.substring(0, lastIndex) + " - " + str.substring(lastIndex + 1) + " " + message;
    }
}
