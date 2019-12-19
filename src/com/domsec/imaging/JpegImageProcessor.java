package com.domsec.imaging;

import com.domsec.JpegAutorotateException;

import javax.imageio.ImageIO;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public final class JpegImageProcessor {

    /**
     * Not intended for instantiation.
     */
    private JpegImageProcessor() {
        throw new IllegalStateException("Not intended for instantiation.");
    }

    /**
     * TODO
     * Processes the JPEG util for orientation correction, thumbnail rotation and metadata updating.
     * Processing only occurs if util type is acceptable.
     *
     * @param file
     *            File object providing a reference to a util that must be checked
     *            to be an acceptable util type before processing.
     * @return
     * @throws JpegAutorotateException
     *            In the event the file is not a JPEG image.
     */
    public static JpegImage process(final File file) throws JpegAutorotateException {
        JpegImage jpegImage = new JpegImage(file);

        processImage(jpegImage);
        processThumbnail(jpegImage.getMetadata());

        return jpegImage;
    }

    /**
     * TODO
     *
     * @param jpegImage
     * @throws JpegAutorotateException
     */
    private static void processImage(JpegImage jpegImage) throws JpegAutorotateException {
        JpegImageTransform.rotateImage(jpegImage);

        ICC_Profile iccProfile = jpegImage.getMetadata().getIccProfile();
        if(iccProfile != null) {
            BufferedImage processedImage = processIccProfile(iccProfile, jpegImage.getImage());
            jpegImage.setImage(processedImage);
        }

        jpegImage.getMetadata().updateExif();
    }

    /**
     * TODO
     *
     * @param metadata
     * @throws JpegAutorotateException
     */
    private static void processThumbnail(JpegImageMetadata metadata) throws JpegAutorotateException {
        if(metadata.getThumbnail() == null) {
            return;
        }

        JpegImageTransform.rotateThumbnail(metadata);

        ICC_Profile iccProfile = metadata.getIccProfile();
        if(iccProfile != null) {
            BufferedImage processedImage = processIccProfile(iccProfile, metadata.getThumbnail());
            metadata.setThumbnail(processedImage);
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(metadata.getThumbnail(), "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            metadata.updateThumbnail(imageInByte);
        } catch(IOException e) {
            throw new JpegAutorotateException("Unable to update JPEG image thumbnail.", e);
        }
    }

    /**
     * TODO
     *
     * @param iccProfile
     * @param image
     * @return
     */
    private static BufferedImage processIccProfile(ICC_Profile iccProfile, BufferedImage image) {
        ICC_ColorSpace ics = new ICC_ColorSpace(iccProfile);
        ColorConvertOp cco = new ColorConvertOp(ics, null);
        return cco.filter(image, null);
    }

}