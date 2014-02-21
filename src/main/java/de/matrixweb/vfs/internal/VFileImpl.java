package de.matrixweb.vfs.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.matrixweb.vfs.VFile;
import de.matrixweb.vfs.wrapped.WrappedSystem;

/**
 * @author markusw
 */
public class VFileImpl implements VFile {

  private final VFileImpl parent;

  private final String name;

  private boolean directory = false;

  private List<VFile> children;

  private int length = 0;

  private byte[] content;

  private long lastModified;

  private WrappedSystem resolvedFile = null;

  /**
   * @param parent
   * @param name
   */
  public VFileImpl(final VFileImpl parent, final String name) {
    this.parent = parent;
    this.name = name;
    if (parent != null) {
      parent.addChild(this);
    }
  }

  private Root getRoot() {
    VFileImpl f = this;
    while (true) {
      if (f == f.getParent()) {
        return (Root) f;
      }
      f = f.getParent();
    }
  }

  private List<WrappedSystem> searchWrappedParents() {
    final List<WrappedSystem> parents = new ArrayList<WrappedSystem>();

    final Entry<VFile, WrappedSystem> mount = getRoot().getMount(this);
    if (mount != null) {
      final WrappedSystem mountRoot = mount.getValue();
      if (mount.getKey().equals(this)) {
        parents.add(mount.getValue());
      } else {
        String relativePath = getPath().substring(
            mount.getKey().getPath().length());
        if (relativePath.endsWith("/")) {
          relativePath = relativePath.substring(0, relativePath.length() - 1);
        }
        final String[] path = relativePath.split("/", 2);
        parents.addAll(findWrappedParentsByPath(mountRoot, path));
      }
    }

    return parents;
  }

  private List<WrappedSystem> findWrappedParentsByPath(
      final WrappedSystem candidate, final String[] path) {
    final List<WrappedSystem> parents = new ArrayList<WrappedSystem>();
    for (final WrappedSystem child : candidate.list()) {
      if (child.isDirectory() && child.getName().equals(path[0])) {
        if (path.length > 1) {
          parents
              .addAll(findWrappedParentsByPath(child, path[1].split("/", 2)));
        } else {
          parents.add(child);
        }
      }
    }
    return parents;
  }

  /**
   * @param directory
   */
  public void mount(final WrappedSystem directory) {
    this.directory = true;
    getRoot().mount(this, directory);
    final List<WrappedSystem> parents = searchWrappedParents();
    for (final WrappedSystem parent : parents) {
      for (final WrappedSystem child : parent.list()) {
        new VFileImpl(this, child.getName()).setResolvedFile(child);
      }
    }
  }

  private void addChild(final VFileImpl child) {
    this.directory = true;
    final List<VFile> children = getChildren();
    if (!children.contains(child)) {
      this.children.add(child);
    }
  }

  private void setResolvedFile(final WrappedSystem resolvedFile) {
    this.resolvedFile = resolvedFile;
    this.directory = resolvedFile.isDirectory();
  }

  private WrappedSystem getResolvedFile() {
    if (this.resolvedFile == null && !isDirectory()) {
      final List<WrappedSystem> parents = searchWrappedParents();
      for (final WrappedSystem parent : parents) {
        for (final WrappedSystem child : parent.list()) {
          if (child.getName().equals(getName())) {
            // TODO: Event in case of directory???
            this.resolvedFile = child;
            break;
          }
        }
      }
    }
    return this.resolvedFile;
  }

  /**
   * @see de.matrixweb.vfs.VFile#getName()
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * @see de.matrixweb.vfs.VFile#getPath()
   */
  @Override
  public String getPath() {
    return this.parent.getPath() + getName() + (isDirectory() ? '/' : "");
  }

  /**
   * @see de.matrixweb.vfs.VFile#getURL()
   */
  @Override
  public URL getURL() {
    return getRoot().getVfs().createUrl(this);
  }

  /**
   * @see de.matrixweb.vfs.VFile#exists()
   */
  @Override
  public boolean exists() {
    if (isDirectory() && getChildren().size() > 0 || this.content != null
        && this.content.length > 0) {
      return true;
    }
    final WrappedSystem wrapped = getResolvedFile();
    if (wrapped != null) {
      return wrapped.exists();
    }
    return false;
  }

  /**
   * @see de.matrixweb.vfs.VFile#isDirectory()
   */
  @Override
  public boolean isDirectory() {
    return this.directory;
  }

  /**
   * @see de.matrixweb.vfs.VFile#getParent()
   */
  @Override
  public VFileImpl getParent() {
    return this.parent;
  }

