package de.matrixweb.vfs.wrapped;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.matrixweb.vfs.VFile;

/**
 * @author markusw
 */
public class WrappedVFS implements WrappedSystem {

  private final VFile file;

  /**
   * @param file
   */
  public WrappedVFS(final VFile file) {
    this.file = file;
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#getName()
   */
  @Override
  public String getName() {
    return this.file.getName();
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#exists()
   */
  @Override
  public boolean exists() {
    return this.file.exists();
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#isDirectory()
   */
  @Override
  public boolean isDirectory() {
    return this.file.isDirectory();
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#list()
   */
  @Override
  public List<WrappedSystem> list() {
    final List<WrappedSystem> list = new ArrayList<WrappedSystem>();
    for (final VFile child : this.file.getChildren()) {
      list.add(new WrappedVFS(child));
    }
    return list;
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#lastModified()
   */
  @Override
  public long lastModified() {
    return this.file.getLastModified();
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#getInputStream()
   */
  @Override
  public InputStream getInputStream() throws IOException {
    return this.file.getInputStream();
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.file.toString();
  }

}
