package nl.tudelft.sem.template.user.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.UUID;

public class AdminKeyTest {
    @Test
    public void testEquality() {
        AdminKey key1 = new AdminKey();
        AdminKey key2 = new AdminKey();

        UUID uuid = UUID.randomUUID();

        key1.setKey(uuid);
        key2.setKey(uuid);

        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    public void testInequality() {
        AdminKey key1 = new AdminKey();
        AdminKey key2 = new AdminKey();

        Random rnd1 = new Random(100);
        Random rnd2 = new Random(101);

        key1.setKey(new UUID(rnd1.nextLong(), rnd1.nextLong()));
        key2.setKey(new UUID(rnd2.nextLong(), rnd2.nextLong()));

        assertNotEquals(key1, key2);
        assertNotEquals(key1.hashCode(), key2.hashCode());

        assertNotEquals(key1, null);
        assertEquals(key1, key1);
        assertNotEquals(key1, "What?");
    }
}
