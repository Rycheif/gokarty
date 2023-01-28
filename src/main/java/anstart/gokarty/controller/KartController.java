package anstart.gokarty.controller;

import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.dto.KartDto;
import anstart.gokarty.service.KartService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling the requests regarding {@link anstart.gokarty.model.Kart} entity
 */

@Slf4j
@AllArgsConstructor
@RequestMapping("api/")
@RestController
public class KartController {

    private final KartService kartService;

    /**
     * Returns a kart. If kart does not exist throws {@link anstart.gokarty.exception.EntityNotFoundException}
     *
     * @param kartId valid kart's id
     * @return user as {@link KartDto}
     */
    @GetMapping("/kart/{kartId}")
    public ResponseEntity<KartDto> getKartById(@PathVariable long kartId) {
        log.info("Getting kart with id {}", kartId);
        return kartService.getKartById(kartId);
    }

    /**
     * Returns list of all karts in the database
     *
     * @return list of karts
     */
    @GetMapping("/karts")
    public List<KartDto> getKarts() {
        log.info("Getting karts");
        return kartService.getKarts();
    }

    /**
     * Updates kart entity in the database with new given information
     *
     * @param kartDto update information about the kart. Things that are not meant to be changed should be null
     * @return message which kart was changed and timestamp
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping("/kart/updateKartData")
    public ResponseEntity<MessageWithTimestamp> updateKartData(@RequestBody KartDto kartDto) {
        log.info("Updating kart with id {}", kartDto.getId());
        return kartService.updateKartData(kartDto);
    }

    /**
     * Creates new kart from the given DTO
     *
     * @param kartDto new kart
     * @return message that the kart was created and timestamp
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PostMapping("/newKart")
    public ResponseEntity<MessageWithTimestamp> newKart(@RequestBody KartDto kartDto) {
        log.info("Creating new kart with id: {}", kartDto.getId());
        return kartService.createNewKart(kartDto);
    }

}
