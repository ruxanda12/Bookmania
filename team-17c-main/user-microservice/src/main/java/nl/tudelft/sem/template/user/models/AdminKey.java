package nl.tudelft.sem.template.user.models;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AdminKey {
    @Id
    private UUID key;

    /**
     * Equals method for AdminKey objects.
     *
     * @param o other Object
     * @return boolean for equality
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminKey adminKey = (AdminKey) o;
        return Objects.equals(key, adminKey.key);
    }

    /**
     * Hashcode method for AdminKey.
     *
     * @return a hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
