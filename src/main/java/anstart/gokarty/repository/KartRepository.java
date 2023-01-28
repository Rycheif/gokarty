package anstart.gokarty.repository;

import anstart.gokarty.model.Kart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

/**
 * JPA repository form {@link Kart} entity.
 */
public interface KartRepository extends JpaRepository<Kart, Long> {

    /**
     * Returns kart with given name
     *
     * @param name kart's name
     * @return kart entity
     */
    Optional<Kart> findByName(String name);

    /**
     * Returns all karts with given names
     *
     * @param kartNames {@link Set} of karts names
     * @return set of all karts with provided names
     */
    @Query(value = "SELECT * FROM gokarty.kart WHERE name IN :names", nativeQuery = true)
    Set<Kart> findKartsByNames(@Param("names") Set<String> kartNames);
}
