package anstart.gokarty.service;

import anstart.gokarty.auth.AppUserDetailsService;
import anstart.gokarty.exception.AccountActivationException;
import anstart.gokarty.exception.EmailNotValidException;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.model.EmailConfirmationToken;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.RegistrationPayload;
import anstart.gokarty.utility.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AppUserDetailsService appUserDetailsService;
    private final EmailConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    @Value("${email.confirmation-link}")
    private String linkToConfirmation;
    @Value("${auth-and-security.email-confirmation-token-validity-minutes}")
    private int tokenValidityMinutes;

    public ResponseEntity<MessageWithTimestamp> registerUser(RegistrationPayload registrationPayload) {
        if (!EmailValidator.isEmailValid(registrationPayload.getEmail())) {
            throw new EmailNotValidException(
                String.format("Email %s is not valid", registrationPayload.getEmail()));
        }

        AppUser newAppUser = new AppUser().name(registrationPayload.getUsername())
            .phone(registrationPayload.getPhone())
            .email(registrationPayload.getEmail())
            .password(registrationPayload.getPassword());

        String emailConfirmationToken = appUserDetailsService.registerUserInTheDB(newAppUser);
        CompletableFuture.runAsync(() ->
            {
                log.debug("Sending email");
                emailSender.sendMailWithHtmlBody(
                    registrationPayload.getEmail(),
                    getEmailBody(registrationPayload.getUsername(), emailConfirmationToken));
            })
            .thenAccept(unused -> log.debug("Email was successfully sent"));

        log.debug("User {} was register", registrationPayload.getUsername());

        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                emailConfirmationToken),
            HttpStatus.OK);
    }

    public ResponseEntity<MessageWithTimestamp> activateAccount(String token) {
        EmailConfirmationToken emailConfirmationToken = confirmationTokenService.getToken(token);
        if (null != emailConfirmationToken.confirmedAt()) {
            log.error("Account is activate");
            throw new AccountActivationException("Account is activate");
        }

        Instant expiresAt = emailConfirmationToken.expiresAt();
        if (expiresAt.isBefore(Instant.now())) {
            log.error("Token expired");
            throw new AccountActivationException("Token expired");
        }

        confirmationTokenService.confirmToken(token);
        appUserDetailsService.enableUser(emailConfirmationToken.idAppUser().email());

        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                "Account activated"),
            HttpStatus.OK);
    }

    private String getEmailBody(String username, String token) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
            "\n" +
            "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
            "\n" +
            "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
            "    <tbody><tr>\n" +
            "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
            "        \n" +
            "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
            "          <tbody><tr>\n" +
            "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
            "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
            "                  <tbody><tr>\n" +
            "                    <td style=\"padding-left:10px\">\n" +
            "                  \n" +
            "                    </td>\n" +
            "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
            "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
            "                    </td>\n" +
            "                  </tr>\n" +
            "                </tbody></table>\n" +
            "              </a>\n" +
            "            </td>\n" +
            "          </tr>\n" +
            "        </tbody></table>\n" +
            "        \n" +
            "      </td>\n" +
            "    </tr>\n" +
            "  </tbody></table>\n" +
            "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
            "    <tbody><tr>\n" +
            "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
            "      <td>\n" +
            "        \n" +
            "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
            "                  <tbody><tr>\n" +
            "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
            "                  </tr>\n" +
            "                </tbody></table>\n" +
            "        \n" +
            "      </td>\n" +
            "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
            "    </tr>\n" +
            "  </tbody></table>\n" +
            "\n" +
            "\n" +
            "\n" +
            "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
            "    <tbody><tr>\n" +
            "      <td height=\"30\"><br></td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
            "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
            "        \n" +
            "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + username + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + linkToConfirmation + token + "\">Activate Now</a> </p></blockquote>\n Link will expire in " + tokenValidityMinutes + " minutes. <p>See you soon</p>" +
            "        \n" +
            "      </td>\n" +
            "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td height=\"30\"><br></td>\n" +
            "    </tr>\n" +
            "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
            "\n" +
            "</div></div>";
    }

}
