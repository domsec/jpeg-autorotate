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

import java.awt.image.BufferedImage;

class JpegImage {

    private BufferedImage image;
    private JpegImageMetadata metadata;

    protected JpegImage(final byte[] bytes) throws JpegAutorotateException {
        this.image = JpegImageReader.readImage(bytes);
        this.metadata = new JpegImageMetadata(bytes);
    }

    protected void setImage(BufferedImage image) {
        this.image = image;
    }

    protected BufferedImage getImage() {
        return this.image;
    }

    protected JpegImageMetadata getMetadata() {
        return this.metadata;
    }

}
