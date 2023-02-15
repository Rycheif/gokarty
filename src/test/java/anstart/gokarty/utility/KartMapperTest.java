package anstart.gokarty.utility;

import anstart.gokarty.model.Difficulty;
import anstart.gokarty.model.Kart;
import anstart.gokarty.payload.dto.KartDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KartMapperTest {

    @Test
    void whenCorrectKartProvidedShouldMapToKartDto() {
        // given
        Kart kart = new Kart("Kart", Difficulty.Easy);
        kart.setId(1L);
        KartDto testDto = new KartDto(1L, "Kart", Difficulty.Easy);

        // when
        KartDto kartDto = KartMapper.mapToKartDto(kart);

        //then
        assertEquals(kartDto, testDto);
    }

}
