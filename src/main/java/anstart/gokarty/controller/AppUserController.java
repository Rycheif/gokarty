package anstart.gokarty.controller;

import anstart.gokarty.auth.AppUserDetails;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.dto.AppUserDto;
import anstart.gokarty.service.AppUserService;
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
public class AppUserController {

    private final AppUserService appUserService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<AppUserDto> getUserById(@PathVariable long userId, @AuthenticationPrincipal AppUserDetails appUser) {
        log.info("Getting user with id {}", userId);
        return appUserService.getUserById(userId, appUser);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/users")
    public Page<AppUserDto> getUsers(
        @RequestParam int page,
        @RequestParam int size) {

        log.info("Getting user from the {} page of size {}", page, size);
        return appUserService.getUsers(page, size);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/user/lock/{userId}")
    public ResponseEntity<MessageWithTimestamp> lockUser(@PathVariable long userId) {
        log.info("Locking user with id {}", userId);
        return appUserService.lockUser(userId);
    }

    @GetMapping("/user/checkEmailAvailability")
    public Boolean checkEmailAvailability(@RequestParam String email) {
        log.info("Is email {} available", email);
        return appUserService.isEmailAvailable(email);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping("/user/updateUserInfo")
    public ResponseEntity<MessageWithTimestamp> updateUsersPersonalData(@RequestBody AppUserDto appUserDto) {
       log.info("Updating user with id {}", appUserDto.getId());
       return appUserService.updateUsersPersonalData(appUserDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/user/updateUsersRoles")
    public ResponseEntity<MessageWithTimestamp> updateUsersRoles(@RequestBody AppUserDto appUserDto) {
        log.info("Updating user with id {} with roles {}", appUserDto.getId(), appUserDto.getAppRoles());
        return appUserService.updateUsersRoles(appUserDto);
    }

}
