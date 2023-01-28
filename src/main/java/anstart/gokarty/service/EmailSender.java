package anstart.gokarty.service;

import anstart.gokarty.exception.EmailCouldNotBeSentException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Class for sending emails. Sender can be defined in the application.yaml file
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;
    @Value("${email.from}")
    private String from;

    /**
     * Sends email with given HTML body.
     *
     * @param to      recipient's email address
     * @param subject subject
     * @param body    HTML body
     */
    public void sendMailWithHtmlBody(String to, String subject, String body) {
        MimeMessage email = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(email, "utf-8");
        try {
            mimeMessageHelper.setText(body, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom(from);
            mailSender.send(email);
        } catch (MessagingException e) {
            log.error("Email could not be sent", e);
            throw new EmailCouldNotBeSentException("Email could not be sent", e);
        }
    }

}
