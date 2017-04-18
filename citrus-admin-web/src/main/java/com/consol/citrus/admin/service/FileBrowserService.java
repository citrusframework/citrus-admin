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

package com.consol.citrus.admin.service;

import com.consol.citrus.Citrus;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.Arrays;

/**
 * @author Christoph Deppisch, Martin Maher
 */
@Service
public class FileBrowserService {

    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * Gets all sub-folder names in give directory.
     * @param directory
     * @return
     */
    public String[] getFolders(File directory) {
        if (directory.exists()) {
            String[] files = directory.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.charAt(0) != '.' && new File(dir, name).isDirectory();
                }
            });


            if (files != null) {
                Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
                return files;
            } else {
                return new String[] {};
            }
        } else {
            throw new ApplicationRuntimeException("Could not open directory because it does not exist: " + directory);
        }
    }

    /**
     * Finds file in folder structure. When recursive also searches in sub-directories.
     * @param directory
     * @param filename
     * @param recursive
     * @return
     */
    public File findFileInPath(File directory, String filename, boolean recursive) {
        if (!directory.isDirectory()) {
            String msg = String.format("Expected a directory but instead got '%s'", directory.getAbsolutePath());
            throw new UnsupportedOperationException(msg);
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory() && recursive) {
                File returnedFile = findFileInPath(file, filename, recursive);
                if (returnedFile != null) {
                    return returnedFile;
                }
            } else {
                if (file.getName().equals(filename)) {
                    return file;
                }
            }
        }
        return null;
    }

    /**
     * Decodes file path url.
     * @param url
     * @param rootDirectory
     * @return
     */
    public String decodeDirectoryUrl(String url, String rootDirectory) {
        String directory;

        try {
            directory = URLDecoder.decode(url, Citrus.CITRUS_FILE_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new ApplicationRuntimeException("Unable to decode directory URL", e);
        }

        if (directory.equals("/")) {
            if (StringUtils.hasText(rootDirectory)) {
                directory = rootDirectory;
            } else {
                return "";
            }
        }

        if (!StringUtils.hasText(directory)) {
            return "";
        } else if (directory.charAt(directory.length() - 1) == '\\') {
            directory = directory.substring(0, directory.length() - 1) + "/";
        } else if (directory.charAt(directory.length() - 1) != '/') {
            directory += "/";
        }

        return directory;
    }

    /**
     * Convert all path separators in given path expression to system separator character.
     * @param path
     * @return
     */
    public String separatorsToSystem(String path) {
        if (path == null) {
            return null;
        }
        if (isSystemWindows()) {
            return separatorsToWindows(path);
        } else {
            return separatorsToUnix(path);
        }
    }

    /**
     * Convert all path separators to windows style separator.
     * @param path
     * @return
     */
    public String separatorsToWindows(String path) {
        if (path == null || path.indexOf(UNIX_SEPARATOR) == -1) {
            return path;
        }
        return path.replace(UNIX_SEPARATOR, WINDOWS_SEPARATOR);
    }

    /**
     * Convert all path separators to unix style separator.
     * @param path
     * @return
     */
    public String separatorsToUnix(String path) {
        if (path == null || path.indexOf(WINDOWS_SEPARATOR) == -1) {
            return path;
        }
        return path.replace(WINDOWS_SEPARATOR, UNIX_SEPARATOR);
    }

    private boolean isSystemWindows() {
        return File.separatorChar == WINDOWS_SEPARATOR;
    }
}
