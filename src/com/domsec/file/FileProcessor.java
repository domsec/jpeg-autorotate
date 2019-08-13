package com.domsec.file;

import com.domsec.JpegAutorotateException;
import com.domsec.imaging.JpegImage;
import com.domsec.imaging.JpegImageTransform;

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
    public static void processImage(final File file) throws JpegAutorotateException {
        if(FileUtil.isAcceptable(file)) {
            JpegImage jpegImage = new JpegImage(file);

            JpegImageTransform.rotateImage(jpegImage);

            // TODO: Rotate Thumbnail
            // TODO: Update Metadata
        } else {
            throw new JpegAutorotateException("File is not compatible. Must be a JPEG image.", file);
        }
    }

}