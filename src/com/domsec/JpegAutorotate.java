package com.domsec;

import com.domsec.file.FileProcessor;

import java.io.File;

/**
 * Rotates and applies the correct orientation to a JPEG image, based on its
 * EXIF metadata <code>Orientation</code> tag.
 *
 * // TODO: More information and description
 *
 * More information on the EXIF metadata is available at:
 *
 *     https://www.daveperrett.com/articles/2012/07/28/exif-orientation-handling-is-a-ghetto/
 *
 */
public final class JpegAutorotate {

    /**
     * Rotates and applies the correct orientation to a JPEG image, based on its
     * EXIF metadata <code>Orientation</code> tag.
     * <p>
     * JPEG file may potentially not contain the appropriate metadata
     * necessary for full processing. In such an event, an exception will
     * be thrown and the JPEG fill will not be processed.
     * </p>
     *
     * @param path
     *            Path to the JPEG image file
     * @return // TODO
     * @throws JpegAutorotateException
     *            In the event the JPEG file either does not contain the
     *            appropriate EXIF metadata, is not an acceptable image
     *            file type or file is a directory.
     */
    public void rotate(String path) throws JpegAutorotateException {
        rotate(new File(path));
    }

    /**
     * Rotates and applies the correct orientation to a JPEG image, based on its
     * EXIF metadata <code>Orientation</code> tag.
     * <p>
     * JPEG file may potentially not contain the appropriate metadata
     * necessary for full processing. In such an event, an exception will
     * be thrown and the JPEG fill will not be processed.
     * </p>
     *
     * @param file
     *            File containing the JPEG image
     * @return // TODO
     * @throws JpegAutorotateException
     *            In the event the JPEG file either does not contain the
     *            appropriate EXIF metadata, is not an acceptable image
     *            file type or file is a directory.
     */
    public void rotate(File file) throws JpegAutorotateException {
        if(file.isDirectory()) {
            throw new JpegAutorotateException("Directory is not supported", file);
        }

        if (file.isFile() && file.exists()) {
            FileProcessor.processImage(file);
        } else {
            throw new JpegAutorotateException("File does not exist", file);
        }
    }
}