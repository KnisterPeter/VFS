package de.matrixweb.vfs.internal;

import de.matrixweb.vfs.VFS;
import de.matrixweb.vfs.VFile;

/**
 * @author markusw
 */
public class Root extends VFileImpl {

  private final VFS vfs;

  /**
   * @param vfs
   */
  public Root(final VFS vfs) {
    super(null, "/", true);
    this.vfs = vfs;
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
  public VFile getParent() {
    return this;
  }

  /**
   * @return the vfs
   */
  VFS getVFS() {
    return this.vfs;
  }

}
