package de.matrixweb.vfs.wrapped;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author marwol
 */
public class MergingVFS implements WrappedSystem {

  private final List<WrappedSystem> merged;

  /**
   * @param systems
   */
  public MergingVFS(final WrappedSystem... systems) {
    this(Arrays.asList(systems));
  }

  /**
   * @param systems
   */
  public MergingVFS(final List<WrappedSystem> systems) {
    this.merged = systems;
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#getName()
   */
  @Override
  public String getName() {
    List<String> names = new ArrayList<String>();
    for (final WrappedSystem system : this.merged) {
      names.add(system.getName());
    }
    return names.toString();
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#exists()
   */
  @Override
  public boolean exists() {
    boolean exists = true;
    for (final WrappedSystem system : this.merged) {
      exists &= system.exists();
    }
    return exists;
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#isDirectory()
   */
  @Override
  public boolean isDirectory() {
    boolean isDirectory = true;
    for (final WrappedSystem system : this.merged) {
      isDirectory &= system.isDirectory();
    }
    return isDirectory;
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#list()
   */
  @Override
  public List<WrappedSystem> list() {
    final List<WrappedSystem> list = new ArrayList<WrappedSystem>();
    for (final WrappedSystem system : this.merged) {
      list.addAll(system.list());
    }
    return list;
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#lastModified()
   */
  @Override
  public long lastModified() {
    throw new UnsupportedOperationException();
  }

  /**
   * @see de.matrixweb.vfs.wrapped.WrappedSystem#getInputStream()
   */
  @Override
  public InputStream getInputStream() throws IOException {
    throw new UnsupportedOperationException();
  }

}
