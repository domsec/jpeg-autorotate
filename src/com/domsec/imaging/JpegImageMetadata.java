package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.File;
import java.io.IOException;

public class JpegImageMetadata {

    private File file;
    private TiffOutputSet outputSet;
    private org.apache.commons.imaging.formats.jpeg.JpegImageMetadata metadata;
    private TiffImageMetadata exif;

    protected JpegImageMetadata(final File file) throws JpegAutorotateException {
        readMetadata(file);
        this.file = file;
        this.exif = this.metadata.getExif();
        readOutputSet();
    }

    /**
     * Attempts to read the JPEG file metadata.
     *
     * @param file
     *            File object providing a reference to a file who's metadata must
     *            be read from.
     * @throws JpegAutorotateException
     *            In the event the JPEG file metadata is unable to be read.
     */
    private void readMetadata(final File file) throws JpegAutorotateException {
        try {
            org.apache.commons.imaging.common.ImageMetadata imageMetadata = Imaging.getMetadata(file);

            if(imageMetadata == null) {
                return;
            }

            this.metadata = (org.apache.commons.imaging.formats.jpeg.JpegImageMetadata) imageMetadata;
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
     *
     * @throws JpegAutorotateException
     *            In the event the JPEG file TIFF metadata is unable to be read.
     */
    private void readOutputSet() throws JpegAutorotateException{
        try {
            this.outputSet = this.exif.getOutputSet();

            if (this.outputSet == null) {
                this.outputSet = new TiffOutputSet();
            } else {
                this.outputSet = this.exif.getOutputSet();
            }
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to read image TIFF metadata", this.file, e);
        }
    }

    /**
     * Attempts to get the JPEG file <code>Orientation</code> EXIF metadata tag value.
     * <p>
     * The JPEG file may potentially not have an <code>Orientation</code> EXIF metadata
     * tag value.
     * </p>
     *
     * @throws JpegAutorotateException
     *            In the event the JPEG file does not have an <code>Orientation</code> EXIF metadata tag.
     */
    public int getOrientation() throws JpegAutorotateException {
        TiffField field = this.metadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_ORIENTATION);

        try {
            if (field != null) {
                return field.getIntValue();
            }
        } catch (ImageReadException e) {
            throw new JpegAutorotateException("Image does not have an Orientation EXIF tag", this.file, e);
        }

        return 0;
    }

}
