package com.domsec;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JpegAutorotateTest {

    private static final String SAMPLES_DIRECTORY = Paths.get("src", "test", "samples").toString();
    private static final String PNG_IMAGE = Paths.get("src", "test", "samples", "image_1.png").toString();
    private static final String JPEG_IMAGE = Paths.get("src", "test", "samples", "image_1.jpg").toString();
    private static final String IMAGE_DOES_NOT_EXIST = Paths.get("src", "test", "samples", "does_not_exist.jpg").toString();

    @Test
    public void testFileExistenceAndAcceptability() {
        assertDoesNotThrow(() -> {
            new JpegAutorotate().rotate(JPEG_IMAGE);}
        );

        assertThrows(JpegAutorotateException.class, () -> {
           new JpegAutorotate().rotate(PNG_IMAGE);
        });

        assertThrows(JpegAutorotateException.class, () -> {
            new JpegAutorotate().rotate(SAMPLES_DIRECTORY);
        });

        assertThrows(JpegAutorotateException.class, () -> {
            new JpegAutorotate().rotate(IMAGE_DOES_NOT_EXIST);
        });
    }

}
