package anstart.gokarty.repository;

import anstart.gokarty.model.Kart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface KartRepository extends JpaRepository<Kart, Long> {

    Optional<Kart> findByName(String name);
    @Query(value = "SELECT * FROM gokarty.kart WHERE name in :names", nativeQuery = true)
    Set<Kart> findKartsByNames(@Param("names") Set<String> kartNames);
}
