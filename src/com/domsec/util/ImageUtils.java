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
