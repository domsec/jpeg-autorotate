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

package com.domsec.imaging;

import com.domsec.JpegAutorotateException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

final class JpegImageReader {

    /**
     * Not intended for instantiation.
     */
    private JpegImageReader() {
        throw new IllegalStateException("Not intended for instantiation.");
    }

    /**
     * Attempts to read JPEG file to a BufferedImage.
     *
     * @param bytes
     *              {@code bytes} containing a JPEG image file.
     * @return If successful, a {@code BufferedImage} containing image data.
     * @throws JpegAutorotateException
     *              In the event the {@code bytes} is unable to be read.
     */
    protected static BufferedImage readImage(final byte[] bytes) throws JpegAutorotateException {
        try {
            File tempFile = File.createTempFile("tmp", "jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bytes);

            BufferedImage image = ImageIO.read(tempFile);

            fos.flush();
            fos.close();
            tempFile.deleteOnExit();

            return image;
        } catch (IOException e) {
            throw new JpegAutorotateException("Unable to read JPEG image.", e);
        }
    }

}