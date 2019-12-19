package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import com.domsec.util.FileUtils;

import java.awt.color.ICC_ColorSpace;
import java.awt.image.ColorConvertOp;
import java.io.File;

public final class JpegImageProcessor {

    /**
     * Not intended for instantiation.
     */
    private JpegImageProcessor() {
        throw new IllegalStateException("Not intended for instantiation.");
    }

    /**
     * TODO
     *
     * Processes the JPEG file for orientation correction, thumbnail rotation and metadata updating.
     * Processing only occurs if file type is acceptable.
     *
     * @param file
     *            File object providing a reference to a file that must be checked
     *            to be an acceptable file type before processing.
     * @throws JpegAutorotateException
     *            In the event the file is not a JPEG image.
     */
    public static JpegImage processImage(final File file) throws JpegAutorotateException {
        if(FileUtils.isAcceptableImage(file)) {
            JpegImage jpegImage = new JpegImage(file);

            JpegImageTransform.rotateImage(jpegImage);

            // TODO: Update Metadata
            // TODO: Rotate Thumbnail

            processIccProfile(jpegImage);

            return jpegImage;
        } else {
            throw new JpegAutorotateException("File is not compatible. Must be a JPEG image.");
        }
    }

    /**
     * TODO
     *
     * @param jpegImage
     */
    private static void processIccProfile(JpegImage jpegImage) {
        ICC_ColorSpace ics = new ICC_ColorSpace(jpegImage.getMetadata().getIccProfile());
        ColorConvertOp cco = new ColorConvertOp( ics, null );
        jpegImage.setImage(cco.filter( jpegImage.getImage(), null ));
    }

}