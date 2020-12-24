/**
 * Copyright (c) 2019-2021 Domenic Seccareccia and contributors
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.domenicseccareccia.jpegautorotate.util;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageUtilsTest {

    private static final String JPEG_IMAGE = "src/test/resources/red_box.jpg";
    private static final String PNG_IMAGE = "src/test/resources/blue_box.png";

    @Test
    public void testIsJpeg() throws Exception {
        // JPEG Image
        try (InputStream is = new FileInputStream(JPEG_IMAGE)) {
            byte[] bytes = ImageUtils.toByteArray(is);

            assertTrue(ImageUtils.isJpeg(bytes));
        }

        // PNG Image
        try (InputStream is = new FileInputStream(PNG_IMAGE)) {
            byte[] bytes = ImageUtils.toByteArray(is);

            assertFalse(ImageUtils.isJpeg(bytes));
        }
    }

}
