package anstart.gokarty.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

@Slf4j
@Component
@RequiredArgsConstructor
public class StringFileLoader {

    private final ResourceLoader resourceLoader;

    public String loadEmailFile(String path, Charset charset) {
        Resource resource = resourceLoader.getResource("classpath:" + path);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), charset)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            log.error("Cannot load file with email");
            throw new UncheckedIOException("Cannot load file with email", e);
        }
    }

}
