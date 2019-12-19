package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data;
import org.apache.commons.imaging.formats.tiff.*;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.*;

public class JpegImageMetadata {

    private org.apache.commons.imaging.formats.jpeg.JpegImageMetadata rawMetadata;
    private TiffOutputSet outputSet;
    private TiffOutputDirectory exifDirectory;
    private ICC_Profile iccProfile;
    private String xmpXml;
    private BufferedImage thumbnail;

    public JpegImageMetadata(File file) throws JpegAutorotateException {
        this.rawMetadata = JpegImageMetadataReader.readRawMetadata(file);
        TiffImageMetadata tempExif = JpegImageMetadataReader.readExif(this.rawMetadata);
        this.outputSet = JpegImageMetadataReader.readOutputSet(tempExif);
        this.exifDirectory = getOrCreateExifDirectory();
        this.iccProfile = JpegImageMetadataReader.readIccProfile(file);
        this.xmpXml = JpegImageMetadataReader.readXmpXml(file);
        this.thumbnail = JpegImageMetadataReader.readThumbnail(this.rawMetadata);
    }

    public TiffOutputSet getOutputSet() {
        return this.outputSet;
    }

    public ICC_Profile getIccProfile() {
        return this.iccProfile;
    }

    public String getXmpXml() {
        return this.xmpXml;
    }

    public PhotoshopApp13Data getPhotoshop() {
        return this.rawMetadata.getPhotoshop().photoshopApp13Data;
    }

    protected void setThumbnail(BufferedImage thumbnail) {
        this.thumbnail = thumbnail;
    }

    public BufferedImage getThumbnail() {
        return thumbnail;
    }

    /**
     * Updates the thumbnail image.
     *
     * @param bytes
     *          A valid byte representation of thumbnail image.
     */
    protected void updateThumbnail(byte[] bytes) {
        TiffOutputDirectory thumbnailDirectory = outputSet.findDirectory(TiffDirectoryConstants.DIRECTORY_TYPE_SUB);
        JpegImageData jpg = new JpegImageData(thumbnailDirectory.getRawJpegImageData().offset, bytes.length, bytes);

        thumbnailDirectory.setJpegImageData(jpg);
    }

    /**
     * Attempts to get the EXIF <code>Orientation</code> metadata tag value.
     *
     * @throws JpegAutorotateException
     *            In the event the EXIF <code>Orientation</code> metadata
     *            tag is not found.
     */
    protected int getOrientation() throws JpegAutorotateException {
        TiffField field = this.rawMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_ORIENTATION);

        if (field == null) {
            throw new JpegAutorotateException("JPEG image does not have an EXIF Orientation metadata tag.");
        }

        try {
            return field.getIntValue();
        } catch (ImageReadException e) {
            throw new JpegAutorotateException("Unable to read JPEG EXIF Orientation metadata tag.", e);
        }
    }

    /**
     * Attempts to read or create EXIF metadata directory.
     *
     * @return A valid TiffOutputDirectory object.
     * @throws JpegAutorotateException
     *              In the event the EXIF metadata directory does not exist or
     *              is unable to be created.
     */
    private TiffOutputDirectory getOrCreateExifDirectory() throws JpegAutorotateException {
        try {
            return this.outputSet.getOrCreateExifDirectory();
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to get or create JPEG EXIF metadata directory.", e);
        }
    }

    /**
     * Attempts to add or update EXIF metadata information:
     *  - <code>Orientation</code>
     *  - <code>Width</code>
     *  - <code>Height</code>
     *
     * @throws JpegAutorotateException
     *              In the event the EXIF metadata is unable to be added or updated.
     */
    protected void updateExif() throws JpegAutorotateException {
        addOrUpdateExifOrientation();
        addOrUpdateExifWidth();
        addOrUpdateExifHeight();
    }

    /**
     * Attempts to add or update EXIF <code>Orientation</code> metadata tags.
     *
     * @throws JpegAutorotateException
     *              In the event the EXIF <code>Orientation</code> metadata tags
     *              is unable to be added or updated.
     */
    private void addOrUpdateExifOrientation() throws JpegAutorotateException {
        if(this.outputSet.findField(TiffTagConstants.TIFF_TAG_ORIENTATION) != null) {
            this.outputSet.removeField(TiffTagConstants.TIFF_TAG_ORIENTATION);
        }

        try {
            this.outputSet.getRootDirectory().add(TiffTagConstants.TIFF_TAG_ORIENTATION, ((Integer) TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL).shortValue());
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to update JPEG EXIF Orientation metadata tag to Horizontal (normal).", e);
        }
    }

    /**
     * Attempts to add or update EXIF <code>Image Width</code> metadata tags.
     *
     * @throws JpegAutorotateException
     *              In the event the EXIF <code>Image Width</code> metadata tags
     *              are unable to be added or updated.
     */
    private void addOrUpdateExifWidth() throws JpegAutorotateException {
        if (this.outputSet.findField(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH) != null) {
            this.outputSet.removeField(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH);
            this.outputSet.removeField(new TagInfoShort("RelatedImageWidth", 0x1001, TiffDirectoryType.EXIF_DIRECTORY_INTEROP_IFD));
        }

        try {
            int width = this.rawMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH).getIntValue();

            this.exifDirectory.add(new TagInfoShort("RelatedImageWidth", 0x1001,TiffDirectoryType.EXIF_DIRECTORY_INTEROP_IFD), ((Integer) width).shortValue());
            this.exifDirectory.add(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH, ((Integer) width).shortValue());
        } catch (ImageReadException | ImageWriteException e) {
            throw new JpegAutorotateException("Unable to update JPEG EXIF Image Width metadata tags.", e);
        }
    }

    /**
     * Attempts to add or update EXIF <code>Image Height</code> metadata tags.
     *
     * @throws JpegAutorotateException
     *              In the event the EXIF <code>Image Height</code> metadata tags
     *              are unable to be added or updated.
     */
    private void addOrUpdateExifHeight() throws JpegAutorotateException {
        if(this.outputSet.findField(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH) != null) {
            this.outputSet.removeField(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH);
            this.outputSet.removeField(new TagInfoShort("RelatedImageHeight", 0x1002, TiffDirectoryType.EXIF_DIRECTORY_INTEROP_IFD));
        }

        try {
            int height = this.rawMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH).getIntValue();

            this.exifDirectory.add(new TagInfoShort("RelatedImageHeight", 0x1002,TiffDirectoryType.EXIF_DIRECTORY_INTEROP_IFD), ((Integer) height).shortValue());
            this.exifDirectory.add(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH, ((Integer) height).shortValue());
        } catch (ImageReadException | ImageWriteException e) {
            throw new JpegAutorotateException("Unable to update JPEG EXIF Image Height metadata tags.", e);
        }
    }

}
