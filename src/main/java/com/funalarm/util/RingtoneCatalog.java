package com.funalarm.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public final class RingtoneCatalog {
    private static final String SOUNDS_DIR = "/sounds";
    private static final String DEFAULT_RINGTONE = "default.wav";

    private RingtoneCatalog() {
    }

    public static List<String> listAvailable() {
        List<String> names = new ArrayList<>();
        URL url = RingtoneCatalog.class.getResource(SOUNDS_DIR);
        if (url != null && "file".equals(url.getProtocol())) {
            try (Stream<Path> stream = Files.list(Path.of(url.toURI()))) {
                stream.map(path -> path.getFileName().toString())
                        .filter(RingtoneCatalog::isAudioFile)
                        .sorted()
                        .forEach(names::add);
            } catch (IOException | URISyntaxException ignored) {
            }
        }
        if (names.isEmpty()) {
            for (String fallback : List.of(DEFAULT_RINGTONE, "morning.mp3", "alarm.wav")) {
                if (RingtoneCatalog.class.getResource(SOUNDS_DIR + "/" + fallback) != null) {
                    names.add(fallback);
                }
            }
        }
        if (names.isEmpty()) {
            names.add(DEFAULT_RINGTONE);
        }
        return Collections.unmodifiableList(names);
    }

    public static String defaultRingtone() {
        List<String> available = listAvailable();
        if (available.contains(DEFAULT_RINGTONE)) {
            return DEFAULT_RINGTONE;
        }
        return available.get(0);
    }

    public static boolean isAudioFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }
        String lower = fileName.toLowerCase();
        return lower.endsWith(".wav")
                || lower.endsWith(".mp3")
                || lower.endsWith(".m4a")
                || lower.endsWith(".aiff")
                || lower.endsWith(".aac");
    }
}
