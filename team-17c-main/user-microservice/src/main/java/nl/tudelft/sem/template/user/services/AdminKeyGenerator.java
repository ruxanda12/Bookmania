package nl.tudelft.sem.template.user.services;

import java.util.Random;
import java.util.UUID;
import nl.tudelft.sem.template.user.models.AdminKey;
import org.springframework.stereotype.Service;

/**
 * Normally we can just let spring generate keys, but now we have the ability to generate same keys.
 * multiple times.
 */
@Service
public class AdminKeyGenerator {
    /**
     * Generate a new AdminKey, specifying a Random object.
     *
     * @return new AdminKey object
     */
    public static AdminKey generateKey(Random rnd) {
        long mostSignificantBits = rnd.nextLong();
        long leastSignificantBits = rnd.nextLong();
        UUID uuid = new UUID(mostSignificantBits, leastSignificantBits);

        AdminKey adminKey = new AdminKey();
        adminKey.setKey(uuid);

        return adminKey;
    }
}
