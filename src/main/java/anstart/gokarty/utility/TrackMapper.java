package anstart.gokarty.utility;

import anstart.gokarty.model.Track;
import anstart.gokarty.payload.dto.TrackDto;

public class TrackMapper {

    public static TrackDto mapToTrackDto(Track track) {
        return new TrackDto(track.id(), track.length());
    }

}
