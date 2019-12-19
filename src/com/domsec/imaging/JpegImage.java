package com.domsec.imaging;

import com.domsec.JpegAutorotateException;

import java.awt.image.BufferedImage;
import java.io.File;

public class JpegImage {

    private File file;
    private BufferedImage image;
    private JpegImageMetadata metadata;

    public JpegImage(final File file) throws JpegAutorotateException {
        this.image = JpegImageReader.readImage(file);
        this.file = file;
        this.metadata = new JpegImageMetadata(file);
    }

    public File getFile() {
        return this.file;
    }

    protected void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public JpegImageMetadata getMetadata() {
        return this.metadata;
    }

}
