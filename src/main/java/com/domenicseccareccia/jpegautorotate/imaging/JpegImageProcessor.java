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

package com.domenicseccareccia.jpegautorotate.imaging;

import com.domenicseccareccia.jpegautorotate.JpegAutorotateException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.jpeg.iptc.JpegIptcRewriter;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;

import javax.imageio.ImageIO;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;

public final class JpegImageProcessor {

    /**
     * Not intended for instantiation.
     */
    private JpegImageProcessor() {
        throw new IllegalStateException("Not intended for instantiation.");
    }

    /**
     * Processes {@code JpegImage} rotation and metadata modification. Processing
     * depends on presence of EXIF {@code Orientation} metadata tag.
     * <p>
     * {@code JpegImage} may contain {@code EXIF} and/or {@code IPTC/Photoshop} metadata, {@code thumbnail} image
     * and {@code ICCProfile}.
     * </p>
     *
     * @param bytes
     *              {@code bytes} containing a JPEG image file.
     * @return If processed, a {@code byte[]} containing processed image;
     *         otherwise, a {@code byte[]} containing the original image file.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} does not have necessary
     *              metadata and/or has unprocessable information.
     */
    public static byte[] process(final byte[] bytes) throws JpegAutorotateException {
        JpegImage jpegImage = new JpegImage(bytes);

        // Determine if JPEG image is already properly oriented.
        if(jpegImage.getMetadata().getOrientation() == TiffTagConstants.ORIENTATION_VALUE_HORIZONTAL_NORMAL) {
            return bytes;
        }

        processImage(jpegImage);
        processThumbnail(jpegImage.getMetadata());

        jpegImage.getMetadata().updateMetadata();

        return writeImage(jpegImage);
    }

    /**
     * Processes {@code JpegImage} rotation. Rotation depends on the EXIF {@code Orientation}
     * metadata tag. Sets original {@code ICCProfile} to rotated image. Updates corresponding
     * metadata information.
     * <p>
     * JPEG image may contain {@code ICCProfile}.
     * </p>
     *
     * @param jpegImage
     *              An instance of {@code JpegImage}.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} is unable to be rotated and/or metadata
     *              is unable to be updated.
     */
    private static void processImage(JpegImage jpegImage) throws JpegAutorotateException {
        JpegImageTransform.rotateImage(jpegImage);

        ICC_Profile iccProfile = jpegImage.getMetadata().getIccProfile();
        if(iccProfile != null) {
            BufferedImage processedImage = processIccProfile(iccProfile, jpegImage.getImage());
            jpegImage.setImage(processedImage);
        }
    }

    /**
     * Processes {@code JpegImage} {@code ExifThumbnail} rotation. Processing depends on presence
     * of a {@code ExifThumbnail} image. Rotation depends on the EXIF {@code Orientation}
     * metadata tag of {@code JpegImage}. Sets original {@code ICCProfile} to rotated image.
     * <p>
     * {@code JpegImage} {@code ExifThumbnail} image may contain {@code ICCProfile}.
     * </p>
     *
     * @param metadata
     *              An instance of {@code JpegImageMetadata}.
     * @throws JpegAutorotateException
     *              In the event the {@code ExifThumbnail} image is unable to be rotated.
     */
    private static void processThumbnail(JpegImageMetadata metadata) throws JpegAutorotateException {
        if(metadata.getThumbnail() == null) {
            return;
        }

        JpegImageTransform.rotateThumbnail(metadata);

        ICC_Profile iccProfile = metadata.getIccProfile();
        if(iccProfile != null) {
            BufferedImage processedImage = processIccProfile(iccProfile, metadata.getThumbnail());
            metadata.setThumbnail(processedImage);
        }

        byte[] bytes = writeThumbnail(metadata);
        metadata.updateThumbnail(bytes);
    }

    /**
     * Adds an {@code ICCProfile} color space and filter to a {@code BufferedImage}.
     *
     * @param iccProfile
     *              An instance of {@code ICC_Profile}.
     * @param image
     *              A {@code BufferedImage} containing image data.
     * @return If successful, a valid {@code BufferedImage}.
     */
    private static BufferedImage processIccProfile(final ICC_Profile iccProfile, final BufferedImage image) {
        ICC_ColorSpace ics = new ICC_ColorSpace(iccProfile);
        ColorConvertOp cco = new ColorConvertOp(ics, null);
        return cco.filter(image, null);
    }

    /**
     * Writes {@code ExifThumbnail} image content from {@code JpegImageMetadata} to a {@code byte[]}.
     * Content written to {@code byte[]} consists of rotated {@code ExifThumbnail} {@code BufferedImage}.
     *
     * @param metadata
     *              An instance of {@code JpegImageMetadata}.
     * @return If successful, a {@code byte[]} representing the {@code ExifThumbnail} image.
     * @throws JpegAutorotateException
     *              In the event, the {@code ExifThumbnail} image is unable to be written
     *              to a {@code byte[]}.
     */
    private static byte[] writeThumbnail(JpegImageMetadata metadata) throws JpegAutorotateException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(metadata.getThumbnail(), "jpeg", baos);
            baos.flush();
            byte[] bytes = baos.toByteArray();
            baos.close();

            return bytes;
        } catch(IOException e) {
            throw new JpegAutorotateException("Unable to update JPEG image thumbnail.", e);
        }
    }

    /**
     * Writes the content of a {@code JpegImage} to a {@code byte[]}. Content written to {@code byte[]}
     * consists of rotated image and metadata. Metadata consists of general basic info,
     * {@code IPTC/Photoshop} and {@code XmpXml}.
     * <p>
     * {@code JpegImage} may contain {@code IPC/Photoshop} and/or {@code XmpXml} metadata.
     * </p>
     *
     * @param image
     *              An instance of {@code JpegImage}.
     * @return If successful, a {@code byte[]} containing the {@code JpegImage} data.
     * @throws JpegAutorotateException
     *              In the event, the {@code JpegImage} is unable to be read or written
     *              to a {@code byte[]}.
     */
    private static byte[] writeImage(JpegImage image) throws JpegAutorotateException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image.getImage(), "jpeg", baos);
            byte [] data = baos.toByteArray();
            baos.reset();

            // Update JPEG image metadata
            baos = new ByteArrayOutputStream();
            new ExifRewriter().updateExifMetadataLossless(data, baos, image.getMetadata().getOutputSet());
            data = baos.toByteArray();
            baos.reset();

            // Update IPTC/Photoshop metadata
            if(image.getMetadata().getPhotoshop() != null) {
                baos = new ByteArrayOutputStream();
                new JpegIptcRewriter().writeIPTC(data, baos, image.getMetadata().getPhotoshop().photoshopApp13Data);
                data = baos.toByteArray();
                baos.reset();
            }

            // Update XMP metadata
            if(image.getMetadata().getXmpXml() != null) {
                baos = new ByteArrayOutputStream();
                new JpegXmpRewriter().updateXmpXml(data, baos, image.getMetadata().getXmpXml());
                data = baos.toByteArray();
                baos.reset();
            }

            baos.flush();
            baos.close();

            return data;
        } catch(ImageWriteException | ImageReadException | IOException e) {
            throw new JpegAutorotateException("Unable to read/write rotated JPEG image to byte array.", e);
        }
    }

}