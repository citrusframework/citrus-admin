/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.admin.service;

import com.consol.citrus.Citrus;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author Christoph Deppisch
 */
public class FileBrowserServiceTest {

    private FileBrowserService testling = new FileBrowserService();

    private File tmpDir = null;
    private File tmpFile = null;

    @BeforeTest
    public void setup() throws Exception {
        tmpDir = createRandomDirectory(getTmpDirectory());
        File tmpSubDir = createRandomDirectory(tmpDir);
        tmpFile = createTmpFile(tmpSubDir);
    }

    @AfterTest
    public void cleanup() throws Exception {
        FileUtils.deleteDirectory(tmpDir);
        tmpDir.delete();
    }

    @Test
    public void testGetFolders() throws Exception {
        String[] folders = testling.getFolders(new ClassPathResource("test-project").getFile());
        Assert.assertNotNull(folders);
        Assert.assertEquals(folders.length, 3);
        Assert.assertEquals(folders[0], "ant");
        Assert.assertEquals(folders[1], "maven");
        Assert.assertEquals(folders[2], "src");
    }

    @Test
    public void testFindFileInPath() throws Exception {
        File foundFile = testling.findFileInPath(tmpDir, tmpFile.getName(), true);
        Assert.assertNotNull(foundFile);
        Assert.assertEquals(foundFile.getAbsolutePath(), tmpFile.getAbsolutePath());

        foundFile = testling.findFileInPath(tmpDir, tmpFile.getName() + "_", true);
        Assert.assertNull(foundFile);
    }

    @Test
    public void testDecodeDirectoryUrl() throws Exception {
        String path = testling.decodeDirectoryUrl(URLEncoder.encode("/request/test/1", Citrus.CITRUS_FILE_ENCODING), "");
        Assert.assertEquals(path, "/request/test/1/");

        path = testling.decodeDirectoryUrl(URLEncoder.encode("/request/test/1", Citrus.CITRUS_FILE_ENCODING), "/Users/home");
        Assert.assertEquals(path, "/request/test/1/");

        path = testling.decodeDirectoryUrl(URLEncoder.encode("/", Citrus.CITRUS_FILE_ENCODING), "/Users/home");
        Assert.assertEquals(path, "/Users/home/");

        path = testling.decodeDirectoryUrl(URLEncoder.encode("", Citrus.CITRUS_FILE_ENCODING), "/Users/home");
        Assert.assertEquals(path, "");

        path = testling.decodeDirectoryUrl(URLEncoder.encode("/", Citrus.CITRUS_FILE_ENCODING), "");
        Assert.assertEquals(path, "");
    }

    @Test
    public void testSeparatorsToUnix() throws Exception {
        String path = testling.separatorsToUnix("C:\\windows\\system\\test");
        Assert.assertEquals(path, "C:/windows/system/test");
    }

    @Test
    public void testSeparatorsToWindows() throws Exception {
        String path = testling.separatorsToWindows("/Users/test/temp");
        Assert.assertEquals(path, "\\Users\\test\\temp");
    }

    private File createTmpFile(File rootDirectory) throws IOException {
        return File.createTempFile("abc",".xml", rootDirectory);
    }

    private File createRandomDirectory(File rootDirectory) throws IOException {
        File tmpDir = new File(rootDirectory, RandomStringUtils.randomAlphanumeric(8));
        if(tmpDir.mkdir()) {
            return tmpDir;
        }
        throw new RuntimeException(String.format("Could not create directory '%s'", tmpDir.getAbsolutePath()));
    }

    private File getTmpDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

}