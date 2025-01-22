package nl.tudelft.sem.template.user.services;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseStatusTest {
    @Test
    void testAll(){
        assertEquals(HttpStatus.UNAUTHORIZED, ResponseStatus.unauthorized().getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, ResponseStatus.badRequest().getStatusCode());
        assertEquals(HttpStatus.OK, ResponseStatus.success().getStatusCode());
    }
}
