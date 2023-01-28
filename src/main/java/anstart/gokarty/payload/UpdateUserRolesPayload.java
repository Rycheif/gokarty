package anstart.gokarty.payload;

/**
 * Payload for updating user's roles
 *
 * @param userId   valid user id which roles are being changed
 * @param roleName role name
 */
public record UpdateUserRolesPayload(Long userId, String roleName) {
}
