package anstart.gokarty.utility;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class StringFileLoaderTest {

    @Autowired
    StringFileLoader fileLoader;

    @Test
    void givenCorrectPathShouldLoadAFile() {
        // given
        String path = "test_file.txt";

        // when
        String loadedFile = fileLoader.loadFileAsString(path, StandardCharsets.UTF_8).trim();

        // given
        assertEquals(loadedFile, "test");
    }
    @Test
    void givenInCorrectPathShouldThrowUncheckedIOException() {
        // given
        String path = "file.txt";

        // when // given
        assertThrows(UncheckedIOException.class, () -> fileLoader.loadFileAsString(path, StandardCharsets.UTF_8));
    }

}
