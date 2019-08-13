package com.domsec.file;

import com.domsec.JpegAutorotateException;

import java.io.File;

public final class FileProcessor {

    private FileProcessor() {
        throw new IllegalStateException("Processing Class");
    }

    /**
     * Processes the correct orientation of the JPEG file through automatic rotation.
     * Updates corresponding file metadata upon successful rotation processing.
     *
     * @param file
     *            File object providing a reference to a file that must be checked
     *            to be an acceptable file type before processing.
     * @throws JpegAutorotateException
     *            In the event the file is not an acceptable image file type.
     */
    public static void processImage(File file) throws JpegAutorotateException {
        if(FileUtil.isAcceptable(file)) {
            // TODO: automatic rotation
        } else {
            throw new JpegAutorotateException("File is not compatible. Must be a JPEG image.", file);
        }
    }

}