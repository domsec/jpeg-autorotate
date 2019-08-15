package com.domsec.file;

import com.domsec.JpegAutorotateException;
import com.domsec.imaging.JpegImage;
import com.domsec.imaging.JpegImageTransform;

import java.awt.color.ICC_ColorSpace;
import java.awt.image.ColorConvertOp;
import java.io.File;

public final class FileProcessor {

    private FileProcessor() {
        throw new IllegalStateException("Not intended for instantiation");
    }

    /**
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
        if(FileUtil.isAcceptable(file)) {
            JpegImage jpegImage = new JpegImage(file);

            JpegImageTransform.rotateImage(jpegImage);

            // TODO: Update Metadata
            // TODO: Rotate Thumbnail

            processIccProfile(jpegImage);

            return jpegImage;
        } else {
            throw new JpegAutorotateException("File is not compatible. Must be a JPEG image.", file);
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
        jpegImage.setBufferedImage(cco.filter( jpegImage.getBufferedImage(), null ));
    }

}