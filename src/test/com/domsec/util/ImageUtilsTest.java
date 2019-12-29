package com.domsec.util;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ImageUtilsTest {

    private static final String JPEG_IMAGE = "src/test/samples/red_box.jpg";
    private static final String PNG_IMAGE = "src/test/samples/blue_box.png";

    @Test
    public void testAcceptableImage() throws Exception {
        // JPEG Image
        assertTrue(ImageUtils.isAcceptableImage(new File(JPEG_IMAGE)));

        try (InputStream is = new FileInputStream(new File(JPEG_IMAGE))) {
            byte[] bytes = ImageUtils.writeImageToBytes(is);

            assertTrue(ImageUtils.isAcceptableImage(bytes));
        }

        // PNG Image
        assertFalse(ImageUtils.isAcceptableImage(new File(PNG_IMAGE)));

        try (InputStream is = new FileInputStream(new File(PNG_IMAGE))) {
            byte[] bytes = ImageUtils.writeImageToBytes(is);

            assertFalse(ImageUtils.isAcceptableImage(bytes));
        }
    }

    @Test
    public void testWriteImageToBytes() throws Exception {
        // JPEG Image
        InputStream is = new FileInputStream(new File(JPEG_IMAGE));
        byte[] expectedBytes = IOUtils.toByteArray(is);

        assertArrayEquals(expectedBytes, ImageUtils.writeImageToBytes(new FileInputStream(new File(JPEG_IMAGE))));
    }

}
