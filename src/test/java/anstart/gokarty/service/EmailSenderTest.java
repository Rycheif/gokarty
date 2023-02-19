package anstart.gokarty.service;

import anstart.gokarty.exception.EmailCouldNotBeSentException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderTest {

    @Mock
    private JavaMailSender mailSender;
    private EmailSender emailSender;

    @BeforeEach
    void setUp() {
        emailSender = new EmailSender(mailSender);
        ReflectionTestUtils.setField(emailSender, "from", "test@email.com");
    }

    @Test
    void givenCorrectParametersShouldSendEmail() throws MessagingException {
        // given
        String to = "otherTest@email.com";
        String subject = "test";
        String body = """
            <html>
                <body>
                Test
                </body>
            </html>
            """;

        MimeMessage email = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(email);
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(email, "utf-8");
        mimeMessageHelper.setText(body, true);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setFrom("test@email.com");

        // when
        emailSender.sendMailWithHtmlBody(to, subject, body);

        // then
        verify(mailSender, times(1)).send(email);
    }

    @Test
    void givenMessagingExceptionShouldCatchAndRethrowEmailCouldNotBeSentException() {
        // given
        String to = "";
        String subject = "test";
        String body = """
            <html>
                <body>
                Test
                </body>
            </html>
            """;

        MimeMessage email = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(email);

        // when // then
        Assertions.assertThrows(
            EmailCouldNotBeSentException.class,
            () -> emailSender.sendMailWithHtmlBody(to, subject, body));
    }

}
