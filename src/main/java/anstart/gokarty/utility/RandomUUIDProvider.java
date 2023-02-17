package anstart.gokarty.utility;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomUUIDProvider implements UUIDProvider {

    @Override
    public UUID getRandomUUID() {
        return UUID.randomUUID();
    }

}
