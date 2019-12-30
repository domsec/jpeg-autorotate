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

import com.domsec.JpegAutorotateException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URLConnection;

public final class ImageUtils {

    /**
     * Not intended for instantiation.
     */
    private ImageUtils() {
        throw new IllegalStateException("Not intended for instantiation.");
    }

    /**
     * Attempts to determine if the image file {@code name} is acceptable.
     */
    private static final FilenameFilter FILENAME_FILTER = (File dir, String name) -> {
        for(Extensions ext : Extensions.values()){
            if(name.toUpperCase().contains(ext.name())) {
                return true;
            }
        }

        return false;
    };

    /**
     * Attempts to determine if the {@code file} is an acceptable image.
     *
     * @param file
     *              {@code File} containing image data.
     * @return {@code true} If the {@code file} type is an acceptable image; otherwise, false.
     */
    public static boolean isAcceptableImage(final File file) {
        return FILENAME_FILTER.accept(file, FilenameUtils.getExtension(file.getName()));
    }

    /**
     * Attempts to determine if the {@code bytes} is an acceptable image file.
     *
     * @param bytes
     *              {@code byte[]} containing an image file.
     * @return {@code true} If image is acceptable; otherwise false.
     * @throws JpegAutorotateException
     *              In the event the image file type is unable to be determined.
     */
    public static boolean isAcceptableImage(final byte[] bytes) throws JpegAutorotateException {
        try {
            String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));

            return FILENAME_FILTER.accept(null, contentType);
        } catch (IOException e) {
            throw new JpegAutorotateException("Unable to determine the type of image file.", e);
        }
    }

    /**
     * Writes image data to a {@code byte[]}.
     *
     * @param is
     *          {@code InputStream} containing image data.
     * @return If successful, a {@code byte[]} containing JPEG image data.
     * @throws JpegAutorotateException
     *              In the event the {@code InputStream} is unable to be read or write
     *              to a {@code byte[]}.
     */
    public static byte[] writeImageToBytes(final InputStream is) throws JpegAutorotateException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            IOUtils.copy(is, baos);

            return baos.toByteArray();
        } catch (IOException e) {
            throw new JpegAutorotateException("Unable to read/write InputStream containing JPEG image data to a byte array.", e);
        }
    }

}
