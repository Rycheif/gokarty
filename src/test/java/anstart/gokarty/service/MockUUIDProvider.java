package anstart.gokarty.service;

import anstart.gokarty.utility.UUIDProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockUUIDProvider implements UUIDProvider {
    @Override
    public UUID getRandomUUID() {
        return UUID.fromString("0000-00-00-00-000000");
    }
}
