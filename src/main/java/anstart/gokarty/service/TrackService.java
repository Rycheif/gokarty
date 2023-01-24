package anstart.gokarty.service;


import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.model.Track;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.dto.TrackDto;
import anstart.gokarty.repository.TrackRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;

    public ResponseEntity<TrackDto> getTrackById(long id) {
        if (id < 0) {
            log.error("Incorrect id {}", id);
            throw new IllegalArgumentException(String.format("Incorrect id %d", id));
        }

        Optional<Track> trackOptional = trackRepository.findById(id);
        if (trackOptional.isPresent()) {
            return new ResponseEntity<>(
                trackOptional.map(track ->
                    new TrackDto(track.getId(), track.getLength())).get(),
                HttpStatus.OK);
        }

        log.error("Track with id {} doesn't exist", id);
        throw new EntityNotFoundException(
            String.format("Track with id %d doesn't exist", id));
    }

    public List<TrackDto> getTracks() {

        return trackRepository.findAll()
            .stream()
            .map(track ->
                new TrackDto(track.getId(), track.getLength()))
            .toList();
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
            existing.setLength(trackDto.getLength());
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


    public ResponseEntity<MessageWithTimestamp> createNewTrack(TrackDto trackDto) {
        trackRepository.save(new Track(trackDto.getLength()));

        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                "Track created"),
            HttpStatus.CREATED);
    }

}
