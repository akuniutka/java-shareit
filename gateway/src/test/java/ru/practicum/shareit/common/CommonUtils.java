package ru.practicum.shareit.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public final class CommonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

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

    public static MediaType getContentType(final HttpStatusCodeException exception) {
        return Optional.ofNullable(exception.getResponseHeaders())
                .map(HttpHeaders::getContentType)
                .orElse(null);
    }

    public static String getBody(final HttpStatusCodeException exception) {
        return exception.getResponseBodyAsString();
    }
}
