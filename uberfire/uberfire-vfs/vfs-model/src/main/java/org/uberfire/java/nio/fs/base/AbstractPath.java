/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.base;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.ClosedWatchServiceException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.kie.commons.validation.PortablePreconditions.*;
import static org.kie.commons.validation.Preconditions.*;
import static org.uberfire.java.nio.file.WatchEvent.*;

public abstract class AbstractPath<FS extends FileSystem> implements Path, AttrHolder<BasicFileAttributes> {

    public static final Pattern WINDOWS_DRIVER = Pattern.compile("^/?[A-Z|a-z]+(:).*");
    public static final String DEFAULT_WINDOWS_DRIVER = "C:";

    protected final FS fs;
    protected final boolean usesWindowsFormat;

    protected final boolean isAbsolute;
    protected final byte[] path;
    protected final List<Pair> offsets = new ArrayList<Pair>();
    protected final boolean isRoot;
    protected final boolean isRealPath;
    protected final boolean isNormalized;
    protected final String host;

    protected String toStringFormat;
    protected File file;
    protected BasicFileAttributes attrs;

    protected abstract Path newPath(FS fs, String substring, String host, boolean realPath, boolean isNormalized);

    protected abstract Path newRoot(FS fs, String substring, String host, boolean realPath);

    protected AbstractPath(final FS fs, final File file) {
        this(checkNotNull("fs", fs), checkNotNull("file", file).getAbsolutePath(), "", false, false, true);
    }

    protected AbstractPath(final FS fs, final String path, final String host, boolean isRoot, boolean isRealPath, boolean isNormalized) {
        checkNotNull("path", path);
        this.fs = checkNotNull("fs", fs);
        this.host = checkNotNull("host", host);
        this.isRealPath = isRealPath;
        this.isNormalized = isNormalized;
        this.usesWindowsFormat = path.matches(".*\\\\.*");

        final RootInfo rootInfo = setupRoot(fs, path, host, isRoot);
        this.path = rootInfo.path;

        checkNotNull("rootInfo", rootInfo);

        this.isAbsolute = rootInfo.isAbsolute;

        int lastOffset = rootInfo.startOffset;
        for (int i = lastOffset; i < this.path.length; i++) {
            final byte b = this.path[i];
            if (b == getSeparator()) {
                offsets.add(new Pair(lastOffset, i));
                i++;
                lastOffset = i;
            }
        }

        if (lastOffset < this.path.length) {
            offsets.add(new Pair(lastOffset, this.path.length));
        }

        this.isRoot = rootInfo.isRoot;
    }

    protected abstract RootInfo setupRoot(final FS fs, final String path, final String host, final boolean isRoot);

    @Override
    public FS getFileSystem() {
        return fs;
    }

    @Override
    public boolean isAbsolute() {
        return isAbsolute;
    }

    @Override
    public Path getRoot() {
        if (isRoot) {
            return this;
        }
        if (isAbsolute || !host.isEmpty()) {
            return newRoot(fs, substring(-1), host, isRealPath);
        }
        return null;
    }

    private String substring(int index) {
        final byte[] result;
        if (index == -1) {
            result = new byte[offsets.get(0).getA()];
            System.arraycopy(path, 0, result, 0, result.length);
        } else {
            final Pair offset = offsets.get(index);
            result = new byte[offset.getB() - offset.getA()];
            System.arraycopy(path, offset.getA(), result, 0, result.length);
        }

        return new String(result);
    }

    private String substring(int beginIndex, int endIndex) {
        final int initPos;
        if (beginIndex == -1) {
            initPos = 0;
        } else {
            initPos = offsets.get(beginIndex).getA();
        }
        final Pair offsetEnd = offsets.get(endIndex);
        byte[] result = new byte[offsetEnd.getB() - initPos];
        System.arraycopy(path, initPos, result, 0, result.length);

        return new String(result);
    }

    @Override
    public Path getFileName() {
        if (getNameCount() == 0) {
            return null;
        }
        return getName(getNameCount() - 1);
    }

    @Override
    public Path getParent() {
        if (getNameCount() <= 0) {
            return null;
        }
        if (getNameCount() == 1) {
            return getRoot();
        }
        return newPath(fs, substring(-1, getNameCount() - 2), host, isRealPath, isNormalized);
    }

    @Override
    public int getNameCount() {
        return offsets.size();
    }

