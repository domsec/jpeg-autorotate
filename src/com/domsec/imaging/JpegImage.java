package com.domsec.imaging;

import com.domsec.JpegAutorotateException;

import java.awt.image.BufferedImage;

class JpegImage {

    private BufferedImage image;
    private JpegImageMetadata metadata;

    protected JpegImage(final byte[] bytes) throws JpegAutorotateException {
        this.image = JpegImageReader.readImage(bytes);
        this.metadata = new JpegImageMetadata(bytes);
    }

    protected void setImage(BufferedImage image) {
        this.image = image;
    }

    protected BufferedImage getImage() {
        return this.image;
    }

    protected JpegImageMetadata getMetadata() {
        return this.metadata;
    }

}