  /**
   * @see de.matrixweb.vfs.VFile#getChildren()
   */
  @Override
  public List<VFile> getChildren() {
    if (this.children == null) {
      this.children = new ArrayList<VFile>();
      final List<WrappedSystem> parents = searchWrappedParents();
      for (final WrappedSystem parent : parents) {
        for (final WrappedSystem child : parent.list()) {
          new VFileImpl(this, child.getName()).setResolvedFile(child);
        }
      }
    }
    return this.children;
  }

  /**
   * @see de.matrixweb.vfs.VFile#getInputStream()
   */
  @Override
  public InputStream getInputStream() throws IOException {
    if (isDirectory()) {
      throw new IOException("Unable to read from directory");
    }
    if (!exists()) {
      throw new IOException("VFile '" + getPath() + "' does not exists");
    }
    final WrappedSystem wrapped = getResolvedFile();
    if (wrapped != null && wrapped.lastModified() > this.lastModified) {
      return wrapped.getInputStream();
    }
    return new FileInputStream(this);
  }

  /**
   * @see de.matrixweb.vfs.VFile#getOutputStream()
   */
  @Override
  public OutputStream getOutputStream() throws IOException {
    if (isDirectory()) {
      throw new IOException("Unable to write to directory");
    }
    return new FileOutputStream(this);
  }

  /**
   * @see de.matrixweb.vfs.VFile#find(java.lang.String)
   */
  @Override
  public VFile find(final String path) throws IOException {
    if (path.startsWith("/")) {
      return getRoot().getVfs().find(path);
    }
    VFile match = null;
    final String[] parts = path.split("/", 2);
    if ("..".equals(parts[0])) {
      match = getParent();
    } else if (".".equals(parts[0])) {
      match = this;
    } else {
      for (final VFile child : getChildren()) {
        if (child.getName().equals(parts[0])) {
          match = child;
        }
      }
    }
    if (match == null) {
      match = new VFileImpl(this, parts[0]);
    }
    if (parts.length > 1) {
      match = match.find(parts[1]);
    }
    return match;
  }

  /**
   * @see de.matrixweb.vfs.VFile#mkdir()
   */
  @Override
  public void mkdir() throws IOException {
    this.directory = true;
  }

  /**
   * @see de.matrixweb.vfs.VFile#getLastModified()
   */
  @Override
  public long getLastModified() {
    final WrappedSystem wrapped = getResolvedFile();
    return wrapped != null ? Math
        .max(wrapped.lastModified(), this.lastModified) : this.lastModified;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.name == null ? 0 : this.name.hashCode());
    result = prime * result
        + (this.parent == null ? 0 : this.parent.hashCode());
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final VFileImpl other = (VFileImpl) obj;
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    if (this.parent == null) {
      if (other.parent != null) {
        return false;
      }
    } else if (!this.parent.equals(other.parent)) {
      return false;
    }
    return true;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getPath();
  }

  private static class FileInputStream extends InputStream {

    private final VFileImpl file;

    private int counter = 0;

    FileInputStream(final VFileImpl file) {
      this.file = file;
    }

    @Override
    public int read() throws IOException {
      if (this.file.content == null) {
        throw new IOException("none-existent");
      }
      if (this.counter == this.file.length) {
        return -1;
      }
      return this.file.content[this.counter++];
    }

  }

  private static class FileOutputStream extends OutputStream {

    private final VFileImpl file;

    private int length = 0;

    private byte[] data = new byte[1024];

    FileOutputStream(final VFileImpl file) {
      this.file = file;
    }

    private void extend(final int n) {
      if (this.length + n > this.data.length) {
        final byte[] temp = this.data;
        this.data = new byte[this.data.length + Math.max(n, 1024)];
        System.arraycopy(temp, 0, this.data, 0, this.length);
      }
    }

    @Override
    public void write(final int b) throws IOException {
      extend(1);
      this.data[this.length++] = (byte) b;
    }

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(final byte[] b) throws IOException {
      extend(b.length);
      System.arraycopy(b, 0, this.data, this.length, b.length);
      this.length += b.length;
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len)
        throws IOException {
      extend(len);
      System.arraycopy(b, off, this.data, this.length, len);
      this.length += len;
    }

    @Override
    public void close() throws IOException {
      this.file.length = this.length;
      this.file.content = new byte[this.length];
      System.arraycopy(this.data, 0, this.file.content, 0, this.length);
      this.file.lastModified = System.currentTimeMillis();
    }

  }

}
