package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import java.awt.image.BufferedImage;
import java.io.*;

final class JpegImageReader {

    /**
     * Not intended for instantiation.
     */
    private JpegImageReader() {
        throw new IllegalStateException("Not intended for instantiation.");
    }

    /**
     * Attempts to read JPEG file to a buffered image.
     *
     * @param file
     *            A valid file object referencing a JPEG image.
     * @return If successful, a valid buffered image.
     * @throws JpegAutorotateException
     */
    protected static BufferedImage readImage(final File file) throws JpegAutorotateException {
        try {
            return Imaging.getBufferedImage(file);
        } catch (ImageReadException | IOException e) {
            throw new JpegAutorotateException("Unable to read JPEG image file.", e);
        }
    }

}