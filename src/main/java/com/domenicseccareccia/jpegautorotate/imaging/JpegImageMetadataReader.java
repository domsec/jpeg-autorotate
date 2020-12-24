/**
 * Copyright (c) 2019-2021 Domenic Seccareccia and contributors
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
import org.apache.commons.imaging.Imaging;

import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.IOException;

final class JpegImageMetadataReader {

    private JpegImageMetadataReader() {
        throw new IllegalStateException("Not intended for instantiation");
    }

    /**
     * Attempts to read {@code JpegImageMetadata}.
     *
     * @param bytes
     *              {@code bytes} containing a JPEG image file.
     * @return If successful, a valid {@code JpegImageMetadata} instance.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImageMetadata} either does not
     *              exist or is unable to be read.
     */
    protected static org.apache.commons.imaging.formats.jpeg.JpegImageMetadata readRawMetadata(final byte[] bytes) throws JpegAutorotateException {
        try {
            org.apache.commons.imaging.common.ImageMetadata imageMetadata = Imaging.getMetadata(bytes);

            if (imageMetadata == null) {
                throw new JpegAutorotateException("JPEG image does not have necessary metadata.");
            }

            return (org.apache.commons.imaging.formats.jpeg.JpegImageMetadata) imageMetadata;
        } catch (IOException | ImageReadException e) {
            throw new JpegAutorotateException("Unable to read JPEG image metadata.", e);
        }
    }

    /**
     * Attempts to read {@code TiffImageMetadata}.
     *
     * @param metadata
     *              An instance of {@code JpegImageMetadata}.
     * @return If successful, a valid {@code TiffImageMetadata} instance.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImageMetadata} is unable to be read.
     */
    protected static TiffImageMetadata readExif(final org.apache.commons.imaging.formats.jpeg.JpegImageMetadata metadata) throws JpegAutorotateException {
        if (metadata.getExif() == null) {
            throw new JpegAutorotateException("Unable to read JPEG image EXIF metadata.");
        }

        return metadata.getExif();
    }

    /**
     * Attempts to read {@code JpegImage} {@code TIFFOutputSet} from the {@code TiffImageMetadata}.
     * <p>
     * In the event the {@code JpegImage} does not contain a {@code TiffOutputSet}, it will be created.
     * </p>
     *
     * @param exif
     *              An instance of {@code TiffImageMetadata}.
     * @return If successful, a valid {@code TiffOutputSet} instance.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} {@code TiffImageMetadata} is unable to be read.
     */
    protected static TiffOutputSet readOutputSet(final TiffImageMetadata exif) throws JpegAutorotateException{
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
     * Attempts to read {@code JpegImage} {@code ICCProfile}.
     *
     * @param bytes
     *              {@code bytes} containing a JPEG image file.
     * @return If successful, a valid {@code ICCProfile} instance.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} {@code ICCProfile} metadata is unable to be read.
     */
    protected static ICC_Profile readIccProfile(final byte[] bytes) throws JpegAutorotateException {
        try {
            return Imaging.getICCProfile(bytes);
        } catch (ImageReadException | IOException e) {
            throw new JpegAutorotateException("Unable to read JPEG image ICC Profile metadata.", e);
        }
    }

    /**
     * Attempts to read {@code JpegImage} {@code XmpXml) metadata.
     *
     * @param bytes
     *              {@code bytes} containing a JPEG image file.
     * @return If successful, a valid {@code XmpXml} metadata String. Otherwise, returns null.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} {@code XmpXml} metadata is unable to be read.
     */
    protected static String readXmpXml(final byte[] bytes) throws JpegAutorotateException {
      try {
          return Imaging.getXmpXml(bytes);
      } catch (IOException | ImageReadException e) {
          throw new JpegAutorotateException("Unable to read JPEG image XMP metadata.", e);
      }
    }

    /**
     * Attempts to read {@code ExifThumbnail} from {@code JpegImageMetadata}.
     *
     * @param metadata
     *              An instance of {@code JpegImageMetadata}.
     * @return If successful, a {@code BufferedImage} containing the thumbnail image data.
     *         Otherwise, returns null.
     * @throws JpegAutorotateException
     *              In the event the {@code JpegImage} {@code ExifThumbnail} is unable to be read.
     */
    protected static BufferedImage readThumbnail(final org.apache.commons.imaging.formats.jpeg.JpegImageMetadata metadata) throws JpegAutorotateException {
        try {
            return metadata.getEXIFThumbnail();
        } catch (ImageReadException | IOException e) {
            throw new JpegAutorotateException("Unable to read JPEG image thumbnail.", e);
        }
    }

}
