package cat.tecnocampus.tinderlab2526.persistence;

import cat.tecnocampus.tinderlab2526.domain.ERole;
import cat.tecnocampus.tinderlab2526.domain.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    @Query("""
        select r from Role r where r.name=:role
""")
    Optional<Role> findByName(ERole role);
}
