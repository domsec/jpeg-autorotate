package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;

import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.IOException;

class JpegImageMetadataReader {

    private JpegImageMetadataReader() {
        throw new IllegalStateException("Not intended for instantiation");
    }

    /**
     * Attempts to read the JPEG file metadata.
     *
     * @throws JpegAutorotateException
     *            In the event the JPEG file metadata is unable to be read.
     */
    protected static org.apache.commons.imaging.formats.jpeg.JpegImageMetadata readMetadata(final File file) throws JpegAutorotateException {
        try {
            org.apache.commons.imaging.common.ImageMetadata imageMetadata = Imaging.getMetadata(file);

            if(imageMetadata == null) {
                throw new JpegAutorotateException("Image does not have metadata", file);
            }

            return (org.apache.commons.imaging.formats.jpeg.JpegImageMetadata) imageMetadata;
        } catch(IOException | ImageReadException e) {
            throw new JpegAutorotateException("Unable to read image metadata", file, e);
        }

    }

    /**
     * Attempts to read the JPEG file TIFF output set.
     * <p>
     * The JPEG file may potentially not have a TIFF output set and in
     * such an event, it will be created.
     * </p>
     * @param exif TODO
     * @throws JpegAutorotateException
     *            In the event the JPEG file TIFF metadata is unable to be read.
     */
    protected static TiffOutputSet readOutputSet(final File file, TiffImageMetadata exif) throws JpegAutorotateException{
        try {
            if (exif.getOutputSet() == null) {
                return new TiffOutputSet();
            } else {
                return exif.getOutputSet();
            }
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to read image TIFF metadata", file, e);
        }
    }

    /**
     * TODO
     *
     * @param file
     * @return
     * @throws JpegAutorotateException
     */
    protected static ICC_Profile readIccProfile(final File file) throws JpegAutorotateException {
        try {
            return Imaging.getICCProfile(file);
        } catch (ImageReadException | IOException e) {
            throw new JpegAutorotateException("Unable to read image ICC Profile", file, e);
        }
    }

    /**
     * TODO
     *
     * @throws JpegAutorotateException
     */
    protected static TiffOutputDirectory getOrCreateExifDirectory(final File file, TiffOutputSet outputSet) throws JpegAutorotateException {
        try {
            return outputSet.getOrCreateExifDirectory();
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to get or create an EXIF metadata directory", file, e);
        }
    }

}
