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

@Slf4j
@AllArgsConstructor
@RequestMapping("api/")
@RestController
public class TrackController {
    private final TrackService trackService;

    @GetMapping("/track/{trackId}")
    public ResponseEntity<TrackDto> getUserById(@PathVariable long trackId) {
        log.info("Getting track with id {}", trackId);
        return trackService.getTrackById(trackId);
    }

    @GetMapping("/tracks")
    public List<TrackDto> getTracks() {
        log.info("Getting tracks");
        return trackService.getTracks();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping("/track/updateTrackInfo")
    public ResponseEntity<MessageWithTimestamp> updateTrackData(@RequestBody TrackDto trackDto) {
        log.info("Updating track with id {}", trackDto.getId());
        return trackService.updateTrackData(trackDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PostMapping("/newTrack")
    public ResponseEntity<MessageWithTimestamp> newTrack(@RequestBody TrackDto trackDto) {
        log.info("Creating new track with id: {}", trackDto.getId());
        return trackService.createNewTrack(trackDto);
    }


}
