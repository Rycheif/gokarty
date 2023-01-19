package anstart.gokarty.controller;

import anstart.gokarty.auth.AppUserDetails;
import anstart.gokarty.model.Track;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.dto.AppUserDto;
import anstart.gokarty.payload.dto.ReservationDto;
import anstart.gokarty.payload.dto.TrackDto;
import anstart.gokarty.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RequestMapping("api/")
@RestController
public class TrackController {
    private final TrackService trackService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/track/{trackId}")
    public ResponseEntity<TrackDto> getUserById(@PathVariable long trackId, @AuthenticationPrincipal AppUserDetails appUser) {
        log.info("Getting track with id {}", trackId);
        return trackService.getTrackById(trackId, appUser);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/tracks")
    public Page<TrackDto> getTracks() {

        log.info("Getting users");
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
    public ResponseEntity<?> newTrack(@RequestBody TrackDto trackDto) {
        log.info("Creating new track with id: {}", trackDto.getId());
        return trackService.createNewTrack(trackDto);
    }


}
