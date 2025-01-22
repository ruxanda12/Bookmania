package nl.tudelft.sem.template.user.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueryStringUtilsTest {

    private static final String ERROR_MSG = "ERROR";

    @Test
    void testErrors(){
        assertEquals(ERROR_MSG, QueryStringUtils.modifyString(null, "test"));
        assertEquals(ERROR_MSG, QueryStringUtils.modifyString("testing,13", null));
        assertEquals(ERROR_MSG, QueryStringUtils.modifyString("", "test"));
        assertEquals(ERROR_MSG, QueryStringUtils.modifyString("testing,13", ""));
        assertEquals(ERROR_MSG, QueryStringUtils.modifyString("testing", "testing"));
    }

    @Test
    void testCorrect(){
        assertEquals("testing - 1 test?", QueryStringUtils.modifyString("testing,1", "test?"));
        assertEquals("testing,1,2 - 4 test?", QueryStringUtils.modifyString("testing,1,2,4", "test?"));
        assertEquals(",1,2 - 4 test?", QueryStringUtils.modifyString(",1,2,4", "test?"));
        assertEquals("Genre1 - 2174 favorites", QueryStringUtils.modifyString("Genre1,2174", "favorites"));
    }
}
