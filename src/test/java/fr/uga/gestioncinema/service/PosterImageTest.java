package fr.uga.gestioncinema.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PosterImageTest {

    @Test
    void detectsJpegPngAndWebp() {
        assertThat(PosterImage.detect(jpeg())).contains(new PosterImage.Detected("image/jpeg", "jpg"));
        assertThat(PosterImage.detect(png())).contains(new PosterImage.Detected("image/png", "png"));
        assertThat(PosterImage.detect(webp())).contains(new PosterImage.Detected("image/webp", "webp"));
    }

    @Test
    void rejectsTextAndTruncatedContent() {
        assertThat(PosterImage.detect("not an image!!".getBytes())).isEmpty();
        assertThat(PosterImage.detect(new byte[] {(byte) 0xFF, (byte) 0xD8})).isEmpty();
    }

    private static byte[] jpeg() {
        byte[] content = new byte[12];
        content[0] = (byte) 0xFF;
        content[1] = (byte) 0xD8;
        content[2] = (byte) 0xFF;
        return content;
    }

    private static byte[] png() {
        byte[] content = new byte[12];
        content[0] = (byte) 0x89;
        content[1] = 0x50;
        content[2] = 0x4E;
        content[3] = 0x47;
        return content;
    }

    private static byte[] webp() {
        byte[] content = new byte[12];
        content[0] = 'R';
        content[1] = 'I';
        content[2] = 'F';
        content[3] = 'F';
        content[8] = 'W';
        content[9] = 'E';
        content[10] = 'B';
        content[11] = 'P';
        return content;
    }
}
