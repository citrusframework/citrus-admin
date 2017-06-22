/*
 * Copyright 2006-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.admin.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author Christoph Deppisch
 */
public class InputStreamPumper implements Runnable, Closeable {

    /** Default line cache size */
    private static final int DEFAULT_LINE_CHACHE_SIZE = 30;

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(InputStreamReader.class);

    private final BufferedReader reader;
    private volatile boolean keepReading;
    private Thread thread;

    private final int lineCacheSize;
    private int lineCache;
    private StringBuilder outputCache = new StringBuilder();

    /**
     * Subclasses may add activity callback logic here.
     * @param line
     */
    public void onActivity(String line){
    }

    /**
     * Subclasses may add output logic here.
     * @param data
     */
    public void onOutput(String data) {
    }

    /**
     * Constructor using input stream. Uses default line cache size.
     * @param in
     */
    public InputStreamPumper(InputStream in) {
        this(in, DEFAULT_LINE_CHACHE_SIZE);
    }

    /**
     * Constructor using input stream.
     * @param in
     * @param lineCacheSize
     */
    public InputStreamPumper(InputStream in, int lineCacheSize) {
        this.lineCacheSize = lineCacheSize;
        this.keepReading = true;
        this.reader = new BufferedReader(new InputStreamReader(in));
    }

    /**
     * Open input stream and read continuously from it.
     */
    public void run() {
        this.thread = Thread.currentThread();

        try {
            lineCache = lineCacheSize;
            String line;

            while (this.keepReading && !Thread.currentThread().isInterrupted() && (line = reader.readLine()) != null) {
                onActivity(line);

                outputCache.append(line);
                outputCache.append(System.lineSeparator());

                if (lineCache > 0) {
                    lineCache--;
                } else {
                    onOutput(outputCache.toString());
                    lineCache = lineCacheSize;
                    outputCache = new StringBuilder();
                }
            }

            if (lineCache < lineCacheSize) {
                onOutput(outputCache.toString());
                lineCache = lineCacheSize;
                outputCache = new StringBuilder();
            }
        } catch (IOException e) {
            if (!this.keepReading) {
                return;
            }

            if (!this.thread.isInterrupted()) {
                LOG.error("Error while pumping stream.", e);
            } else {
                LOG.debug("Interrupted while pumping stream.", e);
            }
        }
    }

    /**
     * Close reading and input stream.
     */
    public void close() {
        this.keepReading = false;

        try {
            reader.close();
        } catch (IOException e) {
            // ignore
            LOG.warn("Failed to close input stream", e);
        }
    }
}
