package com.runsidekick.agent.core.util;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.output.NullOutputStream;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

/**
 * Utility class for file related stuffs.
 *
 * @author serkan
 */
public final class FileUtils {

    private FileUtils() {
    }

    public static File getFile(File directory, String... names) {
        if (directory == null) {
            throw new NullPointerException("directory must not be null");
        } else if (names == null) {
            throw new NullPointerException("names must not be null");
        } else {
            File file = directory;
            for (String name : names) {
                file = new File(file, name);
            }
            return file;
        }
    }

    public static File getFile(String... names) {
        if (names == null) {
            throw new NullPointerException("names must not be null");
        } else {
            File file = null;
            for (String name : names) {
                if (file == null) {
                    file = new File(name);
                } else {
                    file = new File(file, name);
                }
            }
            return file;
        }
    }

    public static String getTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static File getTempDirectory() {
        return new File(getTempDirectoryPath());
    }

    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    public static File getUserHome() {
        return new File(getUserHomePath());
    }

    public static String getUserDirectoryPath() {
        return System.getProperty("user.dir");
    }

    public static File getUserDirectory() {
        return new File(getUserDirectoryPath());
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            } else if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            } else {
                return new FileInputStream(file);
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
    }

    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Directory '" + parent + "' could not be created");
            }
        }
        return new FileOutputStream(file, append);
    }

    public static void touch(File file) throws IOException {
        if (!file.exists()) {
            openOutputStream(file).close();
        }

        boolean success = file.setLastModified(System.currentTimeMillis());
        if (!success) {
            throw new IOException("Unable to set the last modification time for " + file);
        }
    }

    private static void innerListFiles(Collection<File> files, File directory, IOFileFilter filter, boolean includeSubDirectories) {
        File[] found = directory.listFiles((FileFilter) filter);
        if (found != null) {
            for (File file : found) {
                if (file.isDirectory()) {
                    if (includeSubDirectories) {
                        files.add(file);
                    }
                    innerListFiles(files, file, filter, includeSubDirectories);
                } else {
                    files.add(file);
                }
            }
        }
    }

    public static Collection<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        validateListFilesParameters(directory, fileFilter);
        IOFileFilter effFileFilter = setUpEffectiveFileFilter(fileFilter);
        IOFileFilter effDirFilter = setUpEffectiveDirFilter(dirFilter);
        Collection<File> files = new LinkedList();
        innerListFiles(files, directory, FileFilterUtils.or(new IOFileFilter[]{effFileFilter, effDirFilter}), false);
        return files;
    }

    private static void validateListFilesParameters(File directory, IOFileFilter fileFilter) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Parameter 'directory' is not a directory: " + directory);
        } else if (fileFilter == null) {
            throw new NullPointerException("Parameter 'fileFilter' is null");
        }
    }

    private static IOFileFilter setUpEffectiveFileFilter(IOFileFilter fileFilter) {
        return FileFilterUtils.and(new IOFileFilter[]{fileFilter, FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE)});
    }

    private static IOFileFilter setUpEffectiveDirFilter(IOFileFilter dirFilter) {
        return dirFilter == null ? FalseFileFilter.INSTANCE : FileFilterUtils.and(new IOFileFilter[]{dirFilter, DirectoryFileFilter.INSTANCE});
    }

    public static Collection<File> listFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        validateListFilesParameters(directory, fileFilter);
        IOFileFilter effFileFilter = setUpEffectiveFileFilter(fileFilter);
        IOFileFilter effDirFilter = setUpEffectiveDirFilter(dirFilter);
        Collection<File> files = new LinkedList();
        if (directory.isDirectory()) {
            files.add(directory);
        }
        innerListFiles(files, directory, FileFilterUtils.or(new IOFileFilter[]{effFileFilter, effDirFilter}), true);
        return files;
    }

    public static Iterator<File> iterateFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return listFiles(directory, fileFilter, dirFilter).iterator();
    }

    public static Iterator<File> iterateFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return listFilesAndDirs(directory, fileFilter, dirFilter).iterator();
    }

    private static String[] toSuffixes(String[] extensions) {
        String[] suffixes = new String[extensions.length];

        for (int i = 0; i < extensions.length; ++i) {
            suffixes[i] = "." + extensions[i];
        }

        return suffixes;
    }

    public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive) {
        Object filter;
        if (extensions == null) {
            filter = TrueFileFilter.INSTANCE;
        } else {
            String[] suffixes = toSuffixes(extensions);
            filter = new SuffixFileFilter(suffixes);
        }
        return listFiles(directory, (IOFileFilter)filter, recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
    }

    public static Iterator<File> iterateFiles(File directory, String[] extensions, boolean recursive) {
        return listFiles(directory, extensions, recursive).iterator();
    }

    public static String readFileToString(File file, Charset encoding) throws IOException {
        InputStream in = openInputStream(file);
        Throwable error = null;
        String content;
        try {
            content = org.apache.commons.io.IOUtils.toString(in, Charsets.toCharset(encoding));
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            if (in != null) {
                if (error != null) {
                    try {
                        in.close();
                    } catch (Throwable t) {
                        error.addSuppressed(t);
                    }
                } else {
                    in.close();
                }
            }

        }
        return content;
    }

    public static String readFileToString(File file, String encoding) throws IOException {
        return readFileToString(file, Charsets.toCharset(encoding));
    }

    public static String readFileToString(File file) throws IOException {
        return readFileToString(file, Charset.defaultCharset());
    }

    public static byte[] readFileToByteArray(File file) throws IOException {
        InputStream in = openInputStream(file);
        Throwable error = null;
        byte[] content;
        try {
            long fileLength = file.length();
            content = fileLength > 0L
                    ? org.apache.commons.io.IOUtils.toByteArray(in, fileLength)
                    : org.apache.commons.io.IOUtils.toByteArray(in);
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            if (in != null) {
                if (error != null) {
                    try {
                        in.close();
                    } catch (Throwable t) {
                        error.addSuppressed(t);
                    }
                } else {
                    in.close();
                }
            }

        }
        return content;
    }

    public static List<String> readLines(File file, Charset encoding) throws IOException {
        InputStream in = openInputStream(file);
        Throwable error = null;
        List<String> lines;
        try {
            lines = org.apache.commons.io.IOUtils.readLines(in, Charsets.toCharset(encoding));
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            if (in != null) {
                if (error != null) {
                    try {
                        in.close();
                    } catch (Throwable t) {
                        error.addSuppressed(t);
                    }
                } else {
                    in.close();
                }
            }

        }
        return lines;
    }

    public static List<String> readLines(File file, String encoding) throws IOException {
        return readLines(file, Charsets.toCharset(encoding));
    }

    public static List<String> readLines(File file) throws IOException {
        return readLines(file, Charset.defaultCharset());
    }

    public static LineIterator lineIterator(File file, String encoding) throws IOException {
        FileInputStream in = null;
        try {
            in = openInputStream(file);
            return org.apache.commons.io.IOUtils.lineIterator(in, encoding);
        } catch (RuntimeException | IOException e) {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e2) {
                e.addSuppressed(e2);
            }
            throw e;
        }
    }

    public static LineIterator lineIterator(File file) throws IOException {
        return lineIterator(file, null);
    }

    public static long checksumCRC32(File file) throws IOException {
        CRC32 crc = new CRC32();
        checksum(file, crc);
        return crc.getValue();
    }

    public static Checksum checksum(File file, Checksum checksum) throws IOException {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("Checksums can't be computed on directories");
        } else {
            InputStream in = new CheckedInputStream(new FileInputStream(file), checksum);
            Throwable error = null;
            try {
                org.apache.commons.io.IOUtils.copy(in, new NullOutputStream());
            } catch (Throwable t) {
                error = t;
                throw t;
            } finally {
                if (in != null) {
                    if (error != null) {
                        try {
                            in.close();
                        } catch (Throwable t) {
                            error.addSuppressed(t);
                        }
                    } else {
                        in.close();
                    }
                }
            }
            return checksum;
        }
    }

    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        } else {
            return Files.isSymbolicLink(file.toPath());
        }
    }

}
