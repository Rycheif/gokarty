package anstart.gokarty.utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailValidatorTest {

    @Test
    void whenCorrectEmailProvidedShouldReturnTrue() {
        // given
        String email = "jk@gmail.com";

        // when // then
        assertTrue(EmailValidator.isEmailValid(email));
    }

    @ParameterizedTest
    @CsvSource({"jk@gmail,com", "jkgmail.com", "jkgmail.com", "jkgmail.com"})
    void whenIncorrectEmailProvidedShouldReturnFalse(String value) {
        // given // when // then
        assertFalse(EmailValidator.isEmailValid(value));
    }

}
