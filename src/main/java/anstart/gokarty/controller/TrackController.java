package anstart.gokarty.controller;

import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.dto.TrackDto;
import anstart.gokarty.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling the requests regarding {@link anstart.gokarty.model.Track} entity
 */

@Slf4j
@AllArgsConstructor
@RequestMapping("api/")
@RestController
public class TrackController {
    private final TrackService trackService;

    /**
     * Returns a track. If track does not exist throws {@link anstart.gokarty.exception.EntityNotFoundException}
     *
     * @param trackId valid track's id
     * @return user as {@link TrackDto}
     */
    @GetMapping("/track/{trackId}")
    public ResponseEntity<TrackDto> getUserById(@PathVariable long trackId) {
        log.info("Getting track with id {}", trackId);
        return trackService.getTrackById(trackId);
    }

    /**
     * Returns list of all tracks in the database
     *
     * @return list of tracks
     */
    @GetMapping("/tracks")
    public List<TrackDto> getTracks() {
        log.info("Getting tracks");
        return trackService.getTracks();
    }

    /**
     * Updates track entity in the database with new given information
     *
     * @param trackDto update information about the track. Things that are not meant to be changed should be null
     * @return message which track was changed and timestamp
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping("/track/updateTrackInfo")
    public ResponseEntity<MessageWithTimestamp> updateTrackData(@RequestBody TrackDto trackDto) {
        log.info("Updating track with id {}", trackDto.getId());
        return trackService.updateTrackData(trackDto);
    }

    /**
     * Creates new track from the given DTO
     *
     * @param trackDto new track
     * @return message that the track was created and timestamp
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PostMapping("/newTrack")
    public ResponseEntity<MessageWithTimestamp> newTrack(@RequestBody TrackDto trackDto) {
        log.info("Creating new track with id: {}", trackDto.getId());
        return trackService.createNewTrack(trackDto);
    }


}
