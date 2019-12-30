/**
 * MIT License
 *
 * Copyright (c) 2019 Domenic Seccareccia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * More information about this project is available at:
 *     https://github.com/domsec/jpeg-autorotate
 */

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
