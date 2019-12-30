/**
 * Copyright (c) 2019-2020 Domenic Seccareccia and contributors
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.domsec;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JpegAutorotateTest {

    private static final String DIRECTORY = "src/test/samples";
    private static final String PNG_IMAGE = "src/test/samples/blue_box.png";
    private static final String IMAGE_DOES_NOT_EXIST = "src/test/samples/does_not_exist.jpg";
    private static final String NO_EXIF = "src/test/samples/exif/no_exif.jpg";
    private static final String UNKNOWN_ORIENTATION = "src/test/samples/orientation/unknown_orientation.jpg";
    private static final String ORIENTATION_1 = "src/test/samples/orientation/orientation_1.jpg";
    private static final String ORIENTATION_1_RESULT = "src/test/samples/orientation/result_orientation_1.jpg";
    private static final String ORIENTATION_2 = "src/test/samples/orientation/orientation_2.jpg";
    private static final String ORIENTATION_2_RESULT = "src/test/samples/orientation/result_orientation_2.jpg";
    private static final String ORIENTATION_3 = "src/test/samples/orientation/orientation_3.jpg";
    private static final String ORIENTATION_3_RESULT = "src/test/samples/orientation/result_orientation_3.jpg";
    private static final String ORIENTATION_4 = "src/test/samples/orientation/orientation_4.jpg";
    private static final String ORIENTATION_4_RESULT = "src/test/samples/orientation/result_orientation_4.jpg";
    private static final String ORIENTATION_5 = "src/test/samples/orientation/orientation_5.jpg";
    private static final String ORIENTATION_5_RESULT = "src/test/samples/orientation/result_orientation_5.jpg";
    private static final String ORIENTATION_6 = "src/test/samples/orientation/orientation_6.jpg";
    private static final String ORIENTATION_6_RESULT = "src/test/samples/orientation/result_orientation_6.jpg";
    private static final String ORIENTATION_7 = "src/test/samples/orientation/orientation_7.jpg";
    private static final String ORIENTATION_7_RESULT = "src/test/samples/orientation/result_orientation_7.jpg";
    private static final String ORIENTATION_8 = "src/test/samples/orientation/orientation_8.jpg";
    private static final String ORIENTATION_8_RESULT = "src/test/samples/orientation/result_orientation_8.jpg";
    private static final String CANON_HDR = "src/test/samples/exif/canon_hdr.jpg";
    private static final String CANON_HDR_RESULT = "src/test/samples/exif/result_canon_hdr.jpg";
    private static final String IPHONE_GPS = "src/test/samples/exif/iphone_gps.jpg";
    private static final String IPHONE_GPS_RESULT = "src/test/samples/exif/result_iphone_gps.jpg";
    private static final String NIKON_XMP = "src/test/samples/exif/nikon_xmp.jpg";
    private static final String NIKON_XMP_RESULT = "src/test/samples/exif/result_nikon_xmp.jpg";

    @Test
    public void testRotateExceptions() {
        assertThrows(JpegAutorotateException.class, () -> JpegAutorotate.rotate(PNG_IMAGE));
        assertThrows(JpegAutorotateException.class, () -> JpegAutorotate.rotate(new FileInputStream(new File(PNG_IMAGE))));

        assertThrows(JpegAutorotateException.class, () -> JpegAutorotate.rotate(DIRECTORY));
        assertThrows(FileNotFoundException.class, () -> JpegAutorotate.rotate(new FileInputStream(new File(DIRECTORY))));

        assertThrows(JpegAutorotateException.class, () -> JpegAutorotate.rotate(IMAGE_DOES_NOT_EXIST));
        assertThrows(FileNotFoundException.class, () -> JpegAutorotate.rotate(new FileInputStream(new File(IMAGE_DOES_NOT_EXIST))));
    }

    @Test
    public void testRotateNoExif() {
        assertThrows(JpegAutorotateException.class, () -> JpegAutorotate.rotate(NO_EXIF));
        assertThrows(JpegAutorotateException.class, () -> JpegAutorotate.rotate(new FileInputStream(new File(NO_EXIF))));
    }

    @Test
    public void testRotateUnknownOrientation() {
        assertThrows(JpegAutorotateException.class, () -> JpegAutorotate.rotate(UNKNOWN_ORIENTATION));
        assertThrows(JpegAutorotateException.class, () -> JpegAutorotate.rotate(new FileInputStream(new File(UNKNOWN_ORIENTATION))));
    }

    @Test
    public void testRotateOrientation() throws Exception {
        testMetadata(ORIENTATION_1);
        testRotateAndFlipImage(ORIENTATION_1, ORIENTATION_1_RESULT);

        testMetadata(ORIENTATION_2);
        testRotateAndFlipImage(ORIENTATION_2, ORIENTATION_2_RESULT);

        testMetadata(ORIENTATION_3);
        testRotateAndFlipImage(ORIENTATION_3, ORIENTATION_3_RESULT);

        testMetadata(ORIENTATION_4);
        testRotateAndFlipImage(ORIENTATION_4, ORIENTATION_4_RESULT);

        testMetadata(ORIENTATION_5);
        testRotateAndFlipImage(ORIENTATION_5, ORIENTATION_5_RESULT);

        testMetadata(ORIENTATION_6);
        testRotateAndFlipImage(ORIENTATION_6, ORIENTATION_6_RESULT);

        testMetadata(ORIENTATION_7);
        testRotateAndFlipImage(ORIENTATION_7, ORIENTATION_7_RESULT);

        testMetadata(ORIENTATION_8);
        testRotateAndFlipImage(ORIENTATION_8, ORIENTATION_8_RESULT);

        testMetadata(CANON_HDR);
        testRotateAndFlipImage(CANON_HDR, CANON_HDR_RESULT);

        testMetadata(IPHONE_GPS);
        testRotateAndFlipImage(IPHONE_GPS, IPHONE_GPS_RESULT);

        testMetadata(NIKON_XMP);
        testRotateAndFlipImage(NIKON_XMP, NIKON_XMP_RESULT);
    }

    private void testRotateAndFlipImage(String originalImagePath, String resultImagePath) throws Exception {
        byte[] rotatedImageBytes = JpegAutorotate.rotate(new FileInputStream(new File(originalImagePath)));
        File tempFile = File.createTempFile("tmp", "jpg");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(rotatedImageBytes);

        tempFile.deleteOnExit();
        fos.flush();
        fos.close();

        BufferedImage originalImage = ImageIO.read(new File(resultImagePath));
        BufferedImage rotatedImage = ImageIO.read(tempFile);

        int width  = originalImage.getWidth();
        int height = originalImage.getHeight();

        assertTrue(() -> {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (originalImage.getRGB(x, y) != rotatedImage.getRGB(x, y)) {
                        return false;
                    }
                }
            }

            return true;
        });
    }

    private void testMetadata(String originalImagePath) throws Exception {
        // Initialize Original Image
        InputStream is = new FileInputStream(new File(originalImagePath));
        byte[] originalImageBytes = IOUtils.toByteArray(is);

        is.close();

        BufferedImage originalImage = ImageIO.read(new File(originalImagePath));
        JpegImageMetadata originalImageMetadata = (org.apache.commons.imaging.formats.jpeg.JpegImageMetadata) Imaging.getMetadata(originalImageBytes);
        TiffOutputSet originalImageOutputSet = originalImageMetadata.getExif().getOutputSet();
        int originalImageOrientation = originalImageMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_ORIENTATION).getIntValue();

        // Initialize rotated image
        byte[] rotatedImageBytes = JpegAutorotate.rotate(originalImagePath);
        File tempFile = File.createTempFile("tmp", "jpg");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(rotatedImageBytes);

        tempFile.deleteOnExit();
        fos.flush();
        fos.close();

        BufferedImage rotatedImage = ImageIO.read(tempFile);
        JpegImageMetadata rotatedImageMetadata = (org.apache.commons.imaging.formats.jpeg.JpegImageMetadata) Imaging.getMetadata(rotatedImageBytes);
        TiffOutputSet rotatedImageOutputSet = rotatedImageMetadata.getExif().getOutputSet();

        switch(originalImageOrientation) {
            case TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL:
            case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL:
            case TiffTagConstants.ORIENTATION_VALUE_ROTATE_180:
            case TiffTagConstants.ORIENTATION_VALUE_MIRROR_VERTICAL:
                assertEquals(originalImage.getHeight(), rotatedImage.getHeight());
                assertEquals(originalImage.getWidth(), rotatedImage.getWidth());
                break;
            case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_270_CW:
            case TiffTagConstants.ORIENTATION_VALUE_ROTATE_90_CW:
            case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_90_CW:
            case TiffTagConstants.ORIENTATION_VALUE_ROTATE_270_CW:
                assertEquals(originalImage.getHeight(), rotatedImage.getWidth());
                assertEquals(originalImage.getWidth(), rotatedImage.getHeight());
                break;
        }

        // Orientation
        assertEquals(TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL, rotatedImageMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_ORIENTATION).getIntValue());

        if(originalImageMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH) != null) {
            assertEquals(rotatedImage.getWidth(), rotatedImageMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH).getIntValue());
        } else {
            assertThrows(NullPointerException.class, () -> rotatedImageMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH).getIntValue());
        }

        if(originalImageMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH) != null) {
            assertEquals(rotatedImage.getHeight(), rotatedImageMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH).getIntValue());
        } else {
            assertThrows(NullPointerException.class, () -> rotatedImageMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH).getIntValue());
        }

        TagInfoShort relatedImageWidth = new TagInfoShort("RelatedImageWidth", 0x1001, TiffDirectoryType.EXIF_DIRECTORY_INTEROP_IFD);
        if(originalImageOutputSet.findField(relatedImageWidth) != null) {
            assertEquals(rotatedImage.getWidth(), rotatedImageMetadata.findEXIFValue(relatedImageWidth).getIntValue());
        }

        TagInfoShort relatedImageHeight = new TagInfoShort("RelatedImageHeight", 0x1002, TiffDirectoryType.EXIF_DIRECTORY_INTEROP_IFD);
        if(originalImageOutputSet.findField(relatedImageHeight) != null) {
            assertEquals(rotatedImage.getHeight(), rotatedImageMetadata.findEXIFValue(relatedImageHeight).getIntValue());
        } else {
            assertThrows(NullPointerException.class, () -> rotatedImageMetadata.findEXIFValue(relatedImageHeight).getIntValue());
        }

        // Thumbnail
        BufferedImage rotatedImageThumbnail = rotatedImageMetadata.getEXIFThumbnail();
        BufferedImage originalImageThumbnail = originalImageMetadata.getEXIFThumbnail();

        if(originalImageMetadata.getEXIFThumbnail() != null) {
            switch(originalImageOrientation) {
                case TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL:
                case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL:
                case TiffTagConstants.ORIENTATION_VALUE_ROTATE_180:
                case TiffTagConstants.ORIENTATION_VALUE_MIRROR_VERTICAL:
                    assertEquals(originalImageThumbnail.getWidth(), rotatedImageThumbnail.getWidth());
                    assertEquals(originalImageThumbnail.getHeight(), rotatedImageThumbnail.getHeight());
                    break;
                case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_270_CW:
                case TiffTagConstants.ORIENTATION_VALUE_ROTATE_90_CW:
                case TiffTagConstants.ORIENTATION_VALUE_MIRROR_HORIZONTAL_AND_ROTATE_90_CW:
                case TiffTagConstants.ORIENTATION_VALUE_ROTATE_270_CW:
                    assertEquals(originalImageThumbnail.getWidth(), rotatedImageThumbnail.getHeight());
                    assertEquals(originalImageThumbnail.getHeight(), rotatedImageThumbnail.getWidth());
                    break;
            }
        } else {
            assertThrows(NullPointerException.class, () -> rotatedImageThumbnail.getHeight());
            assertThrows(NullPointerException.class, () -> rotatedImageThumbnail.getWidth());
        }

        // GPS
        if(originalImageOutputSet.getGPSDirectory() != null) {
            List<TiffOutputField> originalImageGPSDirectories = originalImageOutputSet.getGPSDirectory().getFields();
            List<TiffOutputField> rotatedImageGPSDirectories = rotatedImageOutputSet.getGPSDirectory().getFields();

            for(TiffOutputField originalTof : originalImageGPSDirectories) {
                for(TiffOutputField rotatedTof : rotatedImageGPSDirectories) {
                    if(originalTof.tagInfo.name.equals(rotatedTof.tagInfo.name)) {
                        assertEquals(originalTof.fieldType, rotatedTof.fieldType);
                        break;
                    }
                }
            }
        } else {
            assertNull(rotatedImageOutputSet.getGPSDirectory());
        }

        // ICC Profile
        assertEquals(originalImage.getColorModel(), rotatedImage.getColorModel());

        // XMP
        String xmp = Imaging.getXmpXml(rotatedImageBytes);
        if(xmp != null) {
            if(xmp.contains("tiff:Orientation=")) {
                assertTrue(xmp.contains("tiff:Orientation=\"" + ((Integer) TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL).shortValue() + "\""));
            }

            if(xmp.contains("tiff:ImageLength=")) {
                assertTrue(xmp.contains("tiff:ImageLength=\"" + rotatedImage.getHeight() + "\""));
            }

            if(xmp.contains("tiff:ImageWidth=")) {
                assertTrue(xmp.contains("tiff:ImageWidth=\"" + rotatedImage.getWidth() + "\""));
            }

            if(xmp.contains("exif:PixelXDimension=")) {
                assertTrue(xmp.contains("exif:PixelXDimension=\"" + rotatedImage.getWidth()  + "\""));
            }

            if(xmp.contains("exif:PixelYDimension=")) {
                assertTrue(xmp.contains("exif:PixelYDimension=\"" + rotatedImage.getHeight() + "\""));
            }

            if(xmp.contains("xmp:ThumbnailsHeight=")) {
                assertTrue(xmp.contains("xmp:ThumbnailsHeight=\"" + rotatedImageThumbnail.getHeight() + "\""));
            }

            if(xmp.contains("xmp:ThumbnailsWidth=")) {
                assertTrue(xmp.contains("xmp:ThumbnailsWidth=\"" + rotatedImageThumbnail.getWidth() + "\""));
            }
        } else {
            assertNull(xmp);
        }
    }

}