package anstart.gokarty.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Simple file loader. Loads file as a {@link String}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StringFileLoader {

    private final ResourceLoader resourceLoader;

    /**
     * Loads file from given path.
     *
     * @param path    path to a file
     * @param charset a charset
     * @return file as String
     */
    public String loadFileAsString(String path, Charset charset) {
        Resource resource = resourceLoader.getResource("classpath:" + path);
        try (Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), charset))) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            log.error("Cannot load file");
            throw new UncheckedIOException("Cannot load file", e);
        }
    }

}
