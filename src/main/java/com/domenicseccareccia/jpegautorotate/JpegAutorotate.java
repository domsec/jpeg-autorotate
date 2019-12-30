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

package com.domenicseccareccia.jpegautorotate;

import com.domenicseccareccia.jpegautorotate.imaging.JpegImageProcessor;
import com.domenicseccareccia.jpegautorotate.util.ImageUtils;

import java.io.*;

/**
 * Rotates JPEG images based on EXIF Orientation metadata tag.
 * <p>
 * This is the entry point for rotating JPEG images with JPEG Autorotate.
 * </p>
 * <p>
 * JPEG Autorotate applies the right EXIF {@code Orientation} metadata tag
 * to a JPEG image. More precisely, it:
 * <ul>
 * <li>Rotates image pixels</li>
 * <li>Rotates the thumbnail, if present</li>
 * <li>Sets the {@code Orientation} EXIF tag to 1 (default {@code Orientation} value)</li>
 * <li>Updates {@code EXIF} and {@code XMP} metadata values related to {@code Height}, {@code Width} and {@code Orientation}</li>
 * <li>Retains and does not alter other metadata tags</li>
 * <li>Currently does not support updating MakerNotes metadata</li>
 * </ul>
 * </p>
 * <p>
 * <dl>
 * <dt><b>Usage:</b></dt>
 * <dd>
 * The following example code demonstrates how to use the library to load a JPEG image by
 * either passing a {@code String} file path, {@code File} or {@code InputStream}, apply rotation,
 * update metadata and return the image as {@code byte[]}.
 * </p>
 * <p>
 * <pre>
 *      // File path
 *      byte[] image = JpegAutorotate.rotate("path/to/directory");
 *
 *      // OR
 *
 *      // File
 *      byte[] image = JpegAutorotate.rotate(new File("path/to/directory"));
 *
 *      // OR
 *
 *      // InputStream
 *      byte[] image = JpegAutorotate.rotate(new FileInputStream(new File("path/to/directory")));
 * </pre>
 * </dd>
 * </dl>
 * </p>
 * <p>
 * More information on the EXIF metadata is available at:
 *      https://www.daveperrett.com/articles/2012/07/28/exif-orientation-handling-is-a-ghetto/
 *      https://exiftool.org/TagNames/
 *      https://www.exiv2.org/tags.html
 * </p>
 * <p>
 * More information about this project is available at:
 *      https://github.com/domsec/jpeg-autorotate
 * </p>
 */
public final class JpegAutorotate {

    /**
     * Rotates a JPEG image, based on its EXIF {@code Orientation} metadata tag.
     * <p>
     * Applies the right {@code Orientation} to the {@code JpegImage} by rotating the pixels,
     * rotating the {@code ExifThumbnail} (if present), setting {@code Orientation} EXIF tag to 1,
     * updating {@code EXIF} and {@code XmpXml} metadata values and retaining all other metadata tags.
     *
     * @param path
     *            {@code String} path to a JPEG image file.
     * @return If successful, a {@code byte[]} containing JPEG image data.
     * @throws JpegAutorotateException
     *              In the event the JPEG file either does not contain the
     *              appropriate {@code EXIF} metadata, is not an acceptable file type,
     *              or is unable to be read
     * @throws FileNotFoundException
     *              In the event the JPEG file does not exist.
     */
    public static byte[] rotate(final String path) throws JpegAutorotateException, FileNotFoundException {
        return rotate(new File(path));
    }

    /**
     * Rotates a JPEG image, based on its EXIF {@code Orientation} metadata tag.
     * <p>
     * Applies the right {@code Orientation} to the {@code JpegImage} by rotating the pixels,
     * rotating the {@code ExifThumbnail} (if present), setting {@code Orientation} EXIF tag to 1,
     * updating {@code EXIF} and {@code XmpXml} metadata values and retaining all other metadata tags.
     *
     * @param file
     *            {@code File} containing a JPEG image file.
     * @return If successful, a {@code byte[]} containing JPEG image data.
     * @throws JpegAutorotateException
     *              In the event the JPEG file either does not contain the
     *              appropriate {@code EXIF} metadata, is not an acceptable file type,
     *              or is unable to be read
     * @throws FileNotFoundException
     *              In the event the JPEG file does not exist.
     */
    public static byte[] rotate(final File file) throws JpegAutorotateException, FileNotFoundException {
        if (!ImageUtils.isAcceptableImage(file)) {
            throw new JpegAutorotateException("File is not compatible, must be a JPEG image.");
        }

        if (!file.isFile() && !file.exists()) {
            throw new FileNotFoundException("JPEG file does not exist.");
        }

        try {
            byte[] bytes = ImageUtils.writeImageToBytes(new FileInputStream(file));

            return JpegImageProcessor.process(bytes);
        } catch (IOException e) {
            throw new JpegAutorotateException("Unable to read JPEG file.", e);
        }
    }

    /**
     * Rotates a JPEG image, based on its EXIF {@code Orientation} metadata tag.
     * <p>
     * Applies the right {@code Orientation} to the {@code JpegImage} by rotating the pixels,
     * rotating the {@code ExifThumbnail} (if present), setting {@code Orientation} EXIF tag to 1,
     * updating {@code EXIF} and {@code XmpXml} metadata values and retaining all other metadata tags.
     *
     * @param inputStream
     *              {@code InputStream} containing a JPEG image file.
     * @return If successful, a {@code byte[]} containing JPEG image data.
     * @throws JpegAutorotateException
     *              In the event the JPEG file either does not contain the
     *              appropriate {@code EXIF} metadata, is not an acceptable file type,
     *              is unable to be read, or does not exist.
     */
    public static byte[] rotate(final InputStream inputStream) throws JpegAutorotateException {
        byte[] bytes = ImageUtils.writeImageToBytes(inputStream);

        if (!ImageUtils.isAcceptableImage(bytes)) {
            throw new JpegAutorotateException("InputStream file is not compatible, must be a JPEG image.");
        }

        return JpegImageProcessor.process(bytes);
    }

}