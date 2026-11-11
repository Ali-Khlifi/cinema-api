package fr.uga.gestioncinema.service;

import java.util.Optional;

/**
 * Detects a poster from its bytes. The client-supplied content type is ignored.
 */
public final class PosterImage {

    private PosterImage() {
    }

    public record Detected(String contentType, String extension) {
    }

    public static Optional<Detected> detect(byte[] content) {
        if (content == null || content.length < 12) {
            return Optional.empty();
        }
        if (isJpeg(content)) {
            return Optional.of(new Detected("image/jpeg", "jpg"));
        }
        if (isPng(content)) {
            return Optional.of(new Detected("image/png", "png"));
        }
        if (isWebp(content)) {
            return Optional.of(new Detected("image/webp", "webp"));
        }
        return Optional.empty();
    }

    private static boolean isJpeg(byte[] content) {
        return (content[0] & 0xFF) == 0xFF
                && (content[1] & 0xFF) == 0xD8
                && (content[2] & 0xFF) == 0xFF;
    }

    private static boolean isPng(byte[] content) {
        return (content[0] & 0xFF) == 0x89
                && content[1] == 0x50
                && content[2] == 0x4E
                && content[3] == 0x47;
    }

    private static boolean isWebp(byte[] content) {
        return content[0] == 'R'
                && content[1] == 'I'
                && content[2] == 'F'
                && content[3] == 'F'
                && content[8] == 'W'
                && content[9] == 'E'
                && content[10] == 'B'
                && content[11] == 'P';
    }
}
