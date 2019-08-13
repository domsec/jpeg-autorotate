package com.domsec.imaging;

import com.domsec.JpegAutorotateException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JpegImage {

    private File file;
    private BufferedImage bufferedImage;
    private JpegImageMetadata metadata;

    public JpegImage(final File file) throws JpegAutorotateException {
        readFile(file);
        this.metadata = new JpegImageMetadata(file);
    }

    /**
     * Attempts to read the JPEG file to a buffer.
     *
     * @param file
     *            File object providing a reference to a file that must be read.
     * @throws JpegAutorotateException
     *            In the event the JPEG file is unable to be read.
     */
    private void readFile(final File file) throws JpegAutorotateException{
        try {
            this.bufferedImage = ImageIO.read(file);
            this.file = file;
        } catch (IOException e) {
            throw new JpegAutorotateException("Unable to read file", file, e);
        }
    }

    public File getFile() {
        return this.file;
    }

    public int getHeight() {
        return this.bufferedImage.getHeight();
    }

    public int getWidth() {
        return  this.bufferedImage.getWidth();
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getBufferedImage() {
        return this.bufferedImage;
    }

    public JpegImageMetadata getMetadata() {
        return this.metadata;
    }

}
