package nl.tudelft.sem.template.user.database;

import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.user.models.LogItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogItemRepository extends JpaRepository<LogItem, UUID> {
    // Needed for analytics
    List<LogItem> findTop10ByOrderByTimestampDesc();
}
