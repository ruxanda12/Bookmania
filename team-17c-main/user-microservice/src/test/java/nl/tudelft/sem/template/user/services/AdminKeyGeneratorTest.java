package nl.tudelft.sem.template.user.services;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminKeyGeneratorTest {
    @Test
    public void testKeyGenerator() {
        assertEquals(AdminKeyGenerator.generateKey(new Random(10)).getKey().toString(),
                "bafd7ad2-7215-16f8-41ff-5d3a69b4a5b4");
    }
}
