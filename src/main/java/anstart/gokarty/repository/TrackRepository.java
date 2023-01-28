package anstart.gokarty.repository;

import anstart.gokarty.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository form {@link Track} entity.
 */
public interface TrackRepository extends JpaRepository<Track, Long> {
}
