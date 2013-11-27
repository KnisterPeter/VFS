package de.matrixweb.vfs.scanner;

import java.util.Set;

/**
 * @author markusw
 */
public interface ResourceLister {

  /**
   * Returns a set of child path for the given path. All returned paths must
   * start relative to the root of the implementing filesystem base directory.
   * Subdirectories must end with '/'.
   * 
   * @param path
   * @return Returns a set of child paths
   */
  Set<String> list(String path);

}
