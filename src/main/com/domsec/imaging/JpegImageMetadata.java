package com.domsec.imaging;

import com.domsec.JpegAutorotateException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.jpeg.JpegPhotoshopMetadata;
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

class JpegImageMetadata {

    private org.apache.commons.imaging.formats.jpeg.JpegImageMetadata rawMetadata;
    private TiffOutputSet outputSet;
    private TiffOutputDirectory exifDirectory;
    private ICC_Profile iccProfile;
    private String xmpXml;
    private BufferedImage thumbnail;
    private int originalOrientation;

    protected JpegImageMetadata(final byte[] bytes) throws JpegAutorotateException {
        this.rawMetadata = JpegImageMetadataReader.readRawMetadata(bytes);
        TiffImageMetadata tempExif = JpegImageMetadataReader.readExif(this.rawMetadata);
        this.outputSet = JpegImageMetadataReader.readOutputSet(tempExif);
        this.exifDirectory = getOrCreateExifDirectory();
        this.iccProfile = JpegImageMetadataReader.readIccProfile(bytes);
        this.xmpXml = JpegImageMetadataReader.readXmpXml(bytes);
        this.thumbnail = JpegImageMetadataReader.readThumbnail(this.rawMetadata);
        this.originalOrientation = getOrientation();
    }

    protected TiffOutputSet getOutputSet() {
        return this.outputSet;
    }

    protected ICC_Profile getIccProfile() {
        return this.iccProfile;
    }

    protected String getXmpXml() {
        return this.xmpXml;
    }

    protected JpegPhotoshopMetadata getPhotoshop() {
        return this.rawMetadata.getPhotoshop();
    }

    protected void setThumbnail(BufferedImage thumbnail) {
        this.thumbnail = thumbnail;
    }

    protected BufferedImage getThumbnail() {
        return thumbnail;
    }

    /**
     * Updates the thumbnail image.
     *
     * @param bytes
     *              A {@code byte[]} containing thumbnail image data.
     */
    protected void updateThumbnail(final byte[] bytes) {
        TiffOutputDirectory thumbnailDirectory = outputSet.findDirectory(TiffDirectoryConstants.DIRECTORY_TYPE_SUB);
        JpegImageData jpg = new JpegImageData(thumbnailDirectory.getRawJpegImageData().offset, bytes.length, bytes);

        thumbnailDirectory.setJpegImageData(jpg);
    }

    /**
     * Attempts to get the EXIF {@code Orientation} metadata tag value.
     *
     * @return If successful, EXIF {@code Orientation} metadata tag value.
     * @throws JpegAutorotateException
     *              In the event the EXIF {@code Orientation} metadata tag is not found.
     */
    protected int getOrientation() throws JpegAutorotateException {
        TiffField field = this.rawMetadata.findEXIFValue(TiffTagConstants.TIFF_TAG_ORIENTATION);

        if(field == null) {
            throw new JpegAutorotateException("JPEG image does not have an EXIF Orientation metadata tag.");
        }

        try {
            return field.getIntValue();
        } catch (ImageReadException e) {
            throw new JpegAutorotateException("Unable to read JPEG image EXIF Orientation metadata tag.", e);
        }
    }

    /**
     * Attempts to read or create {@code ExifDirectory}.
     *
     * @return If successful, a valid {@code TiffOutputDirectory} instance.
     * @throws JpegAutorotateException
     *              In the event the {@code ExifDirectory} does not exist or
     *              is unable to be created.
     */
    private TiffOutputDirectory getOrCreateExifDirectory() throws JpegAutorotateException {
        try {
            return this.outputSet.getOrCreateExifDirectory();
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to get or create JPEG image EXIF metadata directory.", e);
        }
    }

    /**
     * Attempts to update {@code metadata} information:
     * <p><ul>
     * <li>EXIF</li>
     * <li>XMP</li>
     * </ul></p>
     *
     * @throws JpegAutorotateException
     *              In the event the EXIF {@code metadata} is unable to be updated.
     */
    protected void updateMetadata() throws JpegAutorotateException {
        updateExifOrientation();
        updateAllMetadataDimensions();
    }

