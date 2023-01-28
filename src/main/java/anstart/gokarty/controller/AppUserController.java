package anstart.gokarty.controller;

import anstart.gokarty.model.AppUser;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.UpdateUserRolesPayload;
import anstart.gokarty.payload.dto.AppUserDto;
import anstart.gokarty.service.AppUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling the requests regarding {@link AppUser} entity
 */

@Slf4j
@AllArgsConstructor
@RequestMapping("api/")
@RestController
public class AppUserController {

    private final AppUserService appUserService;

    /**
     * Returns a user. If user does not exist throws {@link anstart.gokarty.exception.EntityNotFoundException}
     *
     * @param userId  valid user's id
     * @param appUser class implementing {@link org.springframework.security.core.userdetails.UserDetails} interface. Injected by Spring
     * @return user as {@link AppUserDto}
     */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<AppUserDto> getUserById(@PathVariable long userId, @AuthenticationPrincipal AppUser appUser) {
        log.info("Getting user with id {}", userId);
        return appUserService.getUserById(userId, appUser);
    }

    /**
     * Returns list of users in the form of a page.
     *
     * @param page page number
     * @param size size of the page
     * @return page of the users
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/users")
    public Page<AppUserDto> getUsers(
        @RequestParam int page,
        @RequestParam int size) {

        log.info("Getting users from the {} page of size {}", page, size);
        return appUserService.getUsers(page, size);
    }

    /**
     * Locks the suer with given id so that user cannot log in
     *
     * @param userId valid user id
     * @return message which user was locked and timestamp
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/user/lock/{userId}")
    public ResponseEntity<MessageWithTimestamp> lockUser(@PathVariable long userId) {
        log.info("Locking user with id {}", userId);
        return appUserService.lockUser(userId);
    }

    /**
     * Check if given email is not used by other user
     *
     * @param email valid email address
     * @return true if email is free
     */
    @GetMapping("/user/checkEmailAvailability")
    public Boolean checkEmailAvailability(@RequestParam String email) {
        log.info("Is email {} available", email);
        return appUserService.isEmailAvailable(email);
    }

    /**
     * Updates user entity in the database with new given information
     *
     * @param appUserDto update information about the user. Things that are not meant to be changed should be null
     * @return message which user was changed and timestamp
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping("/user/updateUserInfo")
    public ResponseEntity<MessageWithTimestamp> updateUsersPersonalData(@RequestBody AppUserDto appUserDto) {
        log.info("Updating user with id {}", appUserDto.getId());
        return appUserService.updateUsersPersonalData(appUserDto);
    }

    /**
     * Updates user's roles.
     *
     * @param payload which user should be changed and their new roles
     * @return message which user was changed and timestamp
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/user/updateUsersRoles")
    public ResponseEntity<MessageWithTimestamp> updateUsersRoles(@RequestBody UpdateUserRolesPayload payload) {
        log.info("Updating user with id {} with roles {}", payload.userId(), payload.roleName());
        return appUserService.updateUsersRoles(payload);
    }

}
