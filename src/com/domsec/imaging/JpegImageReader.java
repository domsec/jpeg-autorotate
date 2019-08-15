package com.domsec.imaging;

import com.domsec.JpegAutorotateException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class JpegImageReader {

    private JpegImageReader() {
        throw new IllegalStateException("Not intended for instantiation");
    }

    /**
     * Attempts to read the JPEG file to a buffer.
     *
     * @param file
     *            File object providing a reference to a file that must be read.
     * @throws JpegAutorotateException
     *            In the event the JPEG file is unable to be read.
     */
    protected static BufferedImage readImage(final File file) throws JpegAutorotateException {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new JpegAutorotateException("Unable to read file", file, e);
        }
    }

}
