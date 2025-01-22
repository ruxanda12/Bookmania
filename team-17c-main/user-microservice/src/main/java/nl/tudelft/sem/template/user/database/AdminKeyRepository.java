package nl.tudelft.sem.template.user.database;

import java.util.UUID;
import nl.tudelft.sem.template.user.models.AdminKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminKeyRepository extends JpaRepository<AdminKey, UUID> {
}