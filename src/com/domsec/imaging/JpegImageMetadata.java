package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.awt.color.ICC_Profile;
import java.io.File;

public class JpegImageMetadata {

    private File file;
    private org.apache.commons.imaging.formats.jpeg.JpegImageMetadata metadata;
    private TiffImageMetadata exif;
    private TiffOutputSet outputSet;
    private TiffOutputDirectory exifDirectory;
    private ICC_Profile iccProfile;

    protected JpegImageMetadata(File file) throws JpegAutorotateException {
        this.file = file;
        this.metadata = JpegImageMetadataReader.readMetadata(file);
        readExif();
        this.outputSet = JpegImageMetadataReader.readOutputSet(file, this.exif);
        this.exifDirectory = JpegImageMetadataReader.getOrCreateExifDirectory(file, this.outputSet);
        this.iccProfile = JpegImageMetadataReader.readIccProfile(file);
    }

    /**
     * TODO
     *
     * @throws JpegAutorotateException
     */
    private void readExif() throws JpegAutorotateException {
        if(metadata.getExif() != null) {
            this.exif = metadata.getExif();
        } else {
            throw new JpegAutorotateException("Unable to read image EXIF metadata", this.file);
        }
    }

    public TiffOutputSet getOutputSet() {
        return this.outputSet;
    }

    public TiffOutputDirectory getExifDirectory() {
        return this.exifDirectory;
    }

    public ICC_Profile getIccProfile() {
        return this.iccProfile;
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
