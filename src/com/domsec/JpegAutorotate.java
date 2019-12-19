package com.domsec;

import com.domsec.util.FileUtils;
import com.domsec.imaging.JpegImage;
import com.domsec.imaging.JpegImageProcessor;

import java.io.File;

/**
 * Rotates and applies the correct orientation to a JPEG image, based on its
 * EXIF <code>Orientation</code> metadata tag.
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
     * TODO
     *
     * Rotates and applies the correct orientation to a JPEG image, based on its
     * EXIF <code>Orientation</code> metadata tag.
     * <p>
     * JPEG file may potentially not contain the appropriate metadata
     * necessary for full processing. In such an event, an exception will
     * be thrown and the JPEG file will not be processed.
     * </p>
     *
     * @param path
     * @return // TODO
     * @throws JpegAutorotateException
     *            In the event the JPEG file either does not contain the
     *            appropriate EXIF metadata, is not an acceptable image
     *            file type or file is a directory.
     */
    public static JpegImage rotate(final String path) throws JpegAutorotateException {
        return rotate(new File(path));
    }

    /**
     * TODO
     *
     * Rotates and applies the correct orientation to a JPEG image, based on its
     * EXIF <code>Orientation</code> metadata tag.
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
    public static JpegImage rotate(final File file) throws JpegAutorotateException {
        if(!FileUtils.isAcceptableImage(file)) {
            throw new JpegAutorotateException("File is not compatible, must be a JPEG image.");
        }

        if (!file.isFile() && !file.exists()) {
            throw new JpegAutorotateException("Image util does not exist.");
        }

        return JpegImageProcessor.process(file);
    }
}