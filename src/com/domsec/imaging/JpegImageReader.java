package com.domsec.imaging;

import com.domsec.JpegAutorotateException;

import javax.imageio.ImageIO;
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
     * Attempts to read JPEG file to a BufferedImage.
     *
     * @param bytes
     *              {@code bytes} containing a JPEG image file.
     * @return If successful, a {@code BufferedImage} containing image data.
     * @throws JpegAutorotateException
     *              In the event the {@code bytes} is unable to be read.
     */
    protected static BufferedImage readImage(final byte[] bytes) throws JpegAutorotateException {
        try {
            File tempFile = File.createTempFile("tmp", "jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bytes);

            BufferedImage image = ImageIO.read(tempFile);

            fos.flush();
            fos.close();
            tempFile.deleteOnExit();

            return image;
        } catch (IOException e) {
            throw new JpegAutorotateException("Unable to read JPEG image.", e);
        }
    }

}