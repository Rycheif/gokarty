package anstart.gokarty.controller;

import anstart.gokarty.dto.AppUserDto;
import anstart.gokarty.service.AppUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RequestMapping("api/users")
@RestController
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("/{id}")
    public ResponseEntity<AppUserDto> getUserById(@PathVariable long id) {
        log.info("Getting user with id {}", id);
        return appUserService.getUserById(id);
    }

    @GetMapping("/checkEmailAvailability")
    public Boolean checkEmailAvailability(@RequestParam String email) {
        log.info("Is email {} available", email);
        return appUserService.isEmailAvailable(email);
    }

    @PatchMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody AppUserDto appUserDto) {
       log.info("Updating user with id {}", appUserDto.getId());
       return appUserService.modifyUser(appUserDto);
    }

}
