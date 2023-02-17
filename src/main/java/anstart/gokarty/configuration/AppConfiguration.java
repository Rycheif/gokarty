package anstart.gokarty.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

}