    @Override
    public Path getName(int index) throws IllegalArgumentException {
        if (isRoot && index > 0) {
            throw new IllegalArgumentException();
        }
        if (index < 0) {
            throw new IllegalArgumentException();
        }
        if (index >= offsets.size()) {
            throw new IllegalArgumentException();
        }

        return newPath(fs, substring(index), host, isRealPath, false);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) throws IllegalArgumentException {
        if (beginIndex < 0) {
            throw new IllegalArgumentException();
        }
        if (beginIndex >= offsets.size()) {
            throw new IllegalArgumentException();
        }
        if (endIndex > offsets.size()) {
            throw new IllegalArgumentException();
        }
        if (beginIndex >= endIndex) {
            throw new IllegalArgumentException();
        }

        return newPath(fs, substring(beginIndex, endIndex - 1), host, isRealPath, false);
    }

    @Override
    public URI toUri() throws IOException, SecurityException {
        if (!isAbsolute()) {
            return toAbsolutePath().toUri();
        }
        if (fs.provider().isDefault() && !isRealPath) {
            try {
                return new URI("default", host, toURIString(), null);
            } catch (URISyntaxException e) {
                return null;
            }
        }
        try {
            return new URI(fs.provider().getScheme(), host, toURIString(), null);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private String toURIString() {
        if (usesWindowsFormat) {
            return "/" + toString().replace("\\", "/");
        }
        return new String(path);
    }

    @Override
    public Path toAbsolutePath() throws IOException, SecurityException {
        if (isAbsolute()) {
            return this;
        }
        if (host.isEmpty()) {
            return newPath(fs, FilenameUtils.normalize(defaultDirectory() + toString(), !usesWindowsFormat), host, isRealPath, true);
        }
        return newPath(fs, defaultDirectory() + toString(false), host, isRealPath, true);
    }

    protected abstract String defaultDirectory();

    @Override
    public Path toRealPath(final LinkOption... options)
            throws IOException, SecurityException {
        if (isRealPath) {
            return this;
        }
        return newPath(fs, FilenameUtils.normalize(toString(), !usesWindowsFormat), host, true, true);
    }

    @Override
    public File toFile()
            throws UnsupportedOperationException {
        if (file == null) {
            synchronized (this) {
                file = new File(toString());
            }
        }
        return file;
    }

    @Override
    public Iterator<Path> iterator() {
        return new Iterator<Path>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < getNameCount();
            }

            @Override
            public Path next() {
                if (i < getNameCount()) {
                    Path result = getName(i);
                    i++;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean startsWith(final Path other) {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startsWith(final String other) throws InvalidPathException {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean endsWith(final Path other) {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean endsWith(final String other) throws InvalidPathException {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public Path normalize() {
        if (isNormalized) {
            return this;
        }

        return newPath(fs, FilenameUtils.normalize(new String(path), !usesWindowsFormat), host, isRealPath, true);
    }

    @Override
    public Path resolve(final Path other) {
        checkNotNull("other", other);
        if (other.isAbsolute()) {
            return other;
        }
        if (other.toString().trim().length() == 0) {
            return this;
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(new String(path));
        if (path[path.length - 1] != getSeparator()) {
            sb.append(getSeparator());
        }
        sb.append(other.toString());

        return newPath(fs, sb.toString(), host, isRealPath, false);
    }

    @Override
    public Path resolve(final String other) throws InvalidPathException {
        checkNotNull("other", other);
        return resolve(newPath(fs, other, host, isRealPath, false));
    }

    @Override
    public Path resolveSibling(final Path other) {
        checkNotNull("other", other);

        final Path parent = this.getParent();
        if (parent == null || other.isAbsolute()) {
            return other;
        }

        return parent.resolve(other);
    }

    @Override
    public Path resolveSibling(final String other) throws InvalidPathException {
        checkNotNull("other", other);

        return resolveSibling(newPath(fs, other, host, isRealPath, false));
    }

    @Override
    public Path relativize(final Path otherx) throws IllegalArgumentException {
        checkNotNull("otherx", otherx);
        final AbstractPath other = checkInstanceOf("otherx", otherx, AbstractPath.class);

        if (this.equals(other)) {
            return emptyPath();
        }

        if (isAbsolute() != other.isAbsolute()) {
            throw new IllegalArgumentException();
        }

        if (isAbsolute() && !this.getRoot().equals(other.getRoot())) {
            throw new IllegalArgumentException();
        }

        if (this.path.length == 0) {
            return other;
        }

        int n = (getNameCount() > other.getNameCount()) ? other.getNameCount() : getNameCount();
        int i = 0;
        while (i < n) {
            if (!this.getName(i).equals(other.getName(i))) {
                break;
            }
            i++;
        }

        int numberOfDots = getNameCount() - i;

        if (numberOfDots == 0 && i < other.getNameCount()) {
            return other.subpath(i, other.getNameCount());
        }

        final StringBuilder sb = new StringBuilder();
        while (numberOfDots > 0) {
            sb.append("..");
            if (numberOfDots > 1) {
                sb.append(getSeparator());
            }
            numberOfDots--;
        }

        if (i < other.getNameCount()) {
            if (sb.length() > 0) {
                sb.append(getSeparator());
            }
            sb.append(((AbstractPath<FS>) other.subpath(i, other.getNameCount())).toString(false));
        }

        return newPath(fs, sb.toString(), host, isRealPath, false);
    }

    private Path emptyPath() {
        return newPath(fs, "", host, isRealPath, true);
    }

    @Override
    public int compareTo(final Path other) {
        checkNotNull("other", other);
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers)
            throws UnsupportedOperationException, IllegalArgumentException,
            ClosedWatchServiceException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchKey register(WatchService watcher, Kind<?>... events)
            throws UnsupportedOperationException, IllegalArgumentException,
            ClosedWatchServiceException, IOException, SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        if (toStringFormat == null) {
            toStringFormat = toString(false);
        }
        return toStringFormat;
    }

    public String toString(boolean addHost) {
        if (!addHost || host.isEmpty()) {
            return new String(path);
        }
        if (isAbsolute) {
            return host + new String(path);
        } else {
            return host + ":" + new String(path);
        }
    }

    private char getSeparator() {
        if (usesWindowsFormat) {
            return '\\';
        }
        return fs.getSeparator().toCharArray()[0];
    }

    @Override
    public BasicFileAttributes getAttrs() {
        if (attrs == null) {
            this.attrs = newAttrs();
        }
        return attrs;
    }

    protected abstract BasicFileAttributes newAttrs();

    public void clearCache() {
        attrs = null;
        file = null;
    }

    @Override
    public boolean equals(final Object o) {
        checkNotNull("o", o);

        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractPath)) {
            return false;
        }

        AbstractPath other = (AbstractPath) o;

        if (isAbsolute != other.isAbsolute) {
            return false;
        }
        if (isRealPath != other.isRealPath) {
            return false;
        }
        if (isRoot != other.isRoot) {
            return false;
        }
        if (usesWindowsFormat != other.usesWindowsFormat) {
            return false;
        }
        if (!host.equals(other.host)) {
            return false;
        }
        if (!fs.equals(other.fs)) {
            return false;
        }

        if (!usesWindowsFormat && !Arrays.equals(path, other.path)) {
            return false;
        }

        if (usesWindowsFormat && !(new String(path).equalsIgnoreCase(new String(other.path)))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = fs != null ? fs.hashCode() : 0;
        result = 31 * result + (usesWindowsFormat ? 1 : 0);
        result = 31 * result + (isAbsolute ? 1 : 0);

        if (!usesWindowsFormat) {
            result = 31 * result + (path != null ? Arrays.hashCode(path) : 0);
        } else {
            result = 31 * result + (path != null ? new String(path).toLowerCase().hashCode() : 0);
        }

        result = 31 * result + (isRoot ? 1 : 0);
        result = 31 * result + (isRealPath ? 1 : 0);
        result = 31 * result + (isNormalized ? 1 : 0);
        return result;
    }

    public String getHost() {
        return host;
    }

    public boolean isRealPath() {
        return isRealPath;
    }

    private static class Pair {

        private final int a;
        private final int b;

        Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }

    public static class RootInfo {

        private final int startOffset;
        private final boolean isAbsolute;
        private final boolean isRoot;
        private final byte[] path;

        public RootInfo(int startOffset, boolean isAbsolute, boolean isRoot, byte[] path) {
            this.startOffset = startOffset;
            this.isAbsolute = isAbsolute;
            this.isRoot = isRoot;
            this.path = path;
        }
    }
}
