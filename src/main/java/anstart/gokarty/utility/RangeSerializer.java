package anstart.gokarty.utility;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.vladmihalcea.hibernate.type.range.Range;
import com.vladmihalcea.hibernate.type.util.JsonSerializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class RangeSerializer extends StdSerializer<Range<LocalDateTime>> implements JsonSerializer {

    public RangeSerializer() {
        this(null);
    }

    public RangeSerializer(Class<Range<LocalDateTime>> rangeClass) {
        super(rangeClass);
    }

    @Override
    public void serialize(
        Range<LocalDateTime> localDateTimeRange,
        JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStringField("start", localDateTimeRange.lower().toString());
        jsonGenerator.writeStringField("end", localDateTimeRange.upper().toString());
    }

    @Override
    public <T> T clone(T t) {
        return null;
    }
}
