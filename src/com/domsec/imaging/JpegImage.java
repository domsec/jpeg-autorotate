package com.domsec.imaging;

import com.domsec.JpegAutorotateException;

import java.awt.image.BufferedImage;
import java.io.File;

public class JpegImage {

    private File file;
    private BufferedImage bufferedImage;
    private JpegImageMetadata metadata;

    public JpegImage(final File file) throws JpegAutorotateException {
        this.bufferedImage = JpegImageReader.readImage(file);
        this.file = file;
        this.metadata = new JpegImageMetadata(file);
    }

    public File getFile() {
        return this.file;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getBufferedImage() {
        return this.bufferedImage;
    }

    public int getHeight() {
        return this.bufferedImage.getHeight();
    }

    public int getWidth() {
        return  this.bufferedImage.getWidth();
    }

    public JpegImageMetadata getMetadata() {
        return this.metadata;
    }

}
