package anstart.gokarty.service;


import anstart.gokarty.auth.AppUserDetails;
import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.exception.ForbiddenContentException;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.model.Kart;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.dto.AppUserDto;
import anstart.gokarty.payload.dto.KartDto;
import anstart.gokarty.payload.dto.TrackDto;
import anstart.gokarty.repository.KartRepository;
import anstart.gokarty.utility.AppUserMapper;
import anstart.gokarty.utility.KartMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class KartService {

    private final KartRepository kartRepository;

    public ResponseEntity<KartDto> getKartById(long id, AppUserDetails appUser) {
        if (!canUserSeeContent(id, appUser)) {
            log.error("This user can't see this content");
            throw new ForbiddenContentException("This user can't see this content");
        }
        if (id < 0) {
            log.error("Incorrect id {}", id);
            throw new IllegalArgumentException(String.format("Incorrect id %d", id));
        }

        Optional<Kart> kartOptional = kartRepository.findById(id);
        if (kartOptional.isPresent()) {
            return new ResponseEntity<>(
                    kartOptional.map(kart ->
                            new KartDto(kart.id(), kart.name(), kart.difficultyLevel())).get(),
                    HttpStatus.OK
            );
        }

        log.error("Kart with id {} doesn't exist", id);
        throw new EntityNotFoundException(
                String.format("Kart with id %d doesn't exist", id));

    }

    public Page<KartDto> getKarts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return kartRepository.findAll(pageRequest)
                .map(kart ->
                        new KartDto(kart.id(), kart.name(), kart.difficultyLevel())
                );
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
            existing.name(kartDto.getName());
        }

        if (null != kartDto.getDifficultyLevel()) {
            existing.difficultyLevel(kartDto.getDifficultyLevel());
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


        if (kartRepository.findById(kartDto.getId()).isPresent()) {
            log.error("Id: {} is occupied", kartDto.getId());
            return new ResponseEntity<>(
                    new MessageWithTimestamp(
                            Instant.now(),
                            String.format("Id: %d is occupied", kartDto.getId())),
                    HttpStatus.FORBIDDEN);
        }

        // TODO: Reszta metody po dodaniu Spring Security

        return null;
    }


    private boolean canUserSeeContent(long id, AppUserDetails appUser) {
        return appUser.getId() == id
                || appUser.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        || grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    }

}
