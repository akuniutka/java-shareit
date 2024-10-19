package ru.practicum.shareit.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public final class CommonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final long USER_ID = 42L;

    private CommonUtils() {
    }

    public static String loadJson(final String filename, final Class<?> clazz) throws IOException {
        final String expandedFilename = clazz.getSimpleName().toLowerCase() + "/" + filename;
        final ClassPathResource resource = new ClassPathResource(expandedFilename, clazz);
        return Files.readString(resource.getFile().toPath());
    }

    public static void assertLogs(final List<LogListener.Event> events, final String filename,
            final Class<?> clazz) throws IOException, JSONException {
        final String expected = loadJson(filename, clazz);
        final String actual = mapper.writeValueAsString(events);
        JSONAssert.assertEquals(expected, actual, false);
    }
}
