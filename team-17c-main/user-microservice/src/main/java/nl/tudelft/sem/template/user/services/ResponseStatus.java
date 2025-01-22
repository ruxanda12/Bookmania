package nl.tudelft.sem.template.user.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Some default response entities, saves some lines.
 */
@Service
public class ResponseStatus {
    /**
     * Generic function for success response.
     *
     * @param <T>  type
     * @return response
     */
    public static <T> ResponseEntity<T> success() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Generic function for unauthorized response.
     *
     * @param <T>  type
     * @return response
     */
    public static <T> ResponseEntity<T> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Generic function for bad-request response.
     *
     * @param <T>  type
     * @return response
     */
    public static <T> ResponseEntity<T> badRequest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
