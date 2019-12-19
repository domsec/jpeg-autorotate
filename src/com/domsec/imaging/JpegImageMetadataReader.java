package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;

import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

final class JpegImageMetadataReader {

    private JpegImageMetadataReader() {
        throw new IllegalStateException("Not intended for instantiation");
    }

    /**
     * Attempts to read the JPEG util metadata.
     *
     * @param file A valid reference to a JPEG util.
     * @return A valid JpegImageMetadata object.
     * @throws JpegAutorotateException  In the event the JPEG util metadata either
     *                                  does not exist or is unable to be read.
     */
    protected static org.apache.commons.imaging.formats.jpeg.JpegImageMetadata readRawMetadata(final File file) throws JpegAutorotateException {
        try {

            org.apache.commons.imaging.common.ImageMetadata imageMetadata = Imaging.getMetadata(file);

            if(imageMetadata == null) {
                throw new JpegAutorotateException("JPEG image does not have metadata.");
            }

            return (org.apache.commons.imaging.formats.jpeg.JpegImageMetadata) imageMetadata;
        } catch(IOException | ImageReadException e) {
            throw new JpegAutorotateException("Unable to read JPEG image metadata.", e);
        }
    }

    /**
     * Attempts to read the JPEG util EXIF metadata.
     *
     * @param metadata A valid instance of JpegImageMetadata.
     * @return A valid TiffImageMetadata object.
     * @throws JpegAutorotateException In the event the JPEG util EXIF metadata is unable to be read.
     */
    protected static TiffImageMetadata readExif(org.apache.commons.imaging.formats.jpeg.JpegImageMetadata metadata) throws JpegAutorotateException {
        if(metadata.getExif() == null) {
            throw new JpegAutorotateException("Unable to read JPEG image EXIF metadata.");
        }

        return metadata.getExif();
    }

    /**
     * Attempts to read the JPEG util TIFF output set from the TIFF metadata.
     * <p>
     * The JPEG util may potentially not have a TIFF output set and in
     * such an event, it will be created.
     * </p>
     *
     * @param exif A valid instance of TiffImageMetadata.
     * @return A valid TiffOutputSet object.
     * @throws JpegAutorotateException In the event the JPEG util TIFF metadata is unable to be read.
     */
    protected static TiffOutputSet readOutputSet(TiffImageMetadata exif) throws JpegAutorotateException{
        try {
            if (exif.getOutputSet() == null) {
                return new TiffOutputSet();
            }

            return exif.getOutputSet();
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to read JPEG image TIFF metadata.", e);
        }
    }

    /**
     * Attempts to read the JPEG util ICC Profile.
     *
     * @param file A valid reference to a JPEG util.
     * @return A valid ICC_Profile object.
     * @throws JpegAutorotateException In the event the JPEG util ICC Profile metadata is unable to be read.
     */
    protected static ICC_Profile readIccProfile(final File file) throws JpegAutorotateException {
        try {
            return Imaging.getICCProfile(file);
        } catch (ImageReadException | IOException e) {
            throw new JpegAutorotateException("Unable to read JPEG image ICC Profile metadata.", e);
        }
    }

    /**
     * Attempts to read the JPEG util XMP metadata.
     *
     * @param file A valid reference to a JPEG util.
     * @return Xmp as String, if present. Otherwise, returns null.
     * @throws JpegAutorotateException In the event the JPEG util XMP metadata is unable to be read.
     */
    protected static String readXmpXml(final File file) throws JpegAutorotateException {
      try {
          return Imaging.getXmpXml(file);
      } catch(IOException | ImageReadException e) {
          throw new JpegAutorotateException("Unable to read JPEG image XMP metadata.", e);
      }
    }

    /**
     * Attempts to read the JPEG util thumbnail.
     *
     * @param metadata A valid instance of JpegImageMetadata.
     * @return Thumbnail as BufferedImage, if present. Otherwise, returns null.
     * @throws JpegAutorotateException In the event the JPEG util thumbnail is unable to be read.
     */
    protected static BufferedImage readThumbnail(org.apache.commons.imaging.formats.jpeg.JpegImageMetadata metadata) throws JpegAutorotateException {
        try {
            return metadata.getEXIFThumbnail();
        } catch (ImageReadException | IOException e) {
            throw new JpegAutorotateException("Unable to read JPEG image thumbnail.", e);
        }
    }

}
