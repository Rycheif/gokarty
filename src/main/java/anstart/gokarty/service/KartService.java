package anstart.gokarty.service;

import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.model.Kart;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.dto.KartDto;
import anstart.gokarty.repository.KartRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class KartService {

    private final KartRepository kartRepository;

    public ResponseEntity<KartDto> getKartById(long id) {
        if (id < 0) {
            log.error("Incorrect id {}", id);
            throw new IllegalArgumentException(String.format("Incorrect id %d", id));
        }

        Optional<Kart> kartOptional = kartRepository.findById(id);
        if (kartOptional.isPresent()) {
            return new ResponseEntity<>(
                kartOptional.map(kart ->
                    new KartDto(kart.getId(), kart.getName(), kart.getDifficultyLevel())).get(),
                HttpStatus.OK
            );
        }

        log.error("Kart with id {} doesn't exist", id);
        throw new EntityNotFoundException(
            String.format("Kart with id %d doesn't exist", id));

    }

    public List<KartDto> getKarts() {
        return kartRepository.findAll()
            .stream()
            .map(kart ->
                new KartDto(kart.getId(), kart.getName(), kart.getDifficultyLevel()))
            .toList();
    }

    public ResponseEntity<MessageWithTimestamp> updateKartData(KartDto kartDto) {
        if (null == kartDto.getId() || kartDto.getId() < 0) {
            log.error("Kart id {} is not correct", kartDto.getId());
            throw new IllegalArgumentException(
                String.format("Kart id %d is not correct", kartDto.getId()));
        }

        Kart existing = kartRepository.findById(kartDto.getId())
            .orElseThrow(() -> {
                throw new EntityNotFoundException(
                    String.format("Kart with id %d doesn't exist", kartDto.getId()));
            });

        if (null != kartDto.getName()) {
            existing.setName(kartDto.getName());
        }

        if (null != kartDto.getDifficultyLevel()) {
            existing.setDifficultyLevel(kartDto.getDifficultyLevel());
        }

        kartRepository.save(existing);

        log.info("Kart with id {} has been changed", kartDto.getId());
        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                String.format("Kart with id %d has been changed",
                    kartDto.getId())),
            HttpStatus.OK);
    }

    public ResponseEntity<?> createNewKart(KartDto kartDto) {
        if (kartRepository.findByName(kartDto.getName()).isPresent()) {
            log.error("Id: {} is occupied", kartDto.getId());
            return new ResponseEntity<>(
                new MessageWithTimestamp(
                    Instant.now(),
                    String.format("Id: %d is occupied", kartDto.getId())),
                HttpStatus.FORBIDDEN);
        }

        kartRepository.save(new Kart(kartDto.getName(), kartDto.getDifficultyLevel()));

        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                "Kart created"),
            HttpStatus.CREATED);
    }

}
