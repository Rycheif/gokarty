package anstart.gokarty.service;


import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.exception.ForbiddenContentException;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.model.Track;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.dto.TrackDto;
import anstart.gokarty.repository.TrackRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;


    public ResponseEntity<TrackDto> getTrackById(long id, AppUser appUser) {
        if (!canUserSeeContent(id, appUser)) {
            log.error("This user can't see this content");
            throw new ForbiddenContentException("This user can't see this content");
        }
        if (id < 0) {
            log.error("Incorrect id {}", id);
            throw new IllegalArgumentException(String.format("Incorrect id %d", id));
        }

        Optional<Track> trackOptional = trackRepository.findById(id);
        if (trackOptional.isPresent()) {
            return new ResponseEntity<>(
                trackOptional.map(track ->
                    new TrackDto(track.id(), track.length())).get(),
                HttpStatus.OK);
        }

        log.error("Track with id {} doesn't exist", id);
        throw new EntityNotFoundException(
            String.format("Track with id %d doesn't exist", id));
    }

    public Page<TrackDto> getTracks() {
        PageRequest pageRequest = PageRequest.of(0, 999);

        return trackRepository.findAll(pageRequest)
            .map(track ->
                new TrackDto(track.id(), track.length())
            );
    }

    public ResponseEntity<MessageWithTimestamp> updateTrackData(TrackDto trackDto) {
        if (null == trackDto.getId() || trackDto.getId() < 0) {
            log.error("Track id {} is not correct", trackDto.getId());
            throw new IllegalArgumentException(
                String.format("Track id %d is not correct", trackDto.getId()));
        }

        Track existing = trackRepository.findById(trackDto.getId())
            .orElseThrow(() -> {
                throw new EntityNotFoundException(
                    String.format("Track with id %d doesn't exist", trackDto.getId()));
            });


        if (null != trackDto.getLength()) {
            existing.length(trackDto.getLength());
        }


        trackRepository.save(existing);

        log.info("Track with id {} has been changed", trackDto.getId());
        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                String.format("Track with id %d has been changed",
                    trackDto.getId())),
            HttpStatus.OK);
    }


    public ResponseEntity<?> createNewTrack(TrackDto trackDto) {


        if (trackRepository.findById(trackDto.getId()).isPresent()) {
            log.error("Id: {} is occupied", trackDto.getId());
            return new ResponseEntity<>(
                new MessageWithTimestamp(
                    Instant.now(),
                    String.format("Id: %d is occupied", trackDto.getId())),
                HttpStatus.FORBIDDEN);
        }

        // TODO: Reszta metody po dodaniu Spring Security

        return null;
    }


    private boolean canUserSeeContent(long id, AppUser appUser) {
        return appUser.getId() == id
            || appUser.getAuthorities()
            .stream()
            .anyMatch(grantedAuthority -> grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_ADMIN"))
                || grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    }

}
