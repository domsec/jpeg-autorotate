package com.domsec;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JpegAutorotateTest {

    private static final String DIRECTORY = Paths.get("src", "test", "samples").toString();
    private static final String PNG_IMAGE = Paths.get("src", "test", "samples", "image_1.png").toString();
    private static final String JPEG_IMAGE = Paths.get("src", "test", "samples", "image_1.jpg").toString();
    private static final String IMAGE_DOES_NOT_EXIST = Paths.get("src", "test", "samples", "does_not_exist.jpg").toString();

    @Test
    public void testExceptions() {
        assertThrows(JpegAutorotateException.class, () -> {
            JpegAutorotate.rotate(JPEG_IMAGE);}
        );

        assertThrows(JpegAutorotateException.class, () -> {
           JpegAutorotate.rotate(PNG_IMAGE);
        });

        assertThrows(JpegAutorotateException.class, () -> {
            JpegAutorotate.rotate(DIRECTORY);
        });

        assertThrows(JpegAutorotateException.class, () -> {
            JpegAutorotate.rotate(IMAGE_DOES_NOT_EXIST);
        });
    }

}
