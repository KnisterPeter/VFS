package de.matrixweb.vfs.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.matrixweb.vfs.VFS;
import de.matrixweb.vfs.VFile;
import de.matrixweb.vfs.wrapped.WrappedSystem;

/**
 * @author markusw
 */
public class Root extends VFileImpl {

  private final VFS vfs;

  private final Map<VFile, WrappedSystem> mounts = new HashMap<VFile, WrappedSystem>();

  /**
   * @param vfs
   */
  public Root(final VFS vfs) {
    super(null, "/");
    this.vfs = vfs;
  }

  /**
   * @param target
   * @param source
   */
  public void mount(final VFile target, final WrappedSystem source) {
    this.mounts.put(target, source);
  }

  Entry<VFile, WrappedSystem> getMount(final VFile file) {
    final String path = file.getPath();
    for (final Entry<VFile, WrappedSystem> mount : this.mounts.entrySet()) {
      if (path.startsWith(mount.getKey().getPath())) {
        return mount;
      }
    }
    return null;
  }

  /**
   * @see de.matrixweb.vfs.internal.VFileImpl#getPath()
   */
  @Override
  public String getPath() {
    return "/";
  }

  /**
   * @see de.matrixweb.vfs.internal.VFileImpl#getParent()
   */
  @Override
  public VFileImpl getParent() {
    return this;
  }

  /**
   * @return the vfs
   */
  public VFS getVfs() {
    return this.vfs;
  }

}
