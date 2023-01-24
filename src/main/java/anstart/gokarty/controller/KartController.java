package anstart.gokarty.controller;


import anstart.gokarty.model.AppUser;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.dto.KartDto;
import anstart.gokarty.service.KartService;
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
public class KartController {

    private final KartService kartService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/kart/{kartId}")
    public ResponseEntity<KartDto> getKartById(@PathVariable long kartId, @AuthenticationPrincipal AppUser appUser) {
        log.info("Getting kart with id {}", kartId);
        return kartService.getKartById(kartId, appUser);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/karts")
    public Page<KartDto> getKarts() {
        log.info("Getting karts");
        return kartService.getKarts();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping("/kart/updateKartData")
    public ResponseEntity<MessageWithTimestamp> updateKartData(@RequestBody KartDto kartDto) {
        log.info("Updating kart with id {}", kartDto.getId());
        return kartService.updateKartData(kartDto);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PostMapping("/newKart")
    public ResponseEntity<?> newKart(@RequestBody KartDto kartDto) {
        log.info("Creating new kart with id: {}", kartDto.getId());
        return kartService.createNewKart(kartDto);
    }


}