    /**
     * Attempts to update all {@code metadata} information containing:
     * <p><ul>
     * <li>Height</li>
     * <li>Width</li>
     * </ul></p>
     *
     * @throws JpegAutorotateException
     *              In the event the EXIF {@code Height} and/or
     *              {@code Width} metadata is unable to be updated.
     */
    private void updateAllMetadataDimensions() throws JpegAutorotateException {
        if(this.outputSet.findField(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH) != null || this.outputSet.findField(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH) != null) {
            try {
                int height = this.rawMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH).getIntValue();
                int width = this.rawMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH).getIntValue();

                switch(this.originalOrientation) {
                    case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_270_CW:
                    case TiffTagConstants.ORIENTATION_VALUE_ROTATE_90_CW:
                    case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_90_CW:
                    case TiffTagConstants.ORIENTATION_VALUE_ROTATE_270_CW:
                        height = this.rawMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH).getIntValue();
                        width = this.rawMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH).getIntValue();
                        break;
                }

                updateExifHeight(height);
                updateExifWidth(width);
                updateXmpXml(height, width);
            } catch (Exception e) {
                throw new JpegAutorotateException("Unable to read JPEG image EXIF Image Width and/or Image Height metadata tags.", e);
            }
        }
    }

    /**
     * Attempts to update EXIF {@code Orientation} metadata tag.
     *
     * @throws JpegAutorotateException
     *              In the event the EXIF {@code Orientation} metadata tag
     *              is unable to be updated.
     */
    private void updateExifOrientation() throws JpegAutorotateException {
        try {
            this.outputSet.removeField(TiffTagConstants.TIFF_TAG_ORIENTATION);

            this.outputSet.getRootDirectory().add(TiffTagConstants.TIFF_TAG_ORIENTATION, ((Integer) TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL).shortValue());
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to update JPEG image EXIF Orientation metadata tag to Horizontal (normal).", e);
        }
    }

    /**
     * Attempts to update EXIF {@code Width} metadata tags.
     *
     * @param width
     * @throws JpegAutorotateException
     *              In the event the EXIF {@code Width} metadata tags
     *              are unable to be updated.
     */
    private void updateExifWidth(int width) throws JpegAutorotateException {
        try {
            // EXIF Width
            this.outputSet.removeField(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH);

            this.exifDirectory.add(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH, ((Integer) width).shortValue());

            // TIFF RelatedImageWidth
            TagInfoShort relatedImageWidth = new TagInfoShort("RelatedImageWidth", 0x1001, TiffDirectoryType.EXIF_DIRECTORY_INTEROP_IFD);

            if(this.outputSet.findField(relatedImageWidth) != null) {
                this.outputSet.removeField(relatedImageWidth);

                this.exifDirectory.add(relatedImageWidth, ((Integer) width).shortValue());
            }
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to update JPEG image EXIF Image Width metadata tags.", e);
        }
    }

    /**
     * Attempts to update EXIF {@code Height} metadata tags.
     *
     * @param height
     * @throws JpegAutorotateException
     *              In the event the EXIF {@code Height} metadata tags
     *              are unable to be updated.
     */
    private void updateExifHeight(int height) throws JpegAutorotateException {
        try {
            // EXIF Height
            this.outputSet.removeField(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH);

            this.exifDirectory.add(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH, ((Integer) height).shortValue());

            // TIFF RelatedImageHeight
            TagInfoShort relatedImageHeight = new TagInfoShort("RelatedImageHeight", 0x1002, TiffDirectoryType.EXIF_DIRECTORY_INTEROP_IFD);

            if(this.outputSet.findField(relatedImageHeight) != null) {
                this.outputSet.removeField(relatedImageHeight);

                this.exifDirectory.add(relatedImageHeight, ((Integer) height).shortValue());
            }
        } catch (ImageWriteException e) {
            throw new JpegAutorotateException("Unable to update JPEG image EXIF Image Height metadata tags.", e);
        }
    }

    /**
     * Attempts to update {@code xmpXml} metadata values.
     *
     * @param height
     * @param width
     */
    private void updateXmpXml(int height, int width) {
        String xmp = getXmpXml();

        if(xmp == null) {
            return;
        }

        this.xmpXml = xmp.replaceAll("tiff:Orientation=\"\\d\"", "tiff:Orientation=\"" + ((Integer) TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL).shortValue() + "\"")
                            .replaceAll("tiff:ImageLength=\"\\d+\"", "tiff:ImageLength=\"" + height + "\"")
                            .replaceAll("tiff:ImageWidth=\"\\d+\"", "tiff:ImageWidth=\"" + width + "\"")
                            .replaceAll("exif:PixelXDimension=\"\\d+\"", "exif:PixelXDimension=\"" + width + "\"")
                            .replaceAll("exif:PixelYDimension=\"\\d+\"", "exif:PixelYDimension=\"" + height + "\"")
                            .replaceAll("xmp:ThumbnailsHeight=\"\\d+\"", "xmp:ThumbnailsHeight=\"" + getThumbnail().getHeight() + "\"")
                            .replaceAll("xmp:ThumbnailsWidth=\"\\d+\"", "xmp:ThumbnailsWidth=\"" + getThumbnail().getWidth() + "\"");
    }

}
