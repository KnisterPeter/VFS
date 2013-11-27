package de.matrixweb.vfs.scanner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.matrixweb.vfs.VFS;
import de.matrixweb.vfs.VFile;

/**
 * @author markusw
 */
public class VFSResourceLister implements ResourceLister {

  private final VFS vfs;

  /**
   * @param vfs
   */
  public VFSResourceLister(final VFS vfs) {
    super();
    this.vfs = vfs;
  }

  /**
   * @see de.matrixweb.vfs.scanner.ResourceLister#list(java.lang.String)
   */
  @Override
  public Set<String> list(String path) {
    if (path.endsWith("/") && path.length() > 1) {
      path = path.substring(0, path.length() - 1);
    }
    final Set<String> list = new HashSet<String>();
    try {
      for (final VFile file : this.vfs.find(path).getChildren()) {
        list.add(file.getPath());
      }
    } catch (final IOException e) {
      // Ignore this
    }
    return list;
  }

}
